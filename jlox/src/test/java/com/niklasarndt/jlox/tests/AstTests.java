package com.niklasarndt.jlox.tests;

import com.niklasarndt.jlox.scanning.Token;
import com.niklasarndt.jlox.syntax.AstPrinter;
import com.niklasarndt.jlox.syntax.Expression;
import com.niklasarndt.jlox.syntax.RPNAstPrinter;
import com.niklasarndt.jlox.util.TokenType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AstTests {

    @Test
    public void testPrinting() {

        //-123 * 45.67
        Expression expression = new Expression.Binary(
                new Expression.Unary(
                        new Token(TokenType.MINUS, "-", null, 1),
                        new Expression.Literal(123)),
                new Token(TokenType.STAR, "*", null, 1),
                new Expression.Grouping(
                        new Expression.Literal(45.67)));

        String out = new AstPrinter().print(expression);
        System.out.println(out);

        Assertions.assertEquals("(* (- 123) (grouped 45.67))", out);
    }

    @Test
    public void testRPNPrinting() {
        Expression expression = new Expression.Binary(
                new Expression.Binary(new Expression.Literal(1),
                        new Token(TokenType.PLUS, "+", null, 1),
                        new Expression.Literal(2)),
                new Token(TokenType.STAR, "*", null, 1),
                new Expression.Binary(new Expression.Literal(3),
                        new Token(TokenType.MINUS, "-", null, 1),
                        new Expression.Literal(4)
                ));

        String out = new RPNAstPrinter().print(expression);
        System.out.println(out);

        Assertions.assertEquals("1 2 + 3 4 - *", out);
    }


}
