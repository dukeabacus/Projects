
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map;
import java.util.TreeSet;
/**
 * Created by Kevin on 2017-04-16.
 */
public class Query {
    QuadTree qt;
    Map<String, Double> params;
    TreeMap<Double, TreeSet<Pair<Double,String> > > listOfImgs;
    double qLongDPP;
    int maxDepth = 0;
    public static final double ROOT_ULLAT = 37.892195547244356, ROOT_ULLON = -122.2998046875,
            ROOT_LRLAT = 37.82280243352756, ROOT_LRLON = -122.2119140625;
    double raster_ul_lon = 99999,raster_ul_lat = -99999,raster_lr_lon = -99999,raster_lr_lat = 99999;
    String imgRoot;
    Query(QuadTree qt, Map<String, Double> params,String imgRoot) {
        this.imgRoot = imgRoot;
        this.qt = qt;
        this.params = params;
        listOfImgs = new TreeMap();
        qLongDPP = (params.get("lrlon") - params.get("ullon"))/params.get("w");
    }

    public Map<String, Object> solve(){
        Node curNode = qt.root;
        findAllImg(curNode);
        Map<String, Object> result = new TreeMap();
        ArrayList<ArrayList<String> > ans = new ArrayList();

        String[][] finalAns = new String[listOfImgs.size()][];
        int i = listOfImgs.size()-1;
        for(Map.Entry<Double,TreeSet<Pair<Double,String> >> entry : listOfImgs.entrySet()) {
            Double key = entry.getKey();
            TreeSet<Pair<Double, String>> values = entry.getValue();
            if (key > raster_ul_lat) raster_ul_lat = key;
            if (key < raster_lr_lat) raster_lr_lat = key;

            String[] temp = new String[values.size()];
            int j = 0;
            for (Pair<Double, String> p : values) {
                double lon = p.getLeft();
                String s = p.getRight();
                temp[j] = imgRoot + s + ".png";
                j += 1;
                if (lon > raster_lr_lon) raster_lr_lon = lon;
                if (lon < raster_ul_lon) raster_ul_lon = lon;
            }
            finalAns[i] = temp;
            i--;
        }

        if(finalAns.length!=0) {
            result.put("query_success", true);
            result.put("depth", maxDepth-1);
            result.put("render_grid", finalAns);
            result.put("raster_ul_lon", raster_ul_lon);
            result.put("raster_lr_lon", (raster_lr_lon - (ROOT_ULLON - ROOT_LRLON) / Math.pow(2, maxDepth - 1)));
            result.put("raster_ul_lat", raster_ul_lat);
            result.put("raster_lr_lat", (raster_lr_lat - (ROOT_ULLAT - ROOT_LRLAT) / Math.pow(2, maxDepth - 1)));
        }
        else {
            result.put("query_success", false);
        }
        return result;
    }


    public boolean containsImg(Node n, Map<String, Double> params ){
        double l1x = n.nWLong;
        double l1y = n.nWLat;
        double r1x = n.sELong;
        double r1y = n.sELat;

        double l2x = params.get("ullon");
        double l2y = params.get("ullat");
        double r2x = params.get("lrlon");
        double r2y = params.get("lrlat");

        if (l1x > r2x || l2x > r1x)
            return false;

        if (l1y < r2y || l2y < r1y)
            return false;

        return true;
    }

    public double calcDPP(Node n){
        return (n.sELong - n.nWLong)/256.0;
    }

    public void findAllImg(Node curNode){
        if (containsImg(curNode, params) ) {
            double longDPP = calcDPP(curNode);
            if (longDPP > qLongDPP && curNode.depth != 8) {
                findAllImg (curNode.nE);
                findAllImg (curNode.nW);
                findAllImg (curNode.sE);
                findAllImg (curNode.sW);
            } else{
                if (!listOfImgs.containsKey(curNode.nWLat) ) {
                    TreeSet<Pair<Double,String> > temp = new TreeSet();
                    temp.add(new Pair(curNode.nWLong,curNode.value));
                    listOfImgs.put(curNode.nWLat,temp);
                } else {
                    TreeSet<Pair<Double,String> > temp = listOfImgs.get(curNode.nWLat);
                    temp.add(new Pair(curNode.nWLong,curNode.value));
                    listOfImgs.put(curNode.nWLat,temp);
                }
                if (curNode.depth > maxDepth) maxDepth = curNode.depth;
            }
        }
    }

}
