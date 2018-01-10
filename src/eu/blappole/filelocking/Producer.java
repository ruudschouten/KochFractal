package eu.blappole.filelocking;

import eu.blappole.calculate.Edge;
import eu.blappole.calculate.KochFractal;
import eu.blappole.ui.FilenameGetter;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class Producer extends Application implements Observer {

    //From Console Mapped
    private String file = FilenameGetter.SYNCPATH;
    private int BYTEAMOUNT = 48;
    private RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
    private FileChannel fileChannel = randomAccessFile.getChannel();
    private MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, BYTEAMOUNT);
    private FileLock dataLock;
    private int edges;
    private int generatedEdges = 0;

    public Producer() throws IOException {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Also from Console
        Scanner scanner = new Scanner(System.in);
        boolean stop = false;
        while (!stop) {
            System.out.println("Select level to generate: (1-12)");
            int level = scanner.nextInt();
            while (level < 1 || level > 12) {
                System.out.printf("%d Level should be between 1 & 12%n", level);
                System.out.println("Select level to generate (1-12)");
                level = scanner.nextInt();
            }
            write(level);
        }
    }

    public void write(int level){
        //Also from console
        KochFractal koch = new KochFractal();
        koch.setLevel(level);
        edges = koch.getNrOfEdges();
        koch.addObserver(this);
        koch.generateRightEdge();
        koch.generateLeftEdge();
        koch.generateBottomEdge();
    }

    @Override
    public void update(Observable o, Object arg) {
        //Set written to false
        boolean written = false;
        //Loop until written is set to true, aka untill the file has been read by Consumer
        while (!written){
            //Before writing lock the file.
            try {
                dataLock = fileChannel.lock(0, 8, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Edge edge = (Edge) arg;
            //Get edge from mappedByteBuffer on pos 4
            //Starts at 4 because mappedByteBuffer starts with an int with the nr of edges
            mappedByteBuffer.position(4);
            int status = mappedByteBuffer.getInt();
            //Check if status = 0 to indicate this edge hasn't been written to and if an edge has already been added
            if(status == 0 || generatedEdges == 0){
                //Put pos back at 0 to overwrite the edges and the rest
                mappedByteBuffer.position(0);
                mappedByteBuffer.putInt(edges);
                //Put this at 1 to tell Consumer that this edge can be read
                mappedByteBuffer.putInt(1);
                //Put in the edge data
                mappedByteBuffer.putDouble(edge.X1);
                mappedByteBuffer.putDouble(edge.Y1);
                mappedByteBuffer.putDouble(edge.X2);
                mappedByteBuffer.putDouble(edge.Y2);
                mappedByteBuffer.putDouble(edge.hue);
                //update generatedEdges, this is done to check if this loop has created an edge
                generatedEdges++;
                written = true;
            }
            //release the lock after adding a edge
            try {
                dataLock.release();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
