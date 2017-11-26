package eu.blappole.ui.controllers;

import eu.blappole.calculate.Edge;
import eu.blappole.calculate.KochFractal;
import eu.blappole.calculate.KochManager;
import javafx.application.Platform;
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
        fis = new FileInputStream(filename);
        ois = new ObjectInputStream(fis);
        scanner = new Scanner(new File(filename));
        readObject();
//        readText();
        drawKoch();
    }

    private void readText() {
        //TODO: Fix positions
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String x1 = line.substring(line.indexOf("X1") + 2, line.indexOf("X2") - 1);
            String x2 = line.substring(line.indexOf("X2") + 2, line.indexOf("Y1") - 1);
            String y1 = line.substring(line.indexOf("Y1") + 2, line.indexOf("Y2") - 1);
            String y2 = line.substring(line.indexOf("Y2") + 2);
            Edge e = new Edge(Double.valueOf(x1) * 1000, Double.valueOf(x2) * 1000, Double.valueOf(y1) * 1000, Double.valueOf(y2) * 1000, Color.WHITE);
            drawEdge(e);
        }
    }

    private void readObject() {

//            Edge e = null;
//            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(scanner.nextLine()))) {
//                e = (Edge) ois.readObject();
//                drawEdge(e);
//            } catch (IOException | ClassNotFoundException e1) {
//                e1.printStackTrace();
//            }
        boolean cont = true;
        try {
            while (cont) {
                Object o = ois.readObject();
                Edge e = (Edge) o;
                if (e != null) {
                    drawEdge(e);
//                    edges.add(e);
                } else cont = false;
            }
            ois.close();
//            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void drawKoch() {
        for (Edge e : edges) {
//            Platform.runLater(new Runnable() {
//                @Override
//                public void run() {
//                    drawEdge(e);
//                }
//            });
            drawEdge(e);
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
        gc.strokeLine(e.X1, e.Y1, e.X2, e.Y2);
    }
}
