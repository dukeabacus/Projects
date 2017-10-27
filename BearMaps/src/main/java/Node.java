/**
 * Created by Kevin on 2017-04-16.
 */
public class Node implements Comparable<Node> {
    double nWLong, nWLat, sELong, sELat;
    Node nW, nE, sE, sW;
    String value;
    int depth;
    Node(double nWLong, double nWLat, double sELong, double sELat, String value, int depth) {
        this.nWLong = nWLong;
        this.nWLat = nWLat;
        this.sELong = sELong;
        this.sELat = sELat;
        this.value = value;
        this.depth = depth;
    }

    public int compareTo(Node other) {
        return 0;
    }
}