package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AppLogger {

    private static final String LOG_FILE = "app.log";
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void info(String msg) {
        write("INFO", msg);
    }

    public static void warn(String msg) {
        write("WARN", msg);
    }

    public static void error(String msg) {
        write("ERROR", msg);
    }

    private static void write(String level, String msg) {
        String line = "[" + LocalDateTime.now().format(fmt) + "] [" + level + "] " + msg;
        System.out.println(line);
        // true - dopisuj do pliku
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Błąd zapisu logu: " + e.getMessage());
        }
    }
}
