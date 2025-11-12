package util;
import java.io.*;
import java.util.regex.*;

public class StatsReader {

    public static void main(String[] args) {
        int num = 15;
        double[] means = new double[num];
        double[] stddevs = new double[num];

        readStats(num, means, stddevs, "./results/");
        // Print no final
        System.out.println("\n=== Resultados Finais ===");
        for (int i = 0; i < means.length; i++) {
            System.out.printf("Arquivo %2d -> Mean: %.4f | StdDev: %.4f%n", i, means[i], stddevs[i]);
        }
    }

    public static void readStats(int num, double[] means, double[] stddevs, String folderPath) {
        Pattern numberPattern = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");

        for (int i = 0; i < num; i++) {
            String filename = folderPath + i + "-stats-client-byzcast.txt";
            File file = new File(filename);

            if (!file.exists()) {
                System.out.println("⚠️ Arquivo não encontrado: " + filename);
                continue;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("Mean:")) {
                        Matcher matcher = numberPattern.matcher(line);
                        if (matcher.find()) {
                            means[i] = Double.parseDouble(matcher.group());
                        }
                    } else if (line.startsWith("StdDev:")) {
                        Matcher matcher = numberPattern.matcher(line);
                        if (matcher.find()) {
                            stddevs[i] = Double.parseDouble(matcher.group());
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
