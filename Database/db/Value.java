package db;

/**
 * Created by Kevin on 2017-02-27.
 */
public class Value {

    String type;
    String str;

    Value(String t, String v) {
        type = t;
        str = v;
    }

    @Override
    public String toString() {

        if (str.equals("NaN") || str.equals("NOVALUE")) {
            return str;
        }
        if (type.equals("float")) {
            double d = Double.parseDouble(str);
            return String.format("%1$,.3f", d).replaceAll(",", "");
        }
        return str;
    }
}
