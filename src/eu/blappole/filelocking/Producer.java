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
        boolean written = false;
        while (!written){
            try {
                dataLock = fileChannel.lock(0, 8, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Edge edge = (Edge) arg;
            mappedByteBuffer.position(4);
            int status = mappedByteBuffer.getInt();
            if(status == 0 || generatedEdges == 0){
                mappedByteBuffer.position(0);
                mappedByteBuffer.putInt(edges);
                mappedByteBuffer.putInt(1);
                mappedByteBuffer.putDouble(edge.X1);
                mappedByteBuffer.putDouble(edge.Y1);
                mappedByteBuffer.putDouble(edge.X2);
                mappedByteBuffer.putDouble(edge.Y2);
                mappedByteBuffer.putDouble(edge.hue);
                generatedEdges++;
                written = true;
            }
            try {
                dataLock.release();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
