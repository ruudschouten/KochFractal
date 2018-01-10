package eu.blappole.filelocking;

import eu.blappole.calculate.Edge;
import eu.blappole.ui.FilenameGetter;
import eu.blappole.ui.controllers.Controller;
import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;

public class Consumer implements Runnable {
    private String file = FilenameGetter.SYNCPATH;
    private FileLock dataLock = null;
    private int TOTALBYTES = 48;
    private RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
    private FileChannel fileChannel = randomAccessFile.getChannel();
    private MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 48);
    private int edgesWritten = 0;
    private List<Edge> edgeList = new ArrayList();
    private Controller controller;

    public Consumer(Controller controller) throws IOException {
        this.controller = controller;
    }

    @Override
    public void run() {
        boolean finished = false;
        edgesWritten = 0;
        while (!finished){
            //Lock the file
            try {
                dataLock = fileChannel.lock(0, TOTALBYTES, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mappedByteBuffer.position(0);
            //Get edges written to file
            int edges = mappedByteBuffer.getInt();
            int status = mappedByteBuffer.getInt();
            if(status == 1){
                //Pos at 8 to skip status and nr of edges
                mappedByteBuffer.position(8);
                //Get edge data and add it to a new Edge instance
                double x1 = mappedByteBuffer.getDouble();
                double y1 = mappedByteBuffer.getDouble();
                double x2 = mappedByteBuffer.getDouble();
                double y2 = mappedByteBuffer.getDouble();
                double hue = mappedByteBuffer.getDouble();
                Edge edge = new Edge(x1, y1, x2, y2, Color.hsb(hue, 1,1 ));
                edgeList.add(edge);
                mappedByteBuffer.position(4);
                //Set status to 0 to indicate that the edge has been read
                mappedByteBuffer.putInt(0);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        //Add the edges on the GUI
                        if(edgesWritten == 0) controller.clear();
                        controller.drawEdge(edge);
                        //Increase edges written
                        edgesWritten++;
                    }
                });

            }
            //Check if edges matches the amount of edgesWritten
            if(edges == edgesWritten)
                finished = true;
            //Release the lock
            try {
                dataLock.release();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Restart the process after finishing a fractal
        controller.reset();
    }
}
