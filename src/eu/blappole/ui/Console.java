package eu.blappole.ui;

import eu.blappole.calculate.Edge;
import eu.blappole.calculate.KochFractal;
import eu.blappole.timeutil.TimeStamp;

import java.io.*;
import java.util.*;

public class Console {
    private static FileWriter writer;
    private static BufferedWriter bufferedWriter;
    private static FileOutputStream fos;
    private static ObjectOutputStream oos;
    private static ObjectOutputStream boos;
    private static BufferedOutputStream bos;

    private static String filename;

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
            try {
                generateKoch(level);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void generateKoch(int level) throws IOException {
        filename = "Koch" + level + ".kch";
        openWriters();
        KochFractal koch = new KochFractal();
        koch.setLevel(level);
        koch.addObserver((o, arg) -> {
            try {
                writeText((Edge) arg);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        TimeStamp timeStamp = new TimeStamp();
        timeStamp.setBegin("Start");
        koch.generateBottomEdge();
        koch.generateLeftEdge();
        koch.generateRightEdge();
        closeWriters();
        timeStamp.setEnd("End");
        System.out.println(timeStamp);
        System.out.println(koch.getNrOfEdges());
    }

    private static void openWriters() throws IOException {
        //Text
        writer = new FileWriter(filename);
        bufferedWriter = new BufferedWriter(writer);
        //Object
        fos = new FileOutputStream(filename);
        oos = new ObjectOutputStream(fos);
        bos = new BufferedOutputStream(fos);
        boos = new ObjectOutputStream(bos);
    }

    private static void closeWriters() throws IOException {
        //Text
        bufferedWriter.close();
        writer.close();
        //Object
//        fos.close();
        bos.close();
        oos.close();
        boos.close();
    }

    private static void writeText(Edge e) throws IOException {
        try {
            writer.write(String.format("%s,%s,%s,%s\n", e.X1, e.X2, e.Y1, e.Y2));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void writeBufferedText(Edge e) throws IOException {
        try {
            bufferedWriter.write(String.format("%s,%s,%s,%s\n", e.X1, e.X2, e.Y1, e.Y2));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void writeObject(Edge e) throws IOException {
        try {
            oos.writeObject(e);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void writeBufferedObject(Edge e) throws IOException {
        try {
            boos.writeObject(e);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
