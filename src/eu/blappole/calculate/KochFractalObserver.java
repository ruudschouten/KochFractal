package eu.blappole.calculate;

import java.util.Observable;
import java.util.Observer;

public class KochFractalObserver implements Observer {

    @Override
    public void update(Observable o, Object arg) {
        Edge e = (Edge) arg;
        System.out.println(String.format("Edges set to: x1:%s\tx2:%s\ty1:%s\ty2:%s",
                (double) Math.round(e.X1 * 100d) / 100d,
                (double) Math.round(e.X2 * 100d) / 100d,
                (double) Math.round(e.Y1 * 100d) / 100d,
                (double) Math.round(e.Y2 * 100d) / 100d));
    }
}
