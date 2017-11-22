package eu.blappole.calculate;

import eu.blappole.jsf31kochfractalfx.JSF31KochFractalFX;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class KochCollector implements Runnable {
    private Task<List<Edge>> tLeft;
    private Task<List<Edge>> tRight;
    private Task<List<Edge>> tBottom;
    private ExecutorService pool;
    private KochManager manager;

    public KochCollector(KochManager manager, int next, JSF31KochFractalFX application) {
        this.manager = manager;
        this.pool = manager.pool;
        tLeft = new KochTask(Side.LEFT, next, application);
        tRight = new KochTask(Side.RIGHT, next, application);
        tBottom = new KochTask(Side.BOTTOM, next, application);
    }

    @Override
    public void run() {
        pool.submit(tLeft);
        pool.submit(tBottom);
        pool.submit(tRight);
        try {
            manager.addEdges((ArrayList<Edge>) tLeft.get());
            manager.addEdges((ArrayList<Edge>) tRight.get());
            manager.addEdges((ArrayList<Edge>) tBottom.get());
            manager.doneWithGenerating();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public Task<List<Edge>> getBottom() {
        return tBottom;
    }

    public Task<List<Edge>> getLeft() {
        return tLeft;
    }

    public Task<List<Edge>> getRight() {
        return tRight;
    }

    enum Side {
        LEFT, RIGHT, BOTTOM
    }
}

