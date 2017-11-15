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
    private TimeStamp time;
    private KochFractal koch;

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
        edges = new ArrayList<>();
        time = new TimeStamp();
        koch.setLevel(next);
        time.setBegin("Start");
        KochCollector collector = new KochCollector(this, next, application);
        application.getBarBottom().progressProperty().bind(collector.gettBottom().progressProperty());
        application.getBarLeft().progressProperty().bind(collector.gettLeft().progressProperty());
        application.getBarRight().progressProperty().bind(collector.gettRight().progressProperty());
        application.getLbBottomStatus().textProperty().bind(collector.gettBottom().messageProperty());
        application.getLbLeftStatus().textProperty().bind(collector.gettLeft().messageProperty());
        application.getLbRightStatus().textProperty().bind(collector.gettRight().messageProperty());
//        application.clearKochPanel();
        pool.submit(collector);
        EventHandler handler = new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                drawEdges();
            }
        };
        collector.gettBottom().setOnSucceeded(handler);
        collector.gettLeft().setOnSucceeded(handler);
        collector.gettRight().setOnSucceeded(handler);
    }

    synchronized void addEdges(ArrayList<Edge> edges) {
        this.edges.addAll(edges);
    }

    synchronized void doneWithGenerating() {
        time.setEnd("End");
        System.out.printf("Done with calc %d - %s", koch.getLevel(), time.toString());
        time.setBegin("Start");
        Platform.runLater(() -> {
            application.setTextNrEdges(String.valueOf(koch.getNrOfEdges()));
            application.setTextCalc(String.valueOf(time.toString()));
        });
        application.requestDrawEdges();
        Platform.runLater(() -> {
            application.setTextDraw(String.valueOf(time.toString()));
        });
    }

    public void drawEdges() {
        application.clearKochPanel();
        time = new TimeStamp();
        time.setBegin("Start");
        for (Edge e : edges) {
            application.drawEdge(e, true);
        }
        time.setEnd("End");
        application.setTextNrEdges(String.valueOf(koch.getNrOfEdges()));
        application.setTextDraw(String.valueOf(time.toString()));
    }
}
