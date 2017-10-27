package db;

/**
 * Created by Kevin on 2017-02-27.
 */

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class TablePrintTest {
    @Test
    public static void TestPrintTable(Table t) {

        String expected = "Apple string,Banana int,Peach float,\n'apple',9,'1.93\n'banana',3,2.33\n'peach',7,5.43\n";
        assertEquals(expected, t.toString());
    }

    @Test
    public static void TestJoinAll(Table t, Table s) {

        String expected = "Apple string,Banana int,Peach float,Apple string,Banana int,Peach float,\n" +
                "'apple',9,'1.93,'apple',9,'1.93\n" +
                "'apple',9,'1.93,'banana',3,2.33\n" +
                "'apple',9,'1.93,'peach',7,5.43\n" +
                "'banana',3,2.33,'apple',9,'1.93\n" +
                "'banana',3,2.33,'banana',3,2.33\n" +
                "'banana',3,2.33,'peach',7,5.43\n" +
                "'peach',7,5.43,'apple',9,'1.93\n" +
                "'peach',7,5.43,'banana',3,2.33\n" +
                "'peach',7,5.43,'peach',7,5.43\n";

        assertEquals(expected, Table.joinAll(t, s));
    }

    public static void main(String[] args) {
        // create a new table t
        /*-------------------------------------------------------------------------------------------*/
        String tableName = "BobleBlob";
        ArrayList<String> rowNames = new ArrayList<String>(
                Arrays.asList("Apple", "Banana", "Peach"));
        ArrayList<String> rowTypes = new ArrayList<String>(
                Arrays.asList("string", "int", "float"));
        ArrayList<Row> rows = new ArrayList<Row>();

        rows.add(new Row(3, new ArrayList<Value>(
                Arrays.asList(new Value("string", "apple"), new Value("int", "9"), new Value("float", "'1.93")))));
        rows.add(new Row(3, new ArrayList<Value>(
                Arrays.asList(new Value("string", "banana"), new Value("int", "3"), new Value("float", "2.33")))));
        rows.add(new Row(3, new ArrayList<Value>(
                Arrays.asList(new Value("string", "peach"), new Value("int", "7"), new Value("float", "5.43")))));

        Table t = new Table(tableName, rowNames, rowTypes);
        t.rows = rows;
        t.numCols = 3;
        t.numRows = 3;
        /*--------------------------------------------------------------------------------------------*/

        TestPrintTable(t);
        Table s = new Table("BlobleBob", t);

        Table p = Table.join(t, s);
        System.out.println(p);

        //   TestJoinAll(t,s);
    }
}
