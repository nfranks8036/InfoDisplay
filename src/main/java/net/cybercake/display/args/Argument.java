package net.cybercake.display.args;

import java.io.Serializable;
import java.util.StringJoiner;

import static net.cybercake.display.utils.Assert.throwsException;

@SuppressWarnings({"unused"})
public class Argument implements Serializable {

    public static Argument DEFAULT_ARGUMENT = Argument.of(false);
    protected static Argument of(Object value) {
        return new Argument(value);
    }



    private final Object raw;
    private Argument(Object raw) {
        this.raw = raw;
    }

    public Object get() { return this.raw; }

    public String getAsString() { return this.raw.toString(); }
    public byte getAsByte() { return throwsException(() -> Byte.parseByte(getAsString()), NumberFormatException.class, (byte) -1); }
    public short getAsShort() { return throwsException(() -> Short.parseShort(getAsString()), NumberFormatException.class, (short) -1); }
    public int getAsInt() { return throwsException(() -> Integer.parseInt(getAsString()), NumberFormatException.class, -1); }
    public long getAsLong() { return throwsException(() -> Long.parseLong(getAsString()), NumberFormatException.class, (long) -1); }
    public float getAsFloat() { return throwsException(() -> Float.parseFloat(getAsString()), NumberFormatException.class, (float) -1); }
    public double getAsDouble() { return throwsException(() -> Double.parseDouble(getAsString()), NumberFormatException.class, (double) -1); }
    public boolean getAsBoolean() { return Boolean.parseBoolean(getAsString()); }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("value=\"" + getAsString().replace("\n", "{NEWLINE}") + "\"")
                .toString();
    }
}
