package com.niklasarndt.jlox.tests;

import com.niklasarndt.jlox.Lox;
import com.niklasarndt.jlox.scanning.Scanner;
import com.niklasarndt.jlox.scanning.Token;
import com.niklasarndt.jlox.util.TokenType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class LoxTests {

    private Lox lox;

    @BeforeEach
    public void setUp() {
        lox = new Lox(true);
    }

    @Test
    public void testBasicLexemes() {
        String src = TestUtils.readResourceLoxFile("000-lexemes.lox");

        Scanner scanner = new Scanner(lox, src);

        List<Token> tokens = scanner.scanTokens();

        Assertions.assertFalse(lox.hadError());
        Assertions.assertEquals(17, tokens.size());
        Assertions.assertEquals(TokenType.EOF, tokens.get(tokens.size()-1).type);
        Assertions.assertEquals(TokenType.LEFT_PAREN, tokens.get(0).type);
        Assertions.assertEquals("(", tokens.get(0).lexeme);
    }

    @Test
    public void testBasicLiterals() {
        String src = TestUtils.readResourceLoxFile("001-literals.lox");

        Scanner scanner = new Scanner(lox, src);
        List<Token> tokens = scanner.scanTokens();

        Assertions.assertFalse(lox.hadError());
        Assertions.assertEquals(3, tokens.size());
        Assertions.assertEquals(TokenType.STRING, tokens.get(0).type);
        Assertions.assertEquals("This is a test :)", tokens.get(0).literal);
        Assertions.assertEquals(TokenType.NUMBER, tokens.get(1).type);
        Assertions.assertEquals(123.456d, (double)tokens.get(1).literal);

        scanner = new Scanner(lox, "\"Unterminated string");
        tokens = scanner.scanTokens();

        Assertions.assertTrue(lox.hadError());
        Assertions.assertEquals(1, tokens.size());
    }

    @Test
    public void testFailure() {
        Scanner scanner = new Scanner(lox, ".-.-31");
        List<Token> tokens = scanner.scanTokens();


    }
}
