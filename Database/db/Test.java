package db;

/**
 * Created by Kevin on 2017-03-03.
 */
public class Test {
    public static void main(String[] args){
        try
        {
            Double.parseDouble("1.645");
        }
        catch(NumberFormatException e)
        {
            System.out.println("fucked");
        }
    }
}
