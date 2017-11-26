package eu.blappole.ui.controllers;

import eu.blappole.calculate.Edge;
import eu.blappole.timeutil.TimeStamp;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.paint.Color;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Controller {
    public ComboBox cbbKochLevels;
    public Canvas kochCanvas;

    private int level = 1;
    private String filename;
    private Scanner scanner;
    private Scanner bufferedScanner;

    private ObjectInputStream ois;
    private FileInputStream fis;

    private ArrayList<Edge> edges = new ArrayList<>();

    public void initialize() {
        clearCanvas();
        for (int i = 1; i <= 12; i++) {
            cbbKochLevels.getItems().add(i);
        }
        cbbKochLevels.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                level = (int) newValue;
            }
        });
    }

    public void loadKoch(ActionEvent actionEvent) throws IOException {
        clearCanvas();
        filename = "Koch" + level + ".kch";
//        fis = new FileInputStream(filename);
//        ois = new ObjectInputStream(fis);
        scanner = new Scanner(new FileInputStream(filename));
        bufferedScanner = new Scanner(new BufferedInputStream(new FileInputStream(filename)));
        TimeStamp time = new TimeStamp();
        time.setBegin("Start");
//        readObject();
//        readBufferedObject();
//        readText();
        readBufferedText();
        time.setEnd("End");
        System.out.println(time);
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
            ObjectInputStream inputStream = new ObjectInputStream(fis);
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
            ObjectInputStream inputStream = new ObjectInputStream(new BufferedInputStream(fis));
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

    private void drawEdge(Edge e) {
        // Graphics
        GraphicsContext gc = kochCanvas.getGraphicsContext2D();

        // Set line color
        gc.setStroke(Color.WHITE);

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
                e.color);
    }
}
