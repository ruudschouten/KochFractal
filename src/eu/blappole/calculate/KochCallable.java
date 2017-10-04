package eu.blappole.calculate;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;

public class KochCallable implements Callable, Observer {
    private ArrayList<Edge> edges;
    private KochManager manager;
    private KochFractal fractal;
    private Side side;

    KochCallable(KochManager manager, Side side, int level) {
        this.manager = manager;
        fractal = new KochFractal();
        this.side = side;
        fractal.addObserver(this);
        fractal.setLevel(level);
        edges = new ArrayList<>();
    }

    @Override
    public void update(Observable o, Object arg) {
        addEdges((Edge) arg);
    }

    @Override
    public ArrayList<Edge> call() throws Exception {
        switch (side) {
            case Left: fractal.generateLeftEdge(); break;
            case Right: fractal.generateRightEdge(); break;
            case Bottom: fractal.generateBottomEdge(); break;
        }
        manager.doneWithGenerating();
        return edges;
    }

    private synchronized void addEdges(Edge edge) {
        edges.add(edge);
    }
}

enum Side {
    Left, Right, Bottom
}