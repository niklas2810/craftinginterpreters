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

    public static void main(String[] args) {
        try {
            init(args);
        } catch (IOException exception) {
            System.err.println("Failed to execute: ");
            exception.printStackTrace();
            System.exit(ExitCodes.ERROR);
        }
    }

    private static void init(String[] args) throws IOException {
        if(args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(ExitCodes.USAGE);
        } else if(args.length == 1) {
            runFile(args[0]);
        } else {
            displayRepl();
        }
    }

    private static void runFile(String path) throws IOException {
        runLox(Files.readString(Paths.get(path), Charset.defaultCharset()));
    }

    private static void displayRepl() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))){
            String line;


            while(true) {
                System.out.print("> ");
                line = reader.readLine();
                if(line == null) break;
                runLox(line);
            }
        } catch (IOException e) {
            throw new IOException("Could not read from sysin", e);
        }
    }

    private static void runLox(String src) {
        Scanner scanner = new Scanner(src);
        List<Token> tokens = scanner.scanTokens();
    }
}
