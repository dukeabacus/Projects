package db;

/**
 * Created by Kevin on 2017-02-27.
 */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.List;

public class Table {
    String tableName;
    ArrayList<String> rowNames;
    ArrayList<String> rowTypes;
    ArrayList<Row> rows = new ArrayList<>();
    int numCols;
    int numRows;

    Table(String tn, ArrayList<String> rn, ArrayList<String> rt) {
        tableName = tn;
        rowNames = rn;
        rowTypes = rt;
    }

    Table(String tn, Table t) {
        tableName = tn;
        rowNames = t.getRowNames();
        rowTypes = t.getRowTypes();
        rows = t.getRows();
        numCols = t.numCols;
        numRows = t.numRows;
    }


    public static boolean validTypes(ArrayList<String> types) {
        for (String s : types) {
            if (!s.equals("float") && !s.equals("int") && !s.equals("string")) {
                return false;
            }
        }
        return true;
    }

    public static boolean validNames(ArrayList<String> names) {
        for (String s : names) {

            if (s.length() == 0 || (s.charAt(0) >= 48 && s.charAt(0) <= 57)) {
                throw new RuntimeException("empty column name or column name starts with number");
            }

            for (int i = 0; i < s.length(); i++) {
                int asciiVal = (int) s.charAt(i);
                if (!((asciiVal >= 48 && asciiVal <= 57) || (asciiVal >= 65 && asciiVal <= 90) || (asciiVal >= 97 && asciiVal <= 122))) {
                    throw new RuntimeException("special characters in column name");
                }
            }


            try {
                int num = Integer.parseInt(s);
                return false;
            } catch (NumberFormatException e) {
                continue;
            }
        }
        return true;
    }

    Table(String str) {
        String fileName = str + ".tbl";
        In scanner = new In(fileName);
        String firstLine = scanner.readLine();
        Pair<ArrayList<String>, ArrayList<String>> nameAndType = breakDown(firstLine);
        ArrayList<String> names = nameAndType.getL();
        ArrayList<String> types = nameAndType.getR();

        if (!validTypes(types)) {
            throw new RuntimeException("ERROR: Invalid Table Types");
        }
        if (!validNames(names)) {
            throw new RuntimeException("ERROR: Invalid Table Names");
        }

        if (!(types.size() == names.size())) {
            throw new RuntimeException("ERROR: Unequal type and name length");
        }

        ArrayList<Row> rows = new ArrayList<>();
        int numCols = names.size();
        int numRows = 0;

        while (scanner.hasNextLine()) {
            String curLine = scanner.readLine();
            ArrayList<Value> curVals = breakDownValueLines(curLine, types, ",");
            rows.add(new Row(types.size(), curVals));
            numRows++;
        }
        rowNames = names;
        rowTypes = types;
        this.rows = rows;
        this.numCols = numCols;
        this.numRows = numRows;
    }

    /* helper function that takes in the first line read and breaks it down into names and types */
    public static Pair<ArrayList<String>, ArrayList<String>> breakDown(String firstLine) {
        String temp = "";
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> types = new ArrayList<>();
        for (int i = 0; i < firstLine.length(); i++) {
            char curChar = firstLine.charAt(i);
            if (curChar == ' ') {
                names.add(temp);
                temp = "";
            } else if (curChar == ',') {
                types.add(temp);
                temp = "";
            } else {
                temp += curChar;
            }
        }
        types.add(temp);
        return new Pair(names, types);
    }

    public void saveToFile(String name) {
        Out writer = new Out(name + ".tbl");
        writer.print(toString());
        writer.close();
    }

    public static boolean compareDouble(Value a, Value b, String operator){
        if(a.str.equals("NaN") && b.str.equals("NaN")){
            if(operator.equals(">=") || operator.equals("<=") || operator.equals("==")){
                return true;
            }else {
                return false;
            }
        }

        if(a.str.equals("NaN")){
            if(operator.equals(">=") || operator.equals(">") || operator.equals("!=")){
                return true;
            }else {
                return false;
            }
        }
        if(b.str.equals("NaN")){
            if(operator.equals("<=") || operator.equals("<") || operator.equals("!=")){
                return true;
            }else {
                return false;
            }
        }
        double vA = Double.parseDouble(a.str);
        double vB = Double.parseDouble(b.str);

        switch (operator) {
            case "==":
                return vA == vB;
            case ">=":
                return vA >= vB;
            case "<=":
                return vA <= vB;
            case ">":
                return vA > vB;
            case "<":
                return vA < vB;
            default:
                return vA != vB;
        }
    }
    public static boolean compareInt(Value a, Value b, String operator){
        if(a.str.equals("NaN") && b.str.equals("NaN")){
            if(operator.equals(">=") || operator.equals("<=") || operator.equals("==")){
                return true;
            }else {
                return false;
            }
        }

        if(a.str.equals("NaN")){
            if(operator.equals(">=") || operator.equals(">") || operator.equals("!=")){
                return true;
            }else {
                return false;
            }
        }
        if(b.str.equals("NaN")){
            if(operator.equals("<=") || operator.equals("<") || operator.equals("!=")){
                return true;
            }else {
                return false;
            }
        }
        int vA = Integer.parseInt(a.str);
        int vB = Integer.parseInt(b.str);

        switch (operator) {
            case "==":
                return vA == vB;
            case ">=":
                return vA >= vB;
            case "<=":
                return vA <= vB;
            case ">":
                return vA > vB;
            case "<":
                return vA < vB;
            default:
                return vA != vB;
        }
    }

    public static boolean compare(Value v1, Value v2, String operator) {

        //NOVALUE case
        if (v1.str.equals("NOVALUE") || v2.str.equals("NOVALUE")) {
            return false;
        }

        // String
        if (v1.type.equals("string")) {
            String strV1 = v1.str;
            String strV2 = v2.str;
            switch (operator) {
                case "==":
                    return strV1.equals(strV2);
                case ">=":
                    return strV1.compareTo(strV2) >= 0;
                case "<=":
                    return strV1.compareTo(strV2) <= 0;
                case ">":
                    return strV1.compareTo(strV2) > 0;
                case "<":
                    return strV1.compareTo(strV2) < 0;
                default:
                    return strV1.compareTo(strV2) != 0;
            }
        } else if (v1.type.equals("float") || v2.type.equals("float")) {
            double strV1, strV2;
            return compareDouble(v1,v2,operator);
        } else {
            return compareInt(v1,v2,operator);
        }
    }


    public static boolean meetsCondition(Row r, Clause c) {
        boolean unary = c.unary;
        String operator = c.operator;
        if (unary) {
            int index = c.operand1;
            Value compVal = c.operand3;
            return compare(r.values.get(index), compVal, operator);
        } else {
            int index1 = c.operand1;
            int index2 = c.operand2;
            return compare(r.values.get(index1), r.values.get(index2), operator);
        }

    }


    public static Table handleCondClauses(Table t, ArrayList<Clause> clauses) {
        int numRows = 0;
        ArrayList<Row> newRow = new ArrayList<>();
        for (Row r : t.rows) {
            boolean add = true;
            for (Clause c : clauses) {

                if (!meetsCondition(r, c)) {
                    add = false;
                    break;
                }
            }
            if (add) {
                newRow.add(r);
                numRows += 1;
            }
        }
        t.rows = newRow;
        t.numRows = numRows;
        return t;
    }


    public static boolean typeIsValid(String type, String val) {

        if (val.equals("") || val.equals("NOVALUE") || val.equals("NaN")) {
            return true;
        }

        switch (type) {
            case "string":
                return val.charAt(0) == '\'' && val.charAt(val.length() - 1) == '\'';
            case "int":
                try {
                    int i = Integer.parseInt(val);
                } catch (NumberFormatException e) {
                    return false;
                }
                return true;
            case "float":
                if (!(val.indexOf('.') != -1 && val.indexOf('.') == val.lastIndexOf('.'))) {
                    return false;
                }
                try {
                    Double.parseDouble(val);
                } catch (NumberFormatException e) {
                    return false;
                }
                return true;
            default:
                return true;
        }
    }

    public static Table handleSelectClauses(Table t, ArrayList<String> names, ArrayList<String> types, ArrayList<Clause> clauses) {

        Table ans = new Table("name", names, types);
        ans.numCols = names.size();
        ans.numRows = t.numRows;

        for (int i = 0; i < ans.numRows; i++) {
            ArrayList<Value> curVals = t.rows.get(i).values;
            ArrayList<Value> tempV = new ArrayList<>();
            for (Clause c : clauses) {
                if (c.operator == null) {
                    tempV.add(curVals.get(c.operand1));
                } else {
                    Value v = operate(c.operator, curVals.get(c.operand1), curVals.get(c.operand2));
                    tempV.add(v);
                }
            }
            ans.rows.add(new Row(ans.numCols, tempV));
        }
        return ans;
    }


    public static Value operate(String operator, Value v1, Value v2) {

        //If both have no value
        if (v1.str.equals("NOVALUE") && v2.str.equals("NOVALUE")) {
            return new Value("string", "NOVALUE");
        }
        // String case

        if (v1.type.equals("string") && !operator.equals("+")) {
            throw new RuntimeException("can only add two strings");
        } else if (v1.type.equals("string")) {
            String strV1, strV2;

            if (v1.str.equals("NOVALUE")) {
                strV1 = "'";
            } else {
                strV1 = v1.str.substring(0, v1.str.length() - 1);
            }
            if (v2.str.equals("NOVALUE")) {
                strV2 = "'";
            } else {
                strV2 = v2.str.substring(1);
            }
            return new Value("string", strV1 + strV2);
        }
        // The NaN case
        if (v1.str.equals("NaN") || v2.str.equals("NaN")) {
            return new Value("int", "NaN");
        }

        // Integer and Integer
        if (v1.type.equals("int") && v2.type.equals("int")) {
            int intV1, intV2;
            if (v1.str.equals("NOVALUE")) {
                intV1 = 0;
            } else {
                intV1 = Integer.parseInt(v1.str);
            }
            if (v2.str.equals("NOVALUE")) {
                intV2 = 0;
            } else {
                intV2 = Integer.parseInt(v2.str);
            }
            return new Value("int", integerOperation(operator, intV1, intV2));
        }
        //Float
        double dV1, dV2;
        if (v1.str.equals("NOVALUE")) {
            dV1 = 0;
        } else {
            dV1 = Double.parseDouble(v1.str);
        }
        if (v2.str.equals("NOVALUE")) {
            dV2 = 0;
        } else {
            dV2 = Double.parseDouble(v2.str);
        }
        return new Value("float", floatOperation(operator, dV1, dV2));
    }

    public static String floatOperation(String operator, double dV1, double dV2) {
        switch (operator) {
            case "+":
                return String.format("%1$,.3f", dV1 + dV2).replaceAll(",", "");
            case "-":
                return String.format("%1$,.3f", dV1 - dV2).replaceAll(",", "");
            case "*":
                return String.format("%1$,.3f", dV1 * dV2).replaceAll(",", "");
            default:
                if (Math.abs(dV2 - 0) < 0.0001) {
                    return "NaN";
                } else {
                    return String.format("%1$,.3f", dV1 / dV2).replaceAll(",", "");
                }
        }
    }


    public static String integerOperation(String operator, int v1, int v2) {
        switch (operator) {
            case "+":
                return Integer.toString(v1 + v2).replaceAll(",", "");
            case "-":
                return Integer.toString(v1 - v2).replaceAll(",", "");
            case "*":
                return Integer.toString(v1 * v2).replaceAll(",", "");
            default:
                if (v2 == 0) {
                    return "NaN";
                } else {
                    return Integer.toString(v1 / v2).replaceAll(",", "");
                }
        }
    }

    /* helper function that takes in the remaining lines read and breaks it down into list of values */
    public static ArrayList<Value> breakDownValueLines(String curLine, ArrayList<String> types, String delimiter) {
        int typeInd = 0;
        ArrayList<Value> values = new ArrayList<>();
        String[] str = curLine.split(delimiter);

        if (str.length != types.size()) {
            throw new RuntimeException("ERROR: values length does not match column length");
        }
        for (String s : str) {
            String curType = types.get(typeInd);
            if (!typeIsValid(curType, s)) {
                throw new RuntimeException("Table value does not match type");
            }
            values.add(new Value(curType, s));
            typeInd++;
        }
        return values;
    }


    @Override
    /* prints out string representation of a table */
    public String toString() {
        String answer = "";
        for (int i = 0; i < numCols; i++) {
            if (i != numCols - 1) {
                answer = answer + rowNames.get(i) + " " + rowTypes.get(i) + ",";
            } else {
                answer = answer + rowNames.get(i) + " " + rowTypes.get(i);
            }
        }
        answer += "\n";
        for (int i = 0; i < numRows; i++) {
            answer += rows.get(i).toString();
        }
        return answer;
    }

    /* helper function that returns a table with t1 multiplied by t2 */
    public static Table joinAll(Table t1, Table t2) {

        ArrayList<String> AllRowNames = new ArrayList<String>(t1.rowNames);
        AllRowNames.addAll(t2.rowNames);
        ArrayList<String> AllRowTypes = new ArrayList<String>(t1.rowTypes);
        AllRowTypes.addAll(t2.rowTypes);

        Table temp = new Table("tempName", AllRowNames, AllRowTypes);
        temp.numRows = t1.numRows * t2.numRows;
        temp.numCols = t1.numCols + t2.numCols;

        for (int i = 0; i < t1.numRows; i++) {
            for (int j = 0; j < t2.numRows; j++) {
                Row row1 = t1.rows.get(i);
                Row row2 = t2.rows.get(j);
                int totalLength = row1.length + row2.length;
                ArrayList<Value> allValues = new ArrayList<>(row1.values);
                allValues.addAll(row2.values);
                temp.rows.add(new Row(totalLength, allValues));
            }
        }
        return temp;
    }

    public static ArrayList<Pair<Integer, Integer>> findIndices(Table t1, Table t2) {
        ArrayList<Pair<Integer, Integer>> answer = new ArrayList<Pair<Integer, Integer>>();
        for (int i = 0; i < t1.numCols; i++) {
            for (int j = 0; j < t2.numCols; j++) {
                if (t1.rowNames.get(i).equals(t2.rowNames.get(j))) {
                    answer.add(new Pair(i, t1.numCols + j));
                }
            }
        }
        return answer;
    }


    public static Table filterTable(Table temp, ArrayList<Pair<Integer, Integer>> IndOfMatchingNames, int t2StartInd) {

        /* create a new table that stores the final solution, first add all names and types in indofmatchingnames, then
        add the remaining ones in t1, and then add the remaining ones in t2
         */
        ArrayList<String> t3RowNames = new ArrayList<String>();
        ArrayList<String> t3RowTypes = new ArrayList<String>();
        ArrayList<Row> rows = new ArrayList<>();
        int newNumCols = temp.numCols - IndOfMatchingNames.size();
        int newNumRows = 0;

        boolean flag[] = new boolean[temp.numCols];
        for (Pair<Integer, Integer> p : IndOfMatchingNames) {
            t3RowNames.add(temp.rowNames.get(p.getL()));
            t3RowTypes.add(temp.rowTypes.get(p.getL()));
            flag[p.getL()] = true;
            flag[p.getR()] = true;
        }
        for (int i = 0; i < temp.numCols; i++) {
            if (flag[i] == false) {
                t3RowNames.add(temp.rowNames.get(i));
                t3RowTypes.add(temp.rowTypes.get(i));
            }
        }


        for (Row row : temp.rows) {
            boolean add = true;
            ArrayList<Value> tempV = new ArrayList<>();
            boolean flag2[] = new boolean[temp.numCols];
            for (int i = 0; i < IndOfMatchingNames.size(); i++) {
                int ind1 = IndOfMatchingNames.get(i).getL();
                int ind2 = IndOfMatchingNames.get(i).getR();

                if (!row.values.get(ind1).str.equals(row.values.get(ind2).str)) {
                    add = false;
                    break;
                } else {
                    tempV.add(row.values.get(ind1));
                    flag2[ind1] = true;
                    flag2[ind2] = true;
                }
            }

            if (add) {
                for (int i = 0; i < temp.numCols; i++) {
                    if (flag2[i] == false) {
                        tempV.add(row.values.get(i));
                    }
                }
                rows.add(new Row(newNumCols, tempV));
                newNumRows++;
            }

        }
        Table answer = new Table("finalTable", t3RowNames, t3RowTypes);
        answer.rows = rows;
        answer.numCols = newNumCols;
        answer.numRows = newNumRows;
        return answer;
    }


    public static Table join(Table t1, Table t2) {
        Table temp = joinAll(t1, t2);
        ArrayList<Pair<Integer, Integer>> IndOfMatchingNames = findIndices(t1, t2);
    /*
        for (Pair<Integer,Integer> p : IndOfMatchingNames){
            System.out.println(p.getL() + " " + p.getR());
        }
    */
        int t1NumCols = t1.numCols;

        if (IndOfMatchingNames.size() == 0) {
            return temp;
        } else if (IndOfMatchingNames.size() == t1.numCols && t1.numCols == t2.numCols) {
            return t1;
        } else {
            temp = filterTable(temp, IndOfMatchingNames, t1NumCols);
        }
        return temp;
    }


    /* returns a copy of the row names */
    public ArrayList<String> getRowNames() {
        return new ArrayList<String>(rowNames);
    }

    /* returns a copy of the row types */
    public ArrayList<String> getRowTypes() {
        return new ArrayList<String>(rowTypes);
    }

    /* returns a copy of the rows */
    public ArrayList<Row> getRows() {
        return new ArrayList<Row>(rows);
    }

    /* returns table name */
    public String getTableName() {
        return tableName;
    }

    /* returns number of columns */
    public int getNumCols() {
        return numCols;
    }

    /* returns number of rows */
    public int getNumRows() {
        return numRows;
    }


}
