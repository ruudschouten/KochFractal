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
            try {
                dataLock = fileChannel.lock(0, TOTALBYTES, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mappedByteBuffer.position(0);
            int edges = mappedByteBuffer.getInt();
            int status = mappedByteBuffer.getInt();
            if(status == 1){
                mappedByteBuffer.position(8);
                double x1 = mappedByteBuffer.getDouble();
                double y1 = mappedByteBuffer.getDouble();
                double x2 = mappedByteBuffer.getDouble();
                double y2 = mappedByteBuffer.getDouble();
                double hue = mappedByteBuffer.getDouble();
                Edge edge = new Edge(x1, y1, x2, y2, Color.hsb(hue, 1,1 ));
                edgeList.add(edge);
                mappedByteBuffer.position(4);
                mappedByteBuffer.putInt(0);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if(edgesWritten == 0) controller.clear();
                        controller.drawEdge(edge);
                        edgesWritten++;
                    }
                });

            }
            if(edges == edgesWritten)
                finished = true;
            try {
                dataLock.release();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        controller.reset();
    }
}
