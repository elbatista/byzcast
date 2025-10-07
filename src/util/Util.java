package util;

import java.util.Random;

public class Util extends BaseObj{
    private static Util instance;
    private char[] symbols= "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    public byte[] randomData() {
        Random rand = new Random();
        int len = rand.nextInt(1024)+1;
        char[] buf = new char[len];
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[rand.nextInt(symbols.length)];
        return new String(buf).getBytes();
    }

    public static Util getInstance(){
        if(instance == null) instance = new Util();
        return instance;
    }

    public static String convertBytes(long bytes) {
        double kb = bytes / 1024.0;
        double mb = kb / 1024.0;
        double gb = mb / 1024.0;

        if (gb >= 1) {
            return String.format("%.2f GB", gb);
        } else if (mb >= 1) {
            return String.format("%.2f MB", mb);
        } else if (kb >= 1) {
            return String.format("%.2f KB", kb);
        } else {
            return String.format("%d Bytes", bytes);
        }
    }

}
