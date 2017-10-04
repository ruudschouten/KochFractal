package eu.blappole.calculate;

import java.util.Observable;
import java.util.Observer;

public class RunnableBottom implements Runnable, Observer {
    private KochManager manager;
    private KochFractal fractal;

    RunnableBottom(KochManager manager, int level) {
        this.manager = manager;
        fractal = new KochFractal();
        fractal.addObserver(this);
        fractal.setLevel(level);
    }

    @Override
    public void run() {
        fractal.generateBottomEdge();
        manager.doneWithGenerating();
    }

    @Override
    public void update(Observable o, Object arg) {
        manager.addEdge((Edge) arg);
    }
}

