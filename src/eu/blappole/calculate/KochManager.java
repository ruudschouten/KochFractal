package eu.blappole.calculate;

import eu.blappole.jsf31kochfractalfx.JSF31KochFractalFX;
import eu.blappole.timeutil.TimeStamp;
import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.*;

public class KochManager implements Observer {
    private ArrayList<Edge> edges = new ArrayList<>();
    ExecutorService pool;

    private JSF31KochFractalFX application;
    private TimeStamp drawTime;
    private TimeStamp calcTime;
    private KochFractal koch;

    private Future collectorContainer;

    public KochManager(JSF31KochFractalFX application) {
        this.application = application;
        koch = new KochFractal();
        koch.addObserver(this);
        pool = Executors.newFixedThreadPool(4);
    }

    @Override
    public void update(Observable o, Object arg) {
        edges.add((Edge) arg);
    }

    public void changeLevel(int next) {
        koch.cancel();
        //Terminate threads in pool
        if(collectorContainer!= null) {
            collectorContainer.cancel(true);
        }
        edges = new ArrayList<>();
        calcTime = new TimeStamp();
        koch.setLevel(next);
        calcTime.setBegin("Start");
        KochCollector collector = new KochCollector(this, next, application);
        application.getBarBottom().progressProperty().bind(collector.getBottom().progressProperty());
        application.getBarLeft().progressProperty().bind(collector.getLeft().progressProperty());
        application.getBarRight().progressProperty().bind(collector.getRight().progressProperty());
        application.getLbBottomStatus().textProperty().bind(collector.getBottom().messageProperty());
        application.getLbLeftStatus().textProperty().bind(collector.getLeft().messageProperty());
        application.getLbRightStatus().textProperty().bind(collector.getRight().messageProperty());
//        application.clearKochPanel();
        collectorContainer = pool.submit(collector);
//        pool.submit(collector);

        EventHandler handler = new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                drawEdges();
            }
        };
        collector.getBottom().setOnSucceeded(handler);
        collector.getLeft().setOnSucceeded(handler);
        collector.getRight().setOnSucceeded(handler);
    }

    synchronized void addEdges(ArrayList<Edge> edges) {
        this.edges.addAll(edges);
    }

    synchronized void doneWithGenerating() {
        calcTime.setEnd("End");
        System.out.printf("Done with calc %d - %s", koch.getLevel(), calcTime.toString());
        calcTime.setBegin("Start");
        Platform.runLater(() -> {
            application.setTextNrEdges(String.valueOf(koch.getNrOfEdges()));
            application.setTextCalc(String.valueOf(calcTime.toString()));
        });
        application.requestDrawEdges();
        Platform.runLater(() -> {
            application.setTextDraw(String.valueOf(drawTime.toString()));
        });
    }

    public void drawEdges() {
        application.clearKochPanel();
        drawTime = new TimeStamp();
        drawTime.setBegin("Start");
        for (Edge e : edges) {
            application.drawEdge(e, true);
        }
        drawTime.setEnd("End");
        application.setTextNrEdges(String.valueOf(koch.getNrOfEdges()));
        application.setTextDraw(String.valueOf(drawTime.toString()));
    }
}
