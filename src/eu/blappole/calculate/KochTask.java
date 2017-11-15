package eu.blappole.calculate;

import eu.blappole.jsf31kochfractalfx.JSF31KochFractalFX;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicLong;

public class KochTask extends Task implements Observer {
    int total;
    List<Edge> edges = new ArrayList<>();
    AtomicLong done = new AtomicLong(0);
    JSF31KochFractalFX application;
    KochFractal fractal;
    KochCollector.Side side;

    public KochTask(KochCollector.Side side, int level, JSF31KochFractalFX application) {
        this.fractal = new KochFractal();
        fractal.setLevel(level);
        fractal.addObserver(this);
        total = fractal.getNrOfEdges() / 3;
        this.side = side;
        this.application = application;
    }

    @Override
    protected Object call() throws Exception {
        if (isCancelled()) return null;
        switch (side) {
            case LEFT: fractal.generateLeftEdge(); break;
            case RIGHT: fractal.generateRightEdge(); break;
            case BOTTOM: fractal.generateBottomEdge(); break;
        }
        return edges;
    }

    @Override
    public void update(Observable o, Object arg) {
        edges.add((Edge) arg);
        try {
            Thread.currentThread().sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
            this.cancel();
        }
        updateProgress(done.incrementAndGet(), total);
        updateMessage(String.valueOf(done));
        Platform.runLater(() -> this.application.drawEdge((Edge) arg, false));
    }

    @Override
    public void cancelled() {
        super.cancelled();
        application.clearKochPanel();
        fractal.cancel();
    }
}
