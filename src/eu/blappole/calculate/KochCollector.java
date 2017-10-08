package eu.blappole.calculate;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class KochCollector implements Runnable {
    private int next;
    private ExecutorService pool;
    private KochManager manager;

    public KochCollector(KochManager manager, int next) {
        this.manager = manager;
        this.pool = manager.pool;
        this.next = next;
    }

    @Override
    public void run() {
        Future fLeft, fRight, fBottom;
        fLeft = pool.submit(new KochCallable(manager, KochCallable.Side.Left, next));
        fRight = pool.submit(new KochCallable(manager, KochCallable.Side.Right, next));
        fBottom = pool.submit(new KochCallable(manager, KochCallable.Side.Bottom, next));
        try {
            ArrayList<Edge> left = (ArrayList<Edge>) fLeft.get();
            ArrayList<Edge> right = (ArrayList<Edge>) fRight.get();
            ArrayList<Edge> bottom = (ArrayList<Edge>) fBottom.get();
            manager.edges.addAll(left);
            manager.edges.addAll(right);
            manager.edges.addAll(bottom);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
