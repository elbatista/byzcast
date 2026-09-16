package util;
import java.io.*;
import java.util.regex.*;

public class StatsReaderClients {

    public static void main(String[] args) {
        int cli = 15;
        int seconds = 120;
        String code = "";
        String size = "";
        String dests = "";

        // Lendo parâmetros do usuário
        if (args.length >= 4) {
            //System.out.println("Algo");
            try {
                cli = Integer.parseInt(args[0]) * 15;
                seconds = Integer.parseInt(args[1]);
                code = (args[2]);
                size = (args[3]);
                dests = (args[4]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid Inut!");
            }
        }

        double[] means = new double[cli];
        double[] stddevs = new double[cli];
        double[] TPSums = new double[cli];
        int[] TotalMSG = new int[cli];

    readStats(cli, seconds, TPSums, means, stddevs, TotalMSG, "./logs/clients/");

    String outputFile = "./Plots/Dests:" + dests + ",Lat:50,Time:" + seconds + ",Cli:" + cli + ".txt";

    try (PrintWriter out = new PrintWriter(new FileWriter(outputFile, true))) {

        double totalLat = 0;
        for (int i = 0; i < means.length; i++) {
            totalLat += means[i] * TPSums[i];
            // out.printf("Arquivo %2d -> Mean: %.4f | StdDev: %.4f%n",
            //             i, means[i], stddevs[i]);
        }

        double totalTP = 0;
        for (int i = 0; i < cli; i++) {
            totalTP += TPSums[i] / seconds;
        }

        out.printf(java.util.Locale.of("pt", "BR"),
           "%s %s %.2f %.2f%n",
           code, size,
           (totalLat / TotalMSG[0]),
           totalTP);
    }
    catch (IOException e) {
        e.printStackTrace();
    }
}

    public static void readStats(int cli, int seconds, double[] TPSums, double[] means, double[] stddevs, int[] TotalMSG, String folderPath) {
        Pattern cliberPattern = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");
        boolean aux = false;
        for (int i = 0; i < cli; i++) {
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
                        Matcher matcher = cliberPattern.matcher(line);
                        if (matcher.find()) {
                            means[i] = Double.parseDouble(matcher.group());
                        }
                    } else if (line.startsWith("StdDev:")) {
                        Matcher matcher = cliberPattern.matcher(line);
                        if (matcher.find()) {
                            stddevs[i] = Double.parseDouble(matcher.group());
                        }
                    } else if (line.startsWith("Total:")) {
                        Matcher matcher = cliberPattern.matcher(line);
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
