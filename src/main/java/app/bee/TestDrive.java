/*
 Put header here
 */
package app.bee;

import app.bee.exceptions.BeeException;

public class TestDrive {

    private static void testNumbers() throws BeeException {
        var bee = new Bee("(a == 1 OR a == -1 OR a == -.20)");
        bee.evaluate();
    }

    private static void testStrings() throws BeeException {
        var bee = new Bee("(a == '1' OR a == \"-1\" OR a == 'let\\'s go!!!!')");
        bee.evaluate();
    }

    private static void testBooleans() throws BeeException {
        var bee = new Bee("(a == tRuE OR a == FALSE or c = true)");
        bee.evaluate();
    }

    public static void main(String[] args) throws BeeException {
        //testNumbers();
        //testStrings();
        testBooleans();
    }
}
