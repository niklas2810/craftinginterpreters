package com.niklasarndt.jlox;

import com.niklasarndt.jlox.scanning.Scanner;
import com.niklasarndt.jlox.scanning.Token;
import com.niklasarndt.jlox.util.ExitCodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {

    private final boolean silent;
    private boolean hadError = false;

    public Lox() {
        this(false);
    }

    public Lox(boolean silent) {
        this.silent = silent;
    }

    void init(String[] args) throws IOException {
        if (args.length > 1) {
            if (!silent)
                System.out.println("Usage: jlox [script]");
            System.exit(ExitCodes.USAGE);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            displayRepl();
        }
    }

    private void runFile(String path) throws IOException {
        run(Files.readString(Paths.get(path), Charset.defaultCharset()));

        if (hadError)
            System.exit(ExitCodes.PARSING_ERROR);
    }

    private void displayRepl() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            long last = 0;

            while (true) {
                if (!silent)
                    System.out.print((last > 0 ? String.format("[%dms]\n",
                            System.currentTimeMillis() - last) : "") + "> ");
                line = reader.readLine();
                if (line == null) break;
                last = System.currentTimeMillis();
                run(line);
                hadError = false;
            }
        } catch (IOException e) {
            throw new IOException("Could not read from sysin", e);
        }
    }

    public void run(String source) {
        Scanner scanner = new Scanner(this, source);
        List<Token> tokens = scanner.scanTokens();
    }

    public void error(int line, String message) {
        report(line, "", message);
    }

    private void report(int line, String location, String message) {
        if (!silent)
            System.err.println((line > 0 ? "[line " + line + "] " : "") + "Error"
                    + (location != null && location.length() > 0 ? " in " + location : "")
                    + ": " + message);

        hadError = true;
    }

    public boolean hadError() {
        return hadError;
    }
}
