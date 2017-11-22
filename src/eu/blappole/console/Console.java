package eu.blappole.console;

import eu.blappole.calculate.Edge;
import eu.blappole.calculate.KochFractal;

import java.io.*;
import java.util.*;

public class Console {
    private static FileOutputStream out;
    private static DataOutputStream dos;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean stop = false;
        while (!stop) {
            System.out.println("What level should be generated? (1-12)");
            int level = scanner.nextInt();
            while (level < 1 || level > 12) {
                System.out.printf("%d isn't between 1 and 12%n", level);
                System.out.println("What level should be generated? (1-12)");
                level = scanner.nextInt();
            }
            try { generateKoch(level); }
            catch (FileNotFoundException e) { e.printStackTrace(); }
        }
    }

    private static void generateKoch(int level) throws FileNotFoundException {
        out = new FileOutputStream("edges.txt");
        dos = new DataOutputStream(out);
        KochFractal koch = new KochFractal();
        koch.setLevel(level);
        koch.addObserver((o, arg) -> {
            try { writeToFile((Edge) arg); }
            catch (IOException ex) { ex.printStackTrace(); }
        });
        koch.generateBottomEdge();
        koch.generateLeftEdge();
        koch.generateRightEdge();
        System.out.println(koch.getNrOfEdges());
    }

    private static void writeToFile(Edge e) throws IOException {
        dos.writeDouble(e.X1);
        dos.writeDouble(e.X2);
        dos.writeDouble(e.Y1);
        dos.writeDouble(e.Y1);
    }
}
