package eu.blappole;

import eu.blappole.calculate.KochFractal;
import eu.blappole.calculate.KochFractalObserver;

public class Main {

    public static void main(String[] args) {
        KochFractal fractal = new KochFractal();
        fractal.setLevel(2);
        KochFractalObserver obs = new KochFractalObserver();
        fractal.addObserver(obs);

        fractal.generateBottomEdge();
        fractal.generateLeftEdge();
        fractal.generateRightEdge();
    }
}
