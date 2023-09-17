package app.bee;

import org.junit.Assert;
import org.junit.Test;

public class CharReaderTest {

    @Test
    public void testHasNext() {
        int actual = 0;
        var input = "lorem ipsum, dolor sit amet!".toCharArray();
        int expected = input.length;

        var r = new CharReader(input);

        while (r.hasNext()) {
            r.next();
            actual++;
        }

        Assert.assertEquals("Iterator step mismatch", expected, actual);
    }

    @Test
    public void testPeekAndRewind() {
        char actual;
        var input = "lorem".toCharArray();
        var expected = input[input.length - 1];

        var r = new CharReader(input);

        while (r.hasNext()) {
            r.next();
        }
        r.rewind();
        actual = r.peek();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSkip() {
        char actual;
        var input = "lorem".toCharArray();
        var expected = input[input.length - 2];

        var r = new CharReader(input);

        while (r.hasNext()) {
            r.next();
        }
        r.skip(-2);
        actual = r.peek();
        Assert.assertEquals(expected, actual);
    }
}