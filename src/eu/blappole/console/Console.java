package eu.blappole.console;

import eu.blappole.calculate.Edge;
import eu.blappole.calculate.KochFractal;

import java.io.*;
import java.util.*;

public class Console {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean stop = false;
        while(!stop) {
            System.out.println("What level should be generated? (1-12)");
            int level = scanner.nextInt();
            while (level < 1 || level > 12) {
                System.out.printf("%d isn't between 1 and 12%n", level);
                System.out.println("What level should be generated? (1-12)");
                level = scanner.nextInt();
            }
            generateKoch(level);
        }
    }

    private static void generateKoch(int level) {
        KochFractal koch = new KochFractal();
        koch.setLevel(level);
        koch.addObserver((o, arg) -> {
            writeToFile((Edge)arg);
        });
        koch.generateBottomEdge();
        koch.generateLeftEdge();
        koch.generateRightEdge();
        System.out.println(koch.getNrOfEdges());
    }

    private static void writeToFile(Edge e) {
        //TODO: Fixe this
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("edgey.txt");
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeDouble(e.X1);
            dos.writeDouble(e.X2);
            dos.writeDouble(e.Y1);
            dos.writeDouble(e.Y1);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
