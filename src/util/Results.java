package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class Results {

    public static void main(String ... args){
        String startDirPath = "/home/elia/byzcast/experiments";
        File startDir = new File(startDirPath);
        if (startDir.exists() && startDir.isDirectory()) {
            try {
                findDirsWithLogs(startDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Invalid base directory: " + startDirPath);
        }
    }

    public static void findDirsWithLogs(File dir) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) return; // Could be permissions or an I/O issue
        boolean hasLogsSubdir = false;
        for (File file : files) {
            if (file.isDirectory() && file.getName().equalsIgnoreCase("logs")) {
                hasLogsSubdir = true;
                break;
            }
        }
        if (hasLogsSubdir) {
            printTpLatAvg(dir.getAbsolutePath());
        }
        // Recurse into subdirectories
        for (File file : files) if (file.isDirectory()) findDirsWithLogs(file);
    }

    private static void printTpLatAvg(String absolutePath) throws IOException {
        double avgTP = calcAvgTp(absolutePath);
        double avgLat = calcAvgLat(absolutePath);
        System.out.println(absolutePath + "\t" + avgTP+ "\t" + avgLat);
    }

    private static double calcAvgLat(String absolutePath) throws IOException {
        ArrayList<Double> values = new ArrayList<>();
        Files.list(Paths.get(absolutePath+"/results")) 
        .filter(file -> {try{return !Files.isHidden(file) && !Files.isDirectory(file) && !file.getFileName().toString().contains("per-node"); }catch (Exception e) {return false;}})
        .forEach(path -> {
            Scanner scan = null;
            try {scan = new Scanner(path.toFile());} catch (FileNotFoundException e) {}
            while(scan.hasNext()){
                String line = scan.nextLine();
                if(line.contains("global"))
                    values.add(Double.valueOf(line.trim().split("\\s+")[1].trim()));
            }
        });
        return values.stream().mapToDouble(d -> d).average().orElse(0);
    }

    private static double calcAvgTp(String absolutePath) throws IOException {
        ArrayList<Double> finalvalues = new ArrayList<>();
        Files.list(Paths.get(absolutePath+"/logs/clients")) 
        .filter(file -> {try{return !Files.isHidden(file) && !Files.isDirectory(file) && file.getFileName().toString().contains("cli"); }catch (Exception e) {return false;}})
        .forEach(path -> {
            Scanner scan = null;
            try {scan = new Scanner(path.toFile());} catch (FileNotFoundException e) {}
            ArrayList<Double> values = new ArrayList<>();
            while(scan.hasNext()){
                String line = scan.nextLine();
                if(line.startsWith("Tp at sec"))
                    values.add(Double.valueOf(line.split(":")[1].trim()));
            }
            finalvalues.add(values.stream().mapToDouble(d -> d).average().orElse(0));
        });
        return finalvalues.stream().mapToDouble(d -> d).sum();
    }


    // static int totalFiles = 0;
    // static int lines = 0;
    // private static long second = 1000000000;

    // static ArrayList<Integer> tppersecond = new ArrayList<>(500);

    // static class TPLine{
    //     int clients, skeen, byz, flex;
    //     public TPLine(int clients, int skeen, int byz, int flex) {
    //         this.clients = clients;
    //         this.skeen = skeen;
    //         this.byz = byz;
    //         this.flex = flex;
    //     }
    // }

    // static ArrayList<Double> readFiles(String strpath){
    //     ArrayList<Double> values = new ArrayList<>();
    //     try {
    //         Files.list(Paths.get(strpath)) 
    //         .filter(file -> {try{return !Files.isHidden(file) && !Files.isDirectory(file);}catch (Exception e) {return false;}})
    //         .forEach(path -> {
    //             // System.out.println("Reading file: " + path.getFileName());
    //             if(path.getFileName().toString().contains("per-node")) return;
    //             totalFiles++;
    //             ArrayList<Double> auxvalues = new ArrayList<>();
    //             Scanner scan = null;
    //             try{scan = new Scanner(path.toFile());}catch (Exception e) {}
    //             while(scan.hasNext()){
    //                 String line = scan.nextLine();
    //                 if(line.startsWith("O") || line.equals("")) continue;
    //                 if(line.startsWith("-")) break;

    //                 StringTokenizer str = new StringTokenizer(line, "\t");
    //                 if(str.countTokens() > 2){
    //                     str.nextToken(); // skip the first column (ORDER)
    //                     auxvalues.add(Double.valueOf(TimeUnit.MICROSECONDS.toMillis(Long.valueOf(str.nextToken())))); // add the second column (LATENCY)
    //                 }

    //             }
    //             values.addAll(auxvalues.subList((int)(auxvalues.size() * 0.1), (int)(auxvalues.size() * 0.9)));
    //             //values.addAll(auxvalues);
    //             auxvalues.clear();
    //         });
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    //     return values;
    // }

    // private static double readTPFiles(String strpath) {
    //     ArrayList<Double> values = new ArrayList<>();
    //     ArrayList<Double> valuesperclient = new ArrayList<>();
    //     try {
    //         Files.list(Paths.get(strpath)) 
    //         .filter(file -> {try{return !Files.isHidden(file) && !Files.isDirectory(file);}catch (Exception e) {return false;}})
    //         .forEach(path -> {
    //             if(!path.getFileName().toString().contains("cli")) return;
    //             totalFiles++;
    //             Scanner scan = null;
    //             //print("read file", path.toFile());
    //             try{scan = new Scanner(path.toFile());}catch (Exception e) {}
    //             while(scan.hasNext()){
    //                 String line = scan.nextLine();
    //                 if(line.startsWith("Tp at sec") && !line.startsWith("Tp at sec 120")){
    //                     lines++;
    //                     StringTokenizer str = new StringTokenizer(line, ":");
    //                     str.nextToken(); // skip the first column (text)
    //                     String value = str.nextToken().trim();
    //                     values.add(Double.valueOf(value)); // add the second column (tp)
    //                     // print("read line", line);
    //                     int second = Integer.valueOf(line.replace(":", "").split(" ")[3]);

    //                     tppersecond.set(second, Integer.valueOf(value)+tppersecond.get(second));
    //                     // tp.
    //                 }
    //             }
    //             valuesperclient.add(Stats.of(values.subList((int)(values.size() * .1), (int)(values.size() * .9))).mean());
    //             values.clear();
    //         });
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    //     // System.out.println(strpath + " - " + Quantiles.scale(100).indexes(5,25,50,75,80,90,95,99).compute(valuesperclient));
    //     return Stats.of(valuesperclient).sum();
    // }

    // static ArrayList<ArrayList<Double>> readFilesPerNode(String strpath, short numNodes){
    //     ArrayList<ArrayList<Double>> values = new ArrayList<>();
    //     try {
    //         Files.list(Paths.get(strpath)) 
    //         .filter(file -> {try{return !Files.isHidden(file) && !Files.isDirectory(file);}catch (Exception e) {return false;}})
    //         .forEach(path -> {
    //             if(!path.getFileName().toString().contains("per-node")) return;

    //             totalFiles++;
    //             ArrayList<ArrayList<Double>> auxvalues = new ArrayList<>();
    //             Scanner scan = null;
    //             try{scan = new Scanner(path.toFile());}catch (Exception e) {}
    //             while(scan.hasNext()){
    //                 String line = scan.nextLine();
    //                 if(line.startsWith("O") || line.equals("")) continue; // pula linha de cabecalho
    //                 if(line.startsWith("-")) break; // para quando comeca o resumo

    //                 StringTokenizer str = new StringTokenizer(line, "\t");
    //                 // EXEMPLOS de Linhas
    //                 // ORDER	LAT_0	LAT_1	LAT_2	LAT_3	LAT_4	LAT_5	LAT_6	LAT_7	LAT_8	LAT_9	LAT_10	LAT_11	DSTS	    TYPE
    //                 // 58	    2102	2320	12634	0	    0	    0	    0	    0	    0	    0	    0	    0	    [0,1,2]	    global
    //                 // 41	    0	    0	    0	    0	    19251	0	    23630	35903	0	    0	    0	    0	    [4,6,7]	    global
    //                 // 14	    0	    0	    0	    0	    0	    5072	0	    0	    0	    1667	0	    0	    [5,9]	    global

    //                 if(str.countTokens() > 2){
    //                     str.nextToken(); // skip the first column (ORDER)

    //                     ArrayList<Double> nodeValues = new ArrayList<>();
    //                     for(short n = 0; n < numNodes; n++){
    //                         String val = str.nextToken();
    //                         if(Double.valueOf(val) > 0){
    //                             nodeValues.add(Double.valueOf(TimeUnit.MICROSECONDS.toMillis(Long.valueOf(val))));
    //                         }
                            
    //                     }

    //                     auxvalues.add(nodeValues); // add the second column (LATENCY)
    //                 }

    //             }
    //             values.addAll(auxvalues.subList((int)(auxvalues.size() * 0.1), (int)(auxvalues.size() * 0.9)));
    //             // values.addAll(auxvalues);
    //             auxvalues.clear();
    //         });
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    //     return values;
    // }

    // private static void writeTPFile(HashMap<Integer, HashMap<String, Double>> tpValues, short nodes, String locality, int gc, String basedir, String cliregion) {
    //     try {
    //         File directory = new File(String.valueOf(basedir+"/plots/tp"+cliregion));
    //         if (!directory.exists())  {
    //             print("Criando dir", directory.getAbsolutePath());
    //             directory.mkdirs();
    //         }

    //         // write data file
    //         PrintWriter printerOut = new PrintWriter(basedir+"/plots/tp"+cliregion+"/TP_Reconf.txt");
    //         ArrayList<Integer> sortedKeys = new ArrayList<Integer>(tpValues.keySet());
    //         for(int i : tppersecond) if(i>0) printerOut.println(i);
    //         printerOut.flush();
    //         printerOut.close();

    //         // write plot file and plot the pdf
    //         printerOut = new PrintWriter(basedir+"/plots/tp"+cliregion+"/plot.p");
    //         printerOut.println( "set terminal pdf dashed size 5, 2.5 font \",18\" ");
    //         printerOut.println( "set key right top maxrow 2 ");
    //         printerOut.println( "set ylabel \"TP (ops/sec)\" ");
    //         printerOut.println( "set xlabel \"Time (sec)\" ");
    //         printerOut.println( "set grid ytics lt 0 lw 1 ");
    //         printerOut.println( "set grid xtics lt 0 lw 1 ");
    //         printerOut.println( "set output '"+basedir+"/plots/tp"+cliregion+"/tp.pdf' ");
    //         printerOut.println( "plot '"+basedir+"/plots/tp"+cliregion+"/TP_Reconf.txt' using 1 t \"TP\" with lines ");
    //         printerOut.flush();
    //         printerOut.close();

    //         @SuppressWarnings("deprecation")
    //         Process  process = Runtime.getRuntime().exec("gnuplot "+basedir+"/plots/tp"+cliregion+"/plot.p");
    //         process.waitFor();

    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    // }

    // private static void writeCDFFiles(boolean sort, String basedir, HashMap<Short, ArrayList<Double>> values, String algo, String locality) {

    //     String plotdir = basedir+"/plots/lat-cdf";

    //         File directory = new File(plotdir);
    //         if (!directory.exists())  {
    //             print("Criando dir", directory.getAbsolutePath());
    //             directory.mkdirs();
    //         }
    //     try {
    //         if(sort)for(ArrayList<Double> a : values.values()) a.sort(Double::compare);
    //         @SuppressWarnings("unchecked")
    //         ArrayList<Double> [] array = new ArrayList[values.size()];
    //         int i = 0;
    //         for(ArrayList<Double> a : values.values()){
    //             array[i] = a;
    //             i++;
    //         }
         
    //         PrintWriter printerOut = new PrintWriter(plotdir+"/CDF_"+algo+"_"+locality+"%loc_node1.txt");
    //         for(double v: array[0]) printerOut.println(v);
    //         printerOut.flush();
    //         printerOut.close();

    //         printerOut = new PrintWriter(plotdir+"/CDF_"+algo+"_"+locality+"%loc_node2.txt");
    //         for(double v: array[1]) printerOut.println(v);
    //         printerOut.flush();
    //         printerOut.close();

    //         printerOut = new PrintWriter(plotdir+"/CDF_"+algo+"_"+locality+"%loc_node3.txt");
    //         for(double v: array[2]) printerOut.println(v);
    //         printerOut.flush();
    //         printerOut.close();

    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    // }

    // private static void processMsgSizeFiles(String strpath, short nodes, String locality, int gc, int cli, String algo) {
    //     int[] qty = new int[nodes];
    //     int[] avgsize = new int[nodes];
    //     try {
    //         Files.list(Paths.get(strpath)) 
    //         .filter(file -> {try{return !Files.isHidden(file) && !Files.isDirectory(file);}catch (Exception e) {return false;}})
    //         .forEach(path -> {
    //             if(!path.getFileName().toString().contains("MsgSizes")) return;
    //             short node = Short.valueOf(path.getFileName().toString().replaceAll("[^0-9]", ""));
    //             Scanner scan = null;
    //             int qtyMsgs=0;
    //             double size=0;
    //             try{scan = new Scanner(path.toFile());}catch (Exception e) {}
    //             while(scan.hasNext()){
    //                 String line = scan.nextLine();
    //                 StringTokenizer str = new StringTokenizer(line, ";");
    //                 size += Double.valueOf(str.nextToken()); // skip the first column (text)
    //                 qtyMsgs++;
    //             }
    //             qty[node] = qtyMsgs;
    //             avgsize[node] = (int)(size/qtyMsgs);
    //         });
            
    //         PrintWriter printerOut = new PrintWriter("plots/msgsizes/"+algo+"_"+nodes+"nodes_"+cli +"cli_"+locality+"%_gc"+gc+"-aws-loc-file-90%.txt");
            
    //         for(int i=0; i < nodes; i++){
    //             printerOut.println(i + "\t" + qty[i] + "\t" + avgsize[i]);
    //             // System.out.println("Node"+node+": "+qtyMsgs+" msgs (avg "+(int)(size/qtyMsgs)+" bytes each)");
    //         }
    //         printerOut.flush();
    //         printerOut.close();

    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }

    // private static void processMsgSizeFilesDiscrete(String strpath, short nodes, String locality, int gc, int cli, String algo, short [] nodeMap) {
        
    //     HashMap<Integer, ArrayList<MsgSize>> values = new HashMap<>();

    //     try {
    //         Files.list(Paths.get(strpath)) 
    //         .filter(file -> {try{return !Files.isHidden(file) && !Files.isDirectory(file);}catch (Exception e) {return false;}})
    //         .forEach(path -> {
    //             if(!path.getFileName().toString().contains("MsgSizes")) return;
    //             short node = Short.valueOf(path.getFileName().toString().replaceAll("[^0-9]", ""));
    //             values.put((int)node, new ArrayList<>());
    //             Scanner scan = null;
    //             long time;
    //             int id;
    //             double size=0;
    //             try{scan = new Scanner(path.toFile());}catch (Exception e) {}
    //             while(scan.hasNext()){
    //                 String line = scan.nextLine();
    //                 StringTokenizer str = new StringTokenizer(line, ";");
    //                 time = Long.valueOf(str.nextToken()); 
    //                 id = Integer.valueOf(str.nextToken()); 
    //                 size = Double.valueOf(str.nextToken()); 
    //                 values.get((int)node).add(new MsgSize(time, id, size, null));
    //             }
    //         });

    //         PrintWriter printerOut = new PrintWriter("plots/msgsizes/"+algo+"_"+nodes+"nodes_"+cli +"cli_"+locality+"%_gc"+gc+"-ttlog.txt");

    //         for(int node : values.keySet()){
    //             List<MsgSize> l = values.get(node);
    //             List<MsgSize> sub = l.subList((int)(l.size()*.1), (int)(l.size()-(l.size()*.1)));
    //             long iniTime = sub.get(0).getTime();
    //             int qty = 0;
    //             double size = 0;
    //             //System.out.println("Node" + node + " ---------");
    //             ArrayList<Integer> qtyPerSec = new ArrayList<>();
    //             ArrayList<Double> sizePerSec = new ArrayList<>();
    //             for(MsgSize m : sub){
    //                 if((m.getTime()-iniTime) >= second){
    //                     //System.out.println(qty + " msgs/sec; " + ((size > 0 && qty > 0) ? size/qty : 0) + " bytes each (avg)");
    //                     qtyPerSec.add(qty);
    //                     sizePerSec.add((size > 0 && qty > 0) ? size/qty : 0);
    //                     iniTime = m.getTime();
    //                     qty = 0;
    //                     size = 0;
    //                 }
    //                 else {
    //                     qty++;
    //                     size += m.getSize();
    //                 }
    //             }
    //             if(qtyPerSec.size() > 0 && sizePerSec.size() > 0){
    //                 // System.out.println(Stats.of(qtyPerSec).mean() + " msg/sec; " + Stats.of(sizePerSec).mean() +" bytes each (avg)");
    //                 printerOut.println(nodeMap[node] + "\t" + Stats.of(qtyPerSec).mean() + "\t" + Stats.of(sizePerSec).mean());
    //             }
    //         }
            
    //         // for(int i=0; i < nodes; i++){
    //         //     printerOut.println(i + "\t" + qty[i] + "\t" + avgsize[i]);
    //         //     // System.out.println("Node"+node+": "+qtyMsgs+" msgs (avg "+(int)(size/qtyMsgs)+" bytes each)");
    //         // }
    //         printerOut.flush();
    //         printerOut.close();

    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }

    // private static void writeCDFFiles2(HashMap<Short, ArrayList<Double>> values, String algo, String locality) {
    //     try{
    //         PrintWriter printerOut = new PrintWriter("plots/lat-cdf2/CDF_"+algo+"_"+locality+"%loc_node1.txt");
    //         ArrayList<Double> dest1 = values.get((short)0);

    //         dest1.sort(Double::compare); //
    //         // Collections.sort(dest1, Collections.reverseOrder());

    //         int n = dest1.size();

    //         print(algo, "first", dest1.get(0), "last", dest1.get(n-1));

    //         int k = 1000;
    //         int count = 1;

    //         for(int i = 0; i < n; i++){
    //             double value = dest1.get(i);
    //             if(count%k == 0) {
    //                 int percentil = (((count*100)/n)+1);
    //                 // printerOut.println(percentil+" "+value);
    //             }
    //             count++;
    //         }

    //         // plotar k/10


    //         printerOut.flush();
    //         printerOut.close();
    //     }
    //     catch(Exception e){
    //         e.printStackTrace();
    //     }
    // }

    // private static void writeCDFFiles3(String basedir, HashMap<Short, ArrayList<Double>> values, String algo, String locality, int cli, int dag) {
    //     try{

    //         String plotdir = basedir+"/plots/lat-cdf2";

    //         File directory = new File(plotdir);
    //         if (!directory.exists())  {
    //             print("Criando dir", directory.getAbsolutePath());
    //             directory.mkdirs();
    //         }

    //         for(short d : new short[]{0,1,2}){
    //             ArrayList<Double> dest = values.get(d);
    //             PrintWriter printerOut = new PrintWriter(plotdir+"/CDF_"+algo+"_"+cli+"cli_"+locality+"%loc_dagtree"+dag+"_node"+(d+1)+".txt");
    //             dest.sort(Double::compare);
                
    //             int n = dest.size();
    //             int k = 1000;
    //             if(d==2) k = 100;
    //             int chunkSize = n/k;
    
    //             for(int i = 1; i <=  k; i++){
    //                 int index = i*chunkSize;
    //                 printerOut.println(i + " " + dest.get(index));
    //             }
    
    //             printerOut.flush();
    //             printerOut.close();
    //         }
    //     }
    //     catch(Exception e){
    //         e.printStackTrace();
    //     }
    // }

    // private static void print(Object... args){
    //     System.out.println(toString(args));
    // }

    // private static String toString(Object... args){
    //     String s = "";
    //     for(Object obj : args)s+=String.valueOf((obj==null?"":obj))+" ";
    //     return s;
    // }

    // private static void processTotalMsgsPerNode(String strpath, short nodes, String locality, int gc, int cli,String algo, short[] nodeMap) {
    //     HashMap<Short, Set<Integer>> values = new HashMap<>();
    //     for(short i = 0; i < nodes; i++) values.put(i, new HashSet<>());
    //     try {
    //         Files.list(Paths.get(strpath)) 
    //         .filter(file -> {try{return !Files.isHidden(file) && !Files.isDirectory(file);} catch (Exception e) {return false;}})
    //         .forEach(path -> {
    //             if(!path.getFileName().toString().contains("MsgSizes")) return;

    //             short node = Short.valueOf(path.getFileName().toString().replaceAll("[^0-9]", ""));
                
    //             //System.out.println(path.toString());

    //             Scanner scan = null;
    //             try{scan = new Scanner(path.toFile());}catch (Exception e) {}
    //             while(scan.hasNext()){
    //                 String line = scan.nextLine();
    //                 StringTokenizer str = new StringTokenizer(line, ";");
    //                 str.nextToken();                            // skip time
    //                 int id = Integer.valueOf(str.nextToken());  // get id
    //                 str.nextToken();                            // skip size
    //                 str.nextToken();                            // skip qtd dests
    //                 String dst = str.nextToken();               // get dests
    //                 String [] arrdst = dst.replace("[", "").replace("]", "").split(",");
    //                 for(String d : arrdst){
    //                     if(Short.valueOf(d.trim()).equals(node)){
    //                         values.get(node).add(id);
    //                     }
    //                 }
    //             }
    //         });

    //         PrintWriter printerOut = new PrintWriter("plots/msgspernode/"+algo+"_"+nodes+"nodes_"+cli +"cli_"+locality+"%_gc"+gc+"_ttlog.txt");
            
    //         for(short node : values.keySet()){
    //             printerOut.println(nodeMap[node] + "\t" + values.get(node).size());
    //         }

    //         printerOut.flush();
    //         printerOut.close();

    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }      
    // }

    // private static void loadNodesMap(short[] nodeMap, String basedir) {
    //     Scanner scan = null;
    //     int index = 0;
    //     try{scan = new Scanner(Paths.get(basedir+"/config/servers.conf"));}catch (Exception e) {}
    //     while(scan.hasNext()){
    //         String line = scan.nextLine();
    //         if(line.startsWith("#") || line.equals("")) continue;
    //         StringTokenizer str = new StringTokenizer(line, ",");
    //         short node = Short.valueOf(str.nextToken().replaceAll("[^0-9]", ""));
    //         nodeMap[index] = node;
    //         index++;
    //     }
    // }

    

    // private static void writeTPFile(HashMap<Integer, HashMap<String, Double>> tpValues, short nodes, String basedir) {
    //     try {
    //         File directory = new File(String.valueOf(basedir+"/plots/tp"));
    //         if (!directory.exists())  {
    //             print("Criando dir", directory.getAbsolutePath());
    //             directory.mkdirs();
    //         }

    //         // write data file
    //         PrintWriter printerOut = new PrintWriter(basedir+"/plots/tp/TP.txt");
    //         ArrayList<Integer> sortedKeys = new ArrayList<Integer>(tpValues.keySet());
    //         for(int i : tppersecond) if(i>0) printerOut.println(i);
    //         printerOut.flush();
    //         printerOut.close();

    //         // write plot file and plot the pdf
    //         printerOut = new PrintWriter(basedir+"/plots/tp/plot.p");
    //         printerOut.println( "set terminal pdf dashed size 5, 2.5 font \",18\" ");
    //         printerOut.println( "set key right top maxrow 2 ");
    //         printerOut.println( "set ylabel \"TP (ops/sec)\" ");
    //         printerOut.println( "set xlabel \"Time (sec)\" ");
    //         printerOut.println( "set grid ytics lt 0 lw 1 ");
    //         printerOut.println( "set grid xtics lt 0 lw 1 ");
    //         printerOut.println( "set output '"+basedir+"/plots/tp/tp.pdf' ");
    //         printerOut.println( "plot '"+basedir+"/plots/tp/TP.txt' using 1 t \"TP\" with lines ");
    //         printerOut.flush();
    //         printerOut.close();

    //         @SuppressWarnings("deprecation")
    //         Process  process = Runtime.getRuntime().exec("gnuplot "+basedir+"/plots/tp/plot.p");
    //         process.waitFor();

    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    // }

    // private static void latenciesPerSec(String basedir) {
    //     HashMap<Integer, ArrayList<Double>> seconds = new HashMap<>();
    //     try {
    //         Files.list(Paths.get(basedir+"/results")) 
    //         .filter(file -> {try{return !Files.isHidden(file) && !Files.isDirectory(file);} catch (Exception e) {return false;}})
    //         .forEach(path -> {
    //             if(path.getFileName().toString().contains("per-node")) return;
                
    //             Scanner scan=null;
    //             String line;
    //             long abs=0;
    //             int second=1;
    //             try{scan = new Scanner(path.toFile());}catch (Exception e) {}
    //             line = scan.nextLine();

    //             while(scan.hasNext()){
    //                 if(line.equals("") || line.startsWith("\t") || line.startsWith("ORDER") || line.startsWith("Sta") || line.startsWith("--")) {
    //                     line = scan.nextLine();
    //                     continue;
    //                 }
    //                 long curAbs = Long.parseLong(line.split("\t")[2]);
    //                 if(abs == 0) abs = curAbs;
    //                 else {
    //                     if(curAbs-abs >= 1000000){
    //                         second++;
    //                         // print(second);
    //                         abs = curAbs;
    //                     }
    //                 }

    //                 if(seconds.get(second)==null){
    //                     seconds.put(second, new ArrayList<>());
    //                 }
    //                 seconds.get(second).add(Double.parseDouble(line.split("\t")[1]));
    //                 // print(line.split("\t")[1], line.split("\t")[2]);
    //                 line = scan.nextLine();
    //             }
    //             // print(path.getFileName().toString());
    //         });

    //         File directory = new File(basedir+"/plots/latpersec");
    //         if (!directory.exists())  {
    //             print("Criando dir", directory.getAbsolutePath());
    //             directory.mkdirs();
    //         }

    //         // write data file
    //         PrintWriter printerOut = new PrintWriter(basedir+"/plots/latpersec/lat.txt");
    //         for(int key : seconds.keySet()){
    //             printerOut.println(key+"\t"+(long)Stats.of(seconds.get(key)).mean()+"\t"+ (long)Stats.of(seconds.get(key)).populationStandardDeviation());
    //         }
    //         printerOut.flush();
    //         printerOut.close();

    //         // write plot file and plot the pdf
    //         printerOut = new PrintWriter(basedir+"/plots/latpersec/plot.p");

    //         printerOut.println( "set terminal pdf dashed size 5, 2.5 font \",18\" ");
    //         printerOut.println( "set style data histogram ");
    //         printerOut.println( "set style histogram cluster gap 1 errorbars ");
    //         printerOut.println( "set key right top maxrow 2 ");
    //         printerOut.println( "set ylabel \"Latency (ms)\" ");
    //         printerOut.println( "set xlabel \"Time (sec)\" ");
    //         printerOut.println( "set grid ytics lt 0 lw 1 ");
    //         printerOut.println( "set grid xtics lt 0 lw 1 ");
    //         printerOut.println("set xrange[0:120]");
    //         printerOut.println("set xtics rotate by 50 right");


    //         printerOut.println( "set output '"+basedir+"/plots/latpersec/lat.pdf' ");
    //         printerOut.println( "plot '"+basedir+"/plots/latpersec/lat.txt' using ($2/1000):($3/1000):xtic($1) t \"Final Latency\" ");
    //         printerOut.flush();
    //         printerOut.close();

    //         @SuppressWarnings("deprecation")
    //         Process  process = Runtime.getRuntime().exec("gnuplot "+basedir+"/plots/latpersec/plot.p");
    //         process.waitFor();
            
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }

    // private static void latenciesPerSecPerDest(String basedir, int numNodes) {
    //     HashMap<Integer, HashMap<String, ArrayList<Long>>> seconds = new HashMap<>();
    //     try {
    //         Files.list(Paths.get(basedir+"/results")) 
    //         .filter(file -> {try{return !Files.isHidden(file) && !Files.isDirectory(file);} catch (Exception e) {return false;}})
    //         .forEach(path -> {
    //             if(!path.getFileName().toString().contains("per-node")) return;
                
    //             Scanner scan=null;
    //             String line;
    //             long abs=0, curAbs=0;
    //             int second=1;
    //             try{scan = new Scanner(path.toFile());}catch (Exception e) {}
    //             line = scan.nextLine();

    //             while(scan.hasNext()){
    //                 if(line.equals("") || line.startsWith("\t") || line.startsWith("ORDER") || line.startsWith("Sta") || line.startsWith("--")) {
    //                     line = scan.nextLine();
    //                     continue;
    //                 }
    //                 String [] linesplit = line.split("\t");
    //                 String dests = linesplit[numNodes+1];

    //                 long latency = 0;
    //                 for(int i=1; i<=numNodes;i++){
    //                     if(Long.valueOf(linesplit[i]) > latency){
    //                         latency = Long.valueOf(linesplit[i]);
    //                     }
    //                 }

    //                 curAbs+=latency;

    //                 if(abs == 0) abs = latency;
    //                 else {
    //                     if(curAbs-abs >= 1000000){
    //                         second++;
    //                         // print(second);
    //                         abs = curAbs;
    //                     }
                        
    //                 }

    //                 if(seconds.get(second)==null){
    //                     seconds.put(second, new HashMap<>());
    //                 }
    //                 if(seconds.get(second).get(dests)==null){
    //                     seconds.get(second).put(dests, new ArrayList<>());
    //                 }
    //                 seconds.get(second).get(dests).add((latency));
    //                 // print(line.split("\t")[1], line.split("\t")[2]);
    //                 line = scan.nextLine();
    //             }
    //             // print(path.getFileName().toString(), seconds.size());
    //         });

    //         File directory = new File(basedir+"/plots/latpersec/perdest");
    //         if (!directory.exists())  {
    //             print("Criando dir", directory.getAbsolutePath());
    //             directory.mkdirs();
    //         }

    //         HashMap<String, PrintWriter> files = new HashMap<>();
    //         PrintWriter plot = new PrintWriter(basedir+"/plots/latpersec/perdest/plot.p");
    //         plot.println( "set terminal pdf dashed size 5, 2.5 font \",18\" ");
    //         plot.println( "set style data histogram ");
    //         // plot.println( "set style histogram cluster gap 1 errorbars ");
    //         plot.println( "set key right top maxrow 2 ");
    //         plot.println( "set ylabel \"Latency (ms)\" ");
    //         plot.println( "set xlabel \"Time (sec)\" ");
    //         plot.println( "set grid ytics lt 0 lw 1 ");
    //         // plot.println( "set grid xtics lt 0 lw 1 ");
    //         // plot.println("set xrange [:10]");
    //         // plot.println(" set xtic 10 ");

    //         plot.println("set xtics rotate by 90 right font ',8' nomirror");
    //         // plot.println("set xtics auto");

    //         for(int key : seconds.keySet()){
    //             for(String key2 : seconds.get(key).keySet()){
    //                 if(files.get(key2) == null){
    //                     files.put(key2, new PrintWriter(basedir+"/plots/latpersec/perdest/lat"+key2+".txt"));
                        
    //                     plot.println( "set output '"+basedir+"/plots/latpersec/perdest/lat"+key2+".pdf' ");
    //                     plot.println( "plot '"+basedir+      "/plots/latpersec/perdest/lat"+key2+".txt' using ($2/1000):xticlabel(1) t \"Dests "+key2+" \" ");

                        
    //                 }
    //                 // Stats s = Stats.of(seconds.get(key).get(key2));
    //                 files.get(key2).println(
    //                     key+"\t"+
    //                     Quantiles.scale(100)
    //                     .indexes(90,95,99)
    //                     .compute(seconds.get(key).get(key2))
    //                     .toString()
    //                     .replace("{","")
    //                     .replaceAll(", ","\t")
    //                     .replace("90=","")
    //                     .replace("95=","")
    //                     .replace("99=","")
    //                     .replace("}","")
    //                 );
    //             }
    //         }

    //         for(PrintWriter file : files.values()){
    //             file.flush();
    //             file.close();
    //         }
    //         plot.flush();
    //         plot.close();

    //         @SuppressWarnings("deprecation")
    //         Process  process = Runtime.getRuntime().exec("gnuplot "+basedir+"/plots/latpersec/perdest/plot.p");
    //         process.waitFor();

            
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }



}
