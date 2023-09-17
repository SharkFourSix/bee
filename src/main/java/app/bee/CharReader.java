package app.bee;

public class CharReader {
    private final char[] buf;
    private int pos, total;

    public CharReader(char[] buf) {
        this.buf = new char[buf.length];
        this.total = buf.length;
        System.arraycopy(buf, 0, this.buf, 0, buf.length);
    }

    public boolean hasNext() {
        return pos < total;
    }

    public void rewind() {
        this.pos--;
    }

    public void skip(int n) {
        this.pos += n;
    }

    public char next() {
        return this.buf[this.pos++];
    }

    public char peek() {
        return this.buf[this.pos];
    }

    public char peekBack() {
        return this.buf[this.pos - 2];
    }

    public int position() {
        return this.pos;
    }
}
