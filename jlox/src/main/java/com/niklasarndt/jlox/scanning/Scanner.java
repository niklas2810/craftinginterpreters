package com.niklasarndt.jlox.scanning;

import java.util.ArrayList;
import java.util.List;

public class Scanner {

    private final String source;

    public Scanner(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        return new ArrayList<>();
    }
}
