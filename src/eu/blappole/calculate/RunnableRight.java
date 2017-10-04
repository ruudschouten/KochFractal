package eu.blappole.calculate;

import java.util.Observable;
import java.util.Observer;

public class RunnableRight implements Runnable, Observer {
    private KochManager manager;
    private KochFractal fractal;

    RunnableRight(KochManager manager, int level) {
        this.manager = manager;
        fractal = new KochFractal();
        fractal.addObserver(this);
        fractal.setLevel(level);
    }

    @Override
    public void run() {
        fractal.generateRightEdge();
        manager.doneWithGenerating();
    }

    @Override
    public void update(Observable o, Object arg) {
        manager.addEdge((Edge) arg);
    }
}

