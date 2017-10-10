package eu.blappole.calculate;

import eu.blappole.jsf31kochfractalfx.JSF31KochFractalFX;
import eu.blappole.timeutil.TimeStamp;
import javafx.application.Platform;

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
        KochCollector collector = new KochCollector(this, next);
        pool.submit(collector);
    }

    //Hierbij hebben we hulp gekregen van Nick,
    //eerst gebruikte we geen synchronized methode waardoor de edges niet allemaal in de lijst kwam
    synchronized void addEdge(Edge edge) {
        edges.add(edge);
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
            application.drawEdge(e);
        }
        time.setEnd("End");
        application.setTextNrEdges(String.valueOf(koch.getNrOfEdges()));
        application.setTextDraw(String.valueOf(time.toString()));
    }
}
