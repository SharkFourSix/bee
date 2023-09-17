package app.bee;

import app.bee.exceptions.BeeException;

public class Bee {
    private final Parser parser;
    private final Options[] options;
    private final Tokenizer tokenizer;

    public Bee(String expression) {
        this.parser = new Parser();
        this.options = new Options[]{};
        this.tokenizer = new Tokenizer(expression);
    }

    public boolean evaluate() throws BeeException {
        var tokens = tokenizer.tokenize();
        if (tokens.isEmpty()) {
            return false;
        }
        while (!tokens.isEmpty()) {
           System.out.println(tokens.removeLast());
        }
        return false;
    }
}
