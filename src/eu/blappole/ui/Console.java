package eu.blappole.ui;

import eu.blappole.calculate.Edge;
import eu.blappole.calculate.KochFractal;
import eu.blappole.timeutil.TimeStamp;
import sun.nio.ch.DirectBuffer;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Console {

    private static FileWriter writer;
    private static BufferedWriter bufferedWriter;
    private static FileOutputStream fileOutputStream;
    private static ObjectOutputStream objectOutputStream;
    private static ObjectOutputStream bufferedObjectOutputStream;
    private static BufferedOutputStream bufferedOutputStream;

    private static String filename;
    private static Type type = TypeGetter.TYPE;
    private static RandomAccessFile randomAccessFile;
    private static FileChannel fileChannel;
    private static byte[] bytes;
    private static MappedByteBuffer mappedByteBuffer;

    public static void main(String[] args) throws IOException {
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

                Path filePath = new File(filename).toPath();
                Path realtimePath = new File(FilenameGetter.REALTIMEPATH).toPath();
                Files.move(filePath, realtimePath, REPLACE_EXISTING);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void generateKoch(int level) throws IOException {
        filename = FilenameGetter.FILEPREFIX + level + FilenameGetter.FILETYPE;
        List<Edge> edges = new ArrayList<>();
//        openWriters();
        KochFractal koch = new KochFractal();
        koch.setLevel(level);
        koch.addObserver((o, arg) -> {
            try {
                switch (type) {
                    case TEXT: writeText((Edge) arg); break;
                    case BINARY: writeObject((Edge) arg); break;
                    case MAPPED: edges.add((Edge) arg); break;
                    case BUFFEREDTEXT: writeBufferedText((Edge) arg); break;
                    case BUFFEREDBINARY: writeBufferedObject((Edge) arg); break;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        TimeStamp timeStamp = new TimeStamp();
        timeStamp.setBegin("Start");
        koch.generateBottomEdge();
        koch.generateLeftEdge();
        koch.generateRightEdge();
//        closeWriters();
        if (type == Type.MAPPED) {
            writeMapped(edges);
        }
        timeStamp.setEnd("End");
        System.out.println(timeStamp);
        System.out.println(koch.getNrOfEdges());
    }

    private static void openWriters() throws IOException {
        if (type == Type.TEXT || type == Type.BUFFEREDTEXT) {
            //Text
            writer = new FileWriter(filename);
            bufferedWriter = new BufferedWriter(writer);
        } else if (type == Type.BINARY || type == Type.BUFFEREDBINARY) {
            //Object
            fileOutputStream = new FileOutputStream(filename);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bufferedObjectOutputStream = new ObjectOutputStream(bufferedOutputStream);
        }
    }

    private static void closeWriters() throws IOException {
        if (type == Type.TEXT) {
            writer.close();
        } else if (type == Type.BUFFEREDTEXT) {
            bufferedWriter.close();
        } else if (type == Type.BINARY) {
            objectOutputStream.close();
        } else if (type == Type.BUFFEREDBINARY) {
            bufferedObjectOutputStream.close();
        }
        if (type == Type.TEXT || type == Type.BUFFEREDTEXT) {
            //Text
            bufferedWriter.close();
            writer.close();
        }
        if (type == Type.BINARY || type == Type.BUFFEREDBINARY) {
            //Object
            objectOutputStream.close();
            fileOutputStream.close();
        }
    }

    private static void writeMapped(List<Edge> edges) throws IOException {
        randomAccessFile = new RandomAccessFile(filename, "rw");
        fileChannel = randomAccessFile.getChannel();
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(edges);

            bytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            objectOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, bytes.length);
        mappedByteBuffer.put(bytes);
        fileChannel.close();
        randomAccessFile.close();

        //Hierbij heb ik hulp gekregen van Bart en Tom.
        sun.misc.Cleaner cleaner = ((DirectBuffer) mappedByteBuffer).cleaner();
        cleaner.clean();
    }

    private static void writeText(Edge e) throws IOException {
        try {
            writer.write(e.toString() + "\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void writeBufferedText(Edge e) throws IOException {
        try {
            bufferedWriter.write(e.toString() + "\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void writeObject(Edge e) throws IOException {
        try {
            objectOutputStream.writeObject(e);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void writeBufferedObject(Edge e) throws IOException {
        try {
            bufferedObjectOutputStream.writeObject(e);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
