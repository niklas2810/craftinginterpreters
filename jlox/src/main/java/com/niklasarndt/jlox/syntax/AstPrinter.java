package com.niklasarndt.jlox.syntax;

import com.niklasarndt.jlox.scanning.Token;
import com.niklasarndt.jlox.util.TokenType;

public class AstPrinter implements Expression.Visitor<String> {

    public String print(Expression expression) {
        return expression.accept(this);
    }

    protected String parenthesize(String name, Expression... expressions) {
        StringBuilder builder = new StringBuilder("(").append(name);

        for (Expression expr : expressions) {
            builder.append(" ").append(expr.accept(this));
        }

        return builder.append(")").toString();
    }

    @Override
    public String visitBinaryExpression(Expression.Binary expression) {
        return parenthesize(expression.operator.lexeme, expression.left, expression.right);
    }

    @Override
    public String visitGroupingExpression(Expression.Grouping expression) {
        return parenthesize("grouped", expression.expression);
    }

    @Override
    public String visitLiteralExpression(Expression.Literal expression) {
        return expression.value != null ? expression.value.toString() : "nil";
    }

    @Override
    public String visitUnaryExpression(Expression.Unary expression) {
        return parenthesize(expression.operator.lexeme, expression.right);
    }
}
