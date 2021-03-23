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

public class Main {

    public static void main(String[] args) {
        try {
            Lox lox = new Lox();
            lox.init(args);
        } catch (IOException exception) {
            System.err.println("Failed to execute: ");
            exception.printStackTrace();
            System.exit(ExitCodes.ERROR);
        }
    }


}
