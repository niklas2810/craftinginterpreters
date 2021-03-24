package com.niklasarndt.jlox;

import com.niklasarndt.jlox.util.ExitCodes;

import java.io.IOException;

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
