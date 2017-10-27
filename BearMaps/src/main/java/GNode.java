import java.util.HashSet;
import java.util.Set;

/**
 * Created by Kevin on 2017-04-17.
 */
public class GNode {
    String id, lon, lat;
    Set<Long> adj;

    public GNode(String id, String lon, String lat){
        this.id = id;
        this.lon = lon;
        this.lat = lat;
        adj = new HashSet<>();
    }

    public void connectNode(String id) {
        adj.add(Long.parseLong(id));
    }

}
