package com.niklasarndt.jlox.syntax;

public class RPNAstPrinter extends AstPrinter {

    @Override
    protected String parenthesize(String name, Expression... expressions) {
        StringBuilder builder = new StringBuilder("");

        for (int i = 0; i < expressions.length; i++) {
            if(i > 0)
                builder.append(" ");
            builder.append(expressions[i].accept(this));
        }

        return builder.append(" ").append(name).toString();
    }
}
