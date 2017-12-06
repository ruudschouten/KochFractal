/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.blappole.calculate;

import javafx.scene.paint.Color;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 *
 * @author Peter Boots
 */
public class Edge implements Serializable {
    public double X1, Y1, X2, Y2;
    public double hue;

    public Edge(double X1, double Y1, double X2, double Y2, Color color) {
        this.X1 = X1;
        this.Y1 = Y1;
        this.X2 = X2;
        this.Y2 = Y2;
        hue = color.getHue();
    }

    public Edge() {

    }

    public Color getColor() {
        return Color.hsb(hue, 1, 1);
    }

    public void persist(final ByteBuffer buffer) {
        buffer.putDouble(X1);
        buffer.putDouble(Y1);
        buffer.putDouble(X2);
        buffer.putDouble(Y2);
        buffer.putDouble(hue);
    }

    public void recover(final ByteBuffer buffer) {
        this.X1 = buffer.getDouble();
        this.Y1 = buffer.getDouble();
        this.X2 = buffer.getDouble();
        this.Y2 = buffer.getDouble();
        this.hue = buffer.getDouble();
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s", X1, X2, Y1, Y2, hue);
    }
}
