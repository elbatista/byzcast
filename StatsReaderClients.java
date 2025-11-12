package util;
import java.io.*;
import java.util.regex.*;

public class StatsReaderClients {

    public static void main(String[] args) {
        int num = 15;
        int seconds = 120;
        double[] means = new double[num];
        double[] stddevs = new double[num];
        double[] TPMeans = new double[seconds];

        readStats(num, seconds, TPMeans, means, stddevs, "./logs/clients/");
        // Print no final
        System.out.println("\n=== Resultados Finais ===");
        for (int i = 0; i < means.length; i++) {
            System.out.printf("Arquivo %2d -> Mean: %.4f | StdDev: %.4f%n", i, means[i], stddevs[i]);
        }
    }

    public static void readStats(int num,int seconds, double[] TPMeans, double[] means, double[] stddevs, String folderPath) {
        Pattern numberPattern = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");
        boolean aux = false;
        for (int i = 0; i < num; i++) {
            String filename = folderPath + "client" + i + ".txt";
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
                    } else if (line.startsWith("Tp at sec")) {
                        String regex = "[:\\s]";
                        String[] parts = line.split(regex);
                        TPMeans[Integer.parseInt(parts[3])] += Integer.parseInt(parts[5]);//Tp at sec 108: 12
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        for (int i = 0; i < seconds; i++) {
            TPMeans[i] = TPMeans[i]/num;
            System.out.printf("Média de throughput em %2d segundos -> %.4f \n", i, TPMeans[i]);
        }
    }
}
