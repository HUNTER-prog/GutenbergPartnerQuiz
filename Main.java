// Aarush Pawar, Vivek Banker [12/4/2025]

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static final List<String> fileNames =
            Arrays.asList("Odyssey.txt", "KingInYellow.txt");

    public static void main(String[] args) {
        long start = System.nanoTime();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            List<Future<String>> results = fileNames.stream()
                    .map(file -> executor.submit(new CaesarTask(file, 3)))
                    .toList();

            for (int i = 0; i < fileNames.size(); i++) {
                String encryptedData = results.get(i).get();
                writeToFile("enc_" + fileNames.get(i), encryptedData);
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        long end = System.nanoTime();

        double ms = (end - start) / 1_000_000.0;
        System.out.println("Total time for Caesar cipher on all files (multithreaded): " + ms + " ms");
    }

    public static class CaesarTask implements Callable<String> {
        private final String fileName;
        private final int shift;

        public CaesarTask(String fileName, int shift) {
            this.fileName = fileName;
            this.shift = shift;
        }

        @Override
        public String call() {
            try {
                String content = Files.readString(Path.of(fileName));
                return applyCaesar(content, shift);
            } catch (IOException e) {
                throw new RuntimeException("Error reading " + fileName, e);
            }
        }
    }

    public static void writeToFile(String fileName, String data) {
        try {
            Files.writeString(Path.of(fileName), data);
        } catch (IOException e) {
            throw new RuntimeException("Error writing " + fileName, e);
        }
    }

    public static String applyCaesar(String input, int shift) {
        StringBuilder sb = new StringBuilder(input.length());

        for (char c : input.toCharArray()) {
            sb.append((char) (c + shift));
        }

        return sb.toString();
    }
}
