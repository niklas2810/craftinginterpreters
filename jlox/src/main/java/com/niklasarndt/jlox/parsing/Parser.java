package com.niklasarndt.jlox.parsing;

import com.niklasarndt.jlox.Lox;
import com.niklasarndt.jlox.scanning.Token;
import com.niklasarndt.jlox.syntax.Expression;
import com.niklasarndt.jlox.util.TokenType;
import java.util.List;

public class Parser {

    private static class ParseError extends RuntimeException {}

    private final Lox runtime;
    private final List<Token> tokens;

    private int current = 0;

    public Parser(Lox runtime, List<Token> tokens) {
        this.runtime = runtime;
        this.tokens = tokens;
    }

    public Expression parse() {
        try {
            return expression();
        } catch (ParseError error) {
            return null;
        }
    }

    private Expression expression() {
        return equality();
    }

    private Expression equality() {
        Expression expr = comparison();

        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            Token operator = peekLast();
            Expression right = comparison();
            expr = new Expression.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expression comparison() {
        Expression expr = term();

        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL,
                TokenType.LESS, TokenType.LESS_EQUAL)) {
            Token operator = peekLast();
            Expression right = term();
            expr = new Expression.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expression term() {
        Expression expr = factor();

        while (match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = peekLast();
            Expression right = factor();
            expr = new Expression.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expression factor() {
        Expression expr = unary();

        while (match(TokenType.STAR, TokenType.SLASH)) {
            Token operator = peekLast();
            Expression right = unary();
            expr = new Expression.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expression unary() {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            Token operator = peekLast();
            Expression right = unary();
            return new Expression.Unary(operator, right);
        }

        return primary();
    }

    private Expression primary() {
        if (match(TokenType.FALSE)) return new Expression.Literal(false);
        if (match(TokenType.TRUE)) return new Expression.Literal(true);
        if (match(TokenType.NIL)) return new Expression.Literal(null);

        if (match(TokenType.NUMBER, TokenType.STRING))
            return new Expression.Literal(peekLast().literal);

        if(match(TokenType.LEFT_PAREN)) {
            Expression expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expected ')' after expression.");
            return new Expression.Grouping(expr);
        }

        throw error(peek(), "Expected expression.");
    }

    private Token consume(TokenType type, String errorMsg) {
        if(isNext(type))
            return advance();

        throw error(peek(), errorMsg);
    }

    private ParseError error(Token token, String message) {
        runtime.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();

        while (!reachedEnd()) {
            if (peekLast().type == TokenType.SEMICOLON) return;

            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }

            advance();
        }
    }

    private boolean match(TokenType... options) {
        for (TokenType t : options) {
            if (isNext(t)) {
                advance();
                return true;
            }
        }
        return false;

    }

    private Token advance() {
        if (!reachedEnd())
            ++current;
        return peekLast();
    }

    private boolean isNext(TokenType t) {
        if (reachedEnd())
            return false;
        return peek().type == t;
    }

    private boolean reachedEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token peekLast() {
        return tokens.get(current - 1);
    }

}
