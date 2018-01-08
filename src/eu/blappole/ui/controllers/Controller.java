package eu.blappole.ui.controllers;

import eu.blappole.calculate.Edge;
import eu.blappole.calculate.WatchKochRunnable;
import eu.blappole.timeutil.TimeStamp;
import eu.blappole.ui.FilenameGetter;
import eu.blappole.ui.Type;
import eu.blappole.ui.TypeGetter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.paint.Color;
import sun.nio.ch.DirectBuffer;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Controller {
    public ComboBox cbbKochLevels;
    public Canvas kochCanvas;

    private int level = 1;
    private String filename;
    private String lockedFilename;
    private Scanner scanner;
    private Scanner bufferedScanner;

    private ObjectInputStream objectInputStream;
    private FileInputStream fileInputStream;
    /*
    Change this for different types
     */
    private Type type = TypeGetter.TYPE;

    public void initialize() throws IOException {
        clearCanvas();
        filename = FilenameGetter.REALTIMEPATH;
        for (int i = 1; i <= 12; i++) {
            cbbKochLevels.getItems().add(i);
        }
        cbbKochLevels.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                level = (int) newValue;
            }
        });
        new Thread(new WatchKochRunnable(Paths.get(FilenameGetter.FILEPATH), this)).start();
    }

    public void loadKoch(ActionEvent actionEvent) throws IOException {
        clearCanvas();
//        filename = FilenameGetter.FILEPREFIX + level + FilenameGetter.FILETYPE;
        openReaders();

        TimeStamp time = new TimeStamp();
        time.setBegin("Start");

        switch (type) {
            case TEXT: readText(); break;
            case BINARY: readObject(); break;
            case MAPPED: for (Edge e : readMapped()) { drawEdge(e); } break;
            case BUFFEREDTEXT: readBufferedText(); break;
            case BUFFEREDBINARY: readBufferedObject(); break;
        }
        time.setEnd("End");
        System.out.println(time);
    }

    private void openReaders() throws IOException {
//        if(type == Type.BINARY || type == Type.BUFFEREDBINARY) {
        fileInputStream = new FileInputStream(filename);
        objectInputStream = new ObjectInputStream(fileInputStream);
//        }
//        else if (type == Type.TEXT || type == Type.BUFFEREDTEXT) {
        scanner = new Scanner(new FileInputStream(filename));
        bufferedScanner = new Scanner(new BufferedInputStream(new FileInputStream(filename)));
//        }
    }

    private ArrayList<Edge> readMapped() throws IOException {
        ArrayList<Edge> edges = new ArrayList<>();
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(filename, "r");
            FileChannel fileChannel = randomAccessFile.getChannel();
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, randomAccessFile.length());
            byte[] bytes = new byte[(int) randomAccessFile.length()];
            mappedByteBuffer.get(bytes);
            //Hierbij heb ik hulp gekregen van Bart en Tom.
            sun.misc.Cleaner cleaner = ((DirectBuffer) mappedByteBuffer).cleaner();
            cleaner.clean();
            ObjectInputStream objectInputStream = null;
            objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
            edges = (ArrayList<Edge>) objectInputStream.readObject();
            objectInputStream.close();
            randomAccessFile.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (randomAccessFile != null) randomAccessFile.close();
        }
        return edges;
    }

    private void readText() {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] entries = line.split(",");
            String x1 = entries[0];
            String x2 = entries[1];
            String y1 = entries[2];
            String y2 = entries[3];
            Edge e = new Edge(Double.valueOf(x1), Double.valueOf(y1), Double.valueOf(x2), Double.valueOf(y2), Color.WHITE);
            drawEdge(e);
        }
    }

    private void readBufferedText() {
        while (bufferedScanner.hasNextLine()) {
            String line = bufferedScanner.nextLine();
            String[] entries = line.split(",");
            String x1 = entries[0];
            String x2 = entries[1];
            String y1 = entries[2];
            String y2 = entries[3];
            Edge e = new Edge(Double.valueOf(x1), Double.valueOf(y1), Double.valueOf(x2), Double.valueOf(y2), Color.WHITE);
            drawEdge(e);
        }
    }

    private void readObject() {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
            Object obj = null;
            while ((obj = inputStream.readObject()) != null) {
                if (obj instanceof Edge) {
                    drawEdge((Edge) obj);
                }
            }
            inputStream.close();
        } catch (EOFException ex) {
            System.out.println("End of file reached");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void readBufferedObject() {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new BufferedInputStream(fileInputStream));
            Object obj = null;
            while ((obj = inputStream.readObject()) != null) {
                if (obj instanceof Edge) {
                    drawEdge((Edge) obj);
                }
            }
            inputStream.close();
        } catch (EOFException ex) {
            System.out.println("End of file reached");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void clearCanvas() {
        GraphicsContext gc = kochCanvas.getGraphicsContext2D();
        gc.clearRect(0.0, 0.0, 580, 580);
        gc.setFill(Color.BLACK);
        gc.fillRect(0.0, 0.0, 580, 580);
    }

    public void drawEdge(Edge e) {
        // Graphics
        GraphicsContext gc = kochCanvas.getGraphicsContext2D();

        // Set line getColor
        gc.setStroke(e.getColor());

        gc.setLineWidth(1.0);

        // Draw line
        e = resizeEdge(e);
        gc.strokeLine(e.X1, e.Y1, e.X2, e.Y2);
    }

    private Edge resizeEdge(Edge e) {
        int kpSize = Math.min(580, 580);
        double zoom = kpSize;
        double zoomTranslateX = (580 - kpSize) / 2.0;
        double zoomTranslateY = (580 - kpSize) / 2.0;
        return new Edge(
                e.X1 * zoom + zoomTranslateX,
                e.Y1 * zoom + zoomTranslateY,
                e.X2 * zoom + zoomTranslateX,
                e.Y2 * zoom + zoomTranslateY,
                e.getColor());
    }
}
