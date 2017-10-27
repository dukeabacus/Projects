package db;

import java.util.ArrayList;

/**
 * Created by Kevin on 2017-02-27.
 */
public class Row {
    int length;
    ArrayList<Value> values;

    Row(int l, ArrayList<Value> v) {
        length = l;
        values = v;
    }


    @Override
    public String toString() {
        String answer = "";
        for (int i = 0; i < length; i++) {
            String val = values.get(i).toString();
            if (i != length - 1) {
                answer = answer + val + ",";
            } else {
                answer = answer + val;
            }
        }
        answer += "\n";
        return answer;
    }

}
