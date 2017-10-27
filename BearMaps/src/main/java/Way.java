import java.util.ArrayList;

/**
 * Created by Kevin on 2017-04-17.
 */
public class Way {
    boolean isValid;
    public ArrayList<String> vertices;

    public Way(){
        vertices = new ArrayList<>();
        isValid = false;
    }
    public void addVertex(String id){
        vertices.add(id);
    }
}
