/**
 * Created by Kevin on 2017-04-16.
 */
public class QuadTree {
    public Node root;

    public void insert(double nWLong, double nWLat, double sELong, double sELat, String value) {
        root = insert(root, nWLong, nWLat,sELong,sELat, value,1);
    }

    private Node insert(Node h, double nWLong, double nWLat, double sELong, double sELat,String value, int depth) {
        if (h == null) { return new Node(nWLong, nWLat,sELong,sELat, value,depth); }
        double midLong = (h.nWLong + h.sELong)/2;
        double midLat = (h.nWLat + h.sELat)/2;
        Rectangle curRec = new Rectangle(nWLong,nWLat,sELong,sELat);
        Rectangle nW = new Rectangle(h.nWLong,h.nWLat,midLong,midLat);
        Rectangle nE = new Rectangle(midLong,h.nWLat,h.sELong,midLat);
        Rectangle sW = new Rectangle(h.nWLong,midLat,midLong,h.sELat);
        Rectangle sE = new Rectangle(midLong,midLat,h.sELong,h.sELat);

        if ( Rectangle.contains(nW,curRec)){ h.nW = insert(h.nW, nWLong, nWLat,sELong,sELat, value,depth+1); }
        else if ( Rectangle.contains(nE,curRec)){ h.nE = insert(h.nE, nWLong, nWLat,sELong,sELat, value,depth+1); }
        else if (Rectangle.contains(sW,curRec)){ h.sW = insert(h.sW, nWLong, nWLat,sELong,sELat, value,depth+1); }
        else if (Rectangle.contains(sE,curRec)){ h.sE = insert(h.sE, nWLong, nWLat,sELong,sELat, value,depth+1); }
        return h;
    }
}