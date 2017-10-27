/**
 * Created by Kevin on 2017-04-17.
 */
public class SearchNode implements Comparable {
    String id;
    double distance;



    public SearchNode (String id, double distance) {
        this.id = id;
        this.distance = distance;
    }
    @Override
    public int compareTo(Object other) {
        SearchNode o = (SearchNode) other;
        if (distance-o.distance > 0) return 1;
        else if (distance - o.distance < 0) return -1;
        else return 0;
    }

}
