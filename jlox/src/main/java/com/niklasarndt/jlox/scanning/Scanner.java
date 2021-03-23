package com.niklasarndt.jlox.scanning;

import com.niklasarndt.jlox.Lox;
import com.niklasarndt.jlox.util.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Scanner {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private final Lox runtime;

    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Scanner(Lox runtime, String source) {
        this.runtime = runtime;
        this.source = source;
    }

    private char next() {
        return source.charAt(current++);
    }

    private char peek() {
        return reachedEnd() ? '\0' : source.charAt(current);
    }

    private char peekNext() {
        return current+1 >= source.length() ? '\0' : source.charAt(current+1);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private void scanToken() {
        char c = next();
        switch (c) {
            case '(':
                addToken(TokenType.LEFT_PAREN);
                break;
            case ')':
                addToken(TokenType.RIGHT_PAREN);
                break;
            case '{':
                addToken(TokenType.LEFT_BRACE);
                break;
            case '}':
                addToken(TokenType.RIGHT_BRACE);
                break;
            case ',':
                addToken(TokenType.COMMA);
                break;
            case '.':
                addToken(TokenType.DOT);
                break;
            case '-':
                addToken(TokenType.MINUS);
                break;
            case '+':
                addToken(TokenType.PLUS);
                break;
            case ';':
                addToken(TokenType.SEMICOLON);
                break;
            case '*':
                addToken(TokenType.STAR);
                break;
            case '/':
                if (isNext('/'))
                    skipComment();
                else if(isNext('*'))
                    skipBlockComment();
                else
                    addToken(TokenType.SLASH);
                break;
            case '!':
                addToken(isNext('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;
            case '=':
                addToken(isNext('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case '<':
                addToken(isNext('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case '>':
                addToken(isNext('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;
            case ' ':
            case '\r':
            case '\t': //Whitespace
                break;
            case '\n':
                ++line;
                break;
            case '"':
                parseString();
                break;
            default:
                if (isDigit(c))
                    parseNumber();
                else if (isAlpha(c))
                    parseIdentifier();
                else
                    runtime.error(line, "Unexpected charater " + c);
                break;
        }
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isAlpha(char c) {
        return ((c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_');
    }

    private void parseIdentifier() {
        String text = source.substring(start, current);

        for (TokenType tokenType : TokenType.values()) {
            if(text.equals(tokenType.keyword)) {
                addToken(tokenType);
                return;
            }
        }

        addToken(TokenType.IDENTIFIER);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void parseNumber() {
        while (isDigit(peek()))
            next();

        if(peek() == '.' && isDigit(peekNext())) {
            next();

            while (isDigit(peek()))
                next();
        }

        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private void parseString() {
        while (peek() != '"' && !reachedEnd()) {
            if (peek() == '\n')
                ++line;
            next();
        }

        if (reachedEnd()) {
            runtime.error(line, "Unterminated string");
            return;
        }

        next();
        addToken(TokenType.STRING, source.substring(start + 1, current - 1));
    }

    private void skipComment() {
        while (peek() != '\n' && !reachedEnd())
            next();
    }

    private void skipBlockComment() {
        while ((peek() != '*' || peekNext() != '/') && !reachedEnd())
            next();

        if(!reachedEnd())
            current += 2;
    }

    private boolean isNext(char expected) {
        if (reachedEnd() || source.charAt(current) != expected) return false;

        ++current;
        return true;
    }

    public List<Token> scanTokens() {
        while (!reachedEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private boolean reachedEnd() {
        return current >= source.length();
    }
}
