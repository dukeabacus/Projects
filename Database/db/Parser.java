package db;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.util.StringJoiner;

public class Parser {
    // Various common constructs, simplifies parsing.
    private static final String REST = "\\s*(.*)\\s*",
            COMMA = "\\s*,\\s*",
            AND = "\\s+and\\s+";

    // Stage 1 syntax, contains the command name.
    private static final Pattern CREATE_CMD = Pattern.compile("create table " + REST),
            LOAD_CMD = Pattern.compile("load " + REST),
            STORE_CMD = Pattern.compile("store " + REST),
            DROP_CMD = Pattern.compile("drop table " + REST),
            INSERT_CMD = Pattern.compile("insert into " + REST),
            PRINT_CMD = Pattern.compile("print " + REST),
            SELECT_CMD = Pattern.compile("select " + REST);

    // Stage 2 syntax, contains the clauses of commands.
    private static final Pattern CREATE_NEW = Pattern.compile("(\\S+)\\s+\\((\\S+\\s+\\S+\\s*" +
            "(?:,\\s*\\S+\\s+\\S+\\s*)*)\\)"),
            SELECT_CLS = Pattern.compile("([^,]+?(?:,[^,]+?)*)\\s+from\\s+" +
                    "(\\S+\\s*(?:,\\s*\\S+\\s*)*)(?:\\s+where\\s+" +
                    "([\\w\\s+\\-*/'<>=!]+?(?:\\s+and\\s+" +
                    "[\\w\\s+\\-*/'<>=!]+?)*))?"),
            CREATE_SEL = Pattern.compile("(\\S+)\\s+as select\\s+" +
                    SELECT_CLS.pattern()),
            INSERT_CLS = Pattern.compile("(\\S+)\\s+values\\s+(.+?" +
                    "\\s*(?:,\\s*.+?\\s*)*)");


    private String message;
    String name;
    String cols[];
    String exprs;
    String tables;
    String conds;
    String expr;

    Parser() {

    }

    public static void main(String[] args) {

    }

    public String getMessage() {
        return message;
    }

    public void eval(String query) {
        Matcher m;
        if ((m = CREATE_CMD.matcher(query)).matches()) {
            createTable(m.group(1));
        } else if ((m = LOAD_CMD.matcher(query)).matches()) {
            loadTable(m.group(1));
        } else if ((m = STORE_CMD.matcher(query)).matches()) {
            storeTable(m.group(1));
        } else if ((m = DROP_CMD.matcher(query)).matches()) {
            dropTable(m.group(1));
        } else if ((m = INSERT_CMD.matcher(query)).matches()) {
            insertRow(m.group(1));
        } else if ((m = PRINT_CMD.matcher(query)).matches()) {
            printTable(m.group(1));
        } else if ((m = SELECT_CMD.matcher(query)).matches()) {
            select(m.group(1));
        } else {
            throw new RuntimeException("Malformed query");
        }
    }

    private void createTable(String expr) {
        Matcher m;
        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            createNewTable(m.group(1), m.group(2).split(COMMA));
        } else if ((m = CREATE_SEL.matcher(expr)).matches()) {
            createSelectedTable(m.group(1), m.group(2), m.group(3), m.group(4));
        } else {
            throw new RuntimeException("Malformed create");
        }
    }

    private void createNewTable(String name, String[] cols) {
        message = "createNewTable";
        this.name = name;
        this.cols = cols;

    }

    private void createSelectedTable(String name, String exprs, String tables, String conds) {
        message = "createNewTableSelect";
        this.name = name;
        this.exprs = exprs;
        this.tables = tables;
        this.conds = conds;
    }

    private void loadTable(String name) {
        message = "loadTable";
        this.name = name;
    }

    private void storeTable(String name) {
        message = "storeTable";
        this.name = name;
    }

    private void dropTable(String name) {
        message = "dropTable";
        this.name = name;
    }

    private void insertRow(String expr) {
        Matcher m = INSERT_CLS.matcher(expr);
        if (!m.matches()) {
            throw new RuntimeException("Malformed insert");
        }
        this.message = "insertRow";
        this.expr = expr;
    }

    private void printTable(String name) {
        this.message = "printTable";
        this.name = name;
    }

    private void select(String expr) {
        Matcher m = SELECT_CLS.matcher(expr);
        if (!m.matches()) {
            throw new RuntimeException("Malformed select");
        }

        select(m.group(1), m.group(2), m.group(3));
    }

    private void select(String exprs, String tables, String conds) {
        this.message = "select";
        this.exprs = exprs;
        this.tables = tables;
        this.conds = conds;
    }
}
