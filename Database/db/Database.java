package db;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class Database {

    static Table temp;
    Parser p;
    private Map<String, Table> tables = new HashMap<>();

    public Database() {
        p = new Parser();
    }

    public String transact(String query) {

        try {
            p.eval(query);
        } catch (RuntimeException e) {
            return "ERROR: " + e.getMessage();
        }

        String message = p.getMessage();
        switch (message) {
            case "loadTable":
                return loadTable(p.name);
            case "printTable":
                return printTable(p.name);
            case "storeTable":
                return storeTable(p.name);
            case "dropTable":
                return dropTable(p.name);
            case "insertRow":
                return insertRow(p.expr);
            case "createNewTable":
                return createNewTable(p.name, p.cols);
            case "createNewTableSelect":
                return createNewSelectTable(p.name, p.exprs, p.tables, p.conds);
            case "select":
                return select(p.exprs, p.tables, p.conds);
            default:
                return "ERROR: not recognized";
        }
    }

    private String insertRow(String expr) {
        String[] vals = expr.split(" ");

        if (vals.length < 3) {
            return "ERROR: malformed insert expression";
        }

        if (!tables.containsKey(vals[0]) || !vals[1].equals("values")) {
            return "ERROR: table not found";
        }
        int index = expr.indexOf("values") + 7;
        String line = expr.substring(index);
        try {
            Table t = tables.get(vals[0]);
            ArrayList<Value> newRow =
                    Table.breakDownValueLines(line, t.rowTypes, ",");
            ArrayList<Row> rows = t.rows;
            rows.add(new Row(t.numCols, newRow));
            t.numRows += 1;
            tables.put(t.tableName, t);
        } catch (RuntimeException e) {
            return "ERROR: " + e.getMessage();
        }
        return "";
    }

    private String dropTable(String name) {
        try {

            if (!tables.containsKey(name)) {
                throw new RuntimeException("table not in memory");
            }

            tables.remove(name);
        } catch (RuntimeException e) {
            return "ERROR: " + e.getMessage();
        }
        return "";
    }


    private String createNewTable(String name, String[] cols) {
        try {
            ArrayList<String> names = new ArrayList<>();
            ArrayList<String> types = new ArrayList<>();

            for (String str : cols) {
                String[] tempStr = str.split("\\s+");
                names.add(tempStr[0].trim());
                types.add(tempStr[1].trim());
            }
            /*
            for(String s: names){
                System.out.print(s + " ");
            }
            System.out.println();
            for(String s: types){
                System.out.print(s + " ");
            }
            System.out.println();
            */
            if (!Table.validTypes(types)) {
                return "ERROR: Invalid column types";
            }
            if (!Table.validNames(names)) {
                return "ERROR: Invalid column names";
            }
            Table tempTbl = new Table(name, names, types);
            tempTbl.numCols = names.size();
            tempTbl.numRows = 0;
            tables.put(name, tempTbl);
            return "";
        } catch (RuntimeException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    private String createNewSelectTable(String name,
                                        String expr, String tbls, String conds) {
        select(expr, tbls, conds);
        tables.put(name, new Table(name, Database.temp));
        return "";
    }

    private String findCondOperator(String s) {
        if (s.contains("==")) {
            return "==";
        }
        if (s.contains(">=")) {
            return ">=";
        }
        if (s.contains("<=")) {
            return "<=";
        }
        if (s.contains(">")) {
            return ">";
        }
        if (s.contains("<")) {
            return "<";
        }
        if (s.contains("!=")) {
            return "!=";
        }
        return "NotFound";
    }


    private String select(String expr, String tbls, String conds) {
        try {
            String[] tempStrs = tbls.split(",");
            Table ans = null;
            System.out.println(tables.get(tempStrs[0]));

            for (String s : tempStrs) {
                s = s.trim();
                if (!tables.containsKey(s)) {
                    return "ERROR: table not found";
                }
                if (ans == null) {
                    ans = tables.get(s);
                } else {
                    ans = Table.join(ans, tables.get(s));
                }
            }
            String[] exprs = expr.split(",");
            if (!(exprs.length == 1 && exprs[0].trim().equals("*"))) {
                ArrayList<Clause> selectClauses = new ArrayList<>();
                ArrayList<String> newTableNames = new ArrayList<>();
                ArrayList<String> newTableTypes = new ArrayList<>();
                for (String s : exprs) {
                    int indOfOperator = findOperator(s);
                    if (indOfOperator != -1) {
                        String operator = Character.toString(s.charAt(indOfOperator));
                        String operand1Str = s.substring(0, indOfOperator).trim();
                        String theRest = s.substring(indOfOperator + 1).trim();
                        String[] leftOver = theRest.split("\\s+");
                        if (leftOver.length != 3) {
                            return "ERROR: malformed select clause";
                        }
                        String operand2Str = leftOver[0].trim();
                        String newColumnName = leftOver[2].trim();
                        int operand1 = findMatchingColumn(operand1Str, ans);
                        int operand2 = findMatchingColumn(operand2Str, ans);
                        if (operand1 == -1 || operand2 == -1) {
                            return "ERROR: cannot find column in select clause";
                        }
                        String newColumnType = findColumnType(ans, operand1, operand2);
                        selectClauses.add(new Clause(operand1, operand2, operator));
                        newTableNames.add(newColumnName);
                        newTableTypes.add(newColumnType);
                    } else {
                        String operand1Str = s.trim();
                        int operand1 = findMatchingColumn(operand1Str, ans);
                        if (operand1 == -1) {
                            return "ERROR: cannot find column in select clause";
                        }
                        selectClauses.add(new Clause(operand1));
                        newTableNames.add(ans.rowNames.get(operand1));
                        newTableTypes.add(ans.rowTypes.get(operand1));
                    }
                }
                ans = Table.handleSelectClauses(ans,
                        newTableNames, newTableTypes, selectClauses);
            }
            if (conds != null) {
                ArrayList<Clause> selectClauses = new ArrayList<>();
                String[] array = conds.split("and");
                for (String s : array) {
                    selectClauses = findCondClauses(s, selectClauses, ans);
                }
                ans = Table.handleCondClauses(ans, selectClauses);
            }
            Database.temp = ans;
            return ans.toString();
        } catch (RuntimeException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    private ArrayList<Clause> findCondClauses(String s, ArrayList<Clause> sc, Table ans) {
        String operator = findCondOperator(s);
        if (operator.equals("NotFound")) {
            throw new RuntimeException("wrong condition clause form");
        }
        String[] operands = s.split(operator);
        if (operands.length != 2) {
            throw new RuntimeException("ERROR: wrong condition clause form");
        }
        int operand1 = findMatchingColumn(operands[0].trim(), ans);
        if (operand1 == -1) {
            throw new RuntimeException("ERROR: cannot find column in condition clause");
        }
        int operand2 = findMatchingColumn(operands[1].trim(), ans);
        if (operand2 == -1) {
            String compType = ans.rowTypes.get(operand1);
            if (!Table.typeIsValid(compType, operands[1].trim())) {
                throw new RuntimeException("ERROR: invalid literal type in condition clause");
            }
            sc.add(new Clause(operand1,
                    new Value(compType, operands[1].trim()), operator, true));
        } else {
            System.out.println(operand1 + " " + operand2);
            sc.add(new Clause(operand1, operand2, operator));
        }
        return sc;
    }


    private boolean condOpIsValid(String s) {
        return s.equals("==") || s.equals("!=") || s.equals("<")
                || s.equals(">") || s.equals("<=") || s.equals(">=");
    }


    private String findColumnType(Table t, int ind1, int ind2) {
        String type1 = t.rowTypes.get(ind1);
        String type2 = t.rowTypes.get(ind2);

        if (type1.equals("string") && (type2.equals("int") || type2.equals("float"))) {
            return "Error";
        } else if (type2.equals("string") && (type1.equals("int") || type1.equals("float"))) {
            return "Error";
        } else if (type1.equals("float") || type2.equals("float")) {
            return "float";
        } else if (type1.equals("int") && type2.equals("int")) {
            return "int";
        } else {
            return "string";
        }

    }

    private int findMatchingColumn(String s, Table t) {
        for (int i = 0; i < t.numCols; i++) {
            if (s.equals(t.rowNames.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private int findOperator(String s) {
        if (s.contains("+")) {
            return s.indexOf('+');
        } else if (s.contains("-")) {
            return s.indexOf('-');
        } else if (s.contains("*")) {
            return s.indexOf("*");
        } else if (s.contains("/")) {
            return s.indexOf("/");
        }
        return -1;
    }

    private String loadTable(String name) {
        try {
            tables.put(name, new Table(name));
        } catch (IllegalArgumentException e) {
            return "ERROR: file not found";
        } catch (RuntimeException e) {
            return "ERROR: " + e.getMessage();
        }
        return "";
    }

    private String printTable(String name) {
        try {
            return tables.get(name).toString();
        } catch (RuntimeException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    private String storeTable(String name) {

        if (!tables.containsKey(name)) {
            return "ERROR: table not found";
        }

        try {
            tables.get(name).saveToFile(name);
        } catch (RuntimeException e) {
            return "ERROR: " + e.getMessage();
        }
        return "";
    }

}
