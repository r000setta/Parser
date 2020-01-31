package config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class IREmit {
    public final static String IRfilePath = "D:\\java2\\Parser\\src\\IR.txt";

    public final static boolean isConsole = true;
    public final static boolean isFile = false;

    private static FileWriter fileWriter;

    public static void init() throws IOException {
        if (isFile) {
            fileWriter = new FileWriter(new File(IRfilePath), true);
        }
    }

    public static void emit(String msg) {
        if (IREmit.isConsole) {
            System.out.println(msg);
        }
        try {
            if (IREmit.isFile) {
                if (fileWriter == null)
                    init();
                fileWriter.write(msg + "\n");
                fileWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close() throws IOException {
        if (isFile && fileWriter != null) {
            fileWriter.close();
        }
    }
}
