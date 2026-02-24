package util;
import java.io.*;
import java.util.regex.*;

public class StatsReaderClients {

    public static void main(String[] args) {
        int num = 15;
        int seconds = 30;

        // Lendo parâmetros do usuário
        if (args.length >= 2) {
            try {
                num = Integer.parseInt(args[0]);
                seconds = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid Inut!");
            }
        }

        double[] means = new double[num];
        double[] stddevs = new double[num];
        double[] TPSums = new double[num];
        int[] TotalMSG = new int[num];

    readStats(num, seconds, TPSums, means, stddevs, TotalMSG, "./logs/clients/");

    String outputFile = "./resultados.txt";

    try (PrintWriter out = new PrintWriter(new FileWriter(outputFile, true))) {
        out.println("\n=== Resultados Finais ===");

        double totalLat = 0;
        for (int i = 0; i < means.length; i++) {
            totalLat += means[i] * TPSums[i];
            // out.printf("Arquivo %2d -> Mean: %.4f | StdDev: %.4f%n",
            //             i, means[i], stddevs[i]);
        }

        out.printf("Média de latência total -> %.4f%n",
                   (totalLat / TotalMSG[0]));

        double totalTP = 0;
        for (int i = 0; i < num; i++) {
            totalTP += TPSums[i] / seconds;
        }

        out.printf("Throughput total -> %.4f%n", totalTP);
    }
    catch (IOException e) {
        e.printStackTrace();
    }
}

    public static void readStats(int num, int seconds, double[] TPSums, double[] means, double[] stddevs, int[] TotalMSG, String folderPath) {
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
                    } else if (line.startsWith("Total:")) {
                        Matcher matcher = numberPattern.matcher(line);
                        if (matcher.find()) {
                            TPSums[i] = Double.parseDouble(matcher.group());
                            TotalMSG[0] += TPSums[i];
                        }
                    }
                    // } else if (line.startsWith("Tp at sec")) {
                    //     String regex = "[:\\s]";
                    //     String[] parts = line.split(regex);
                    //     TPSums[i] += Integer.parseInt(parts[5]);//Tp at sec 108: 12
                    // }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
