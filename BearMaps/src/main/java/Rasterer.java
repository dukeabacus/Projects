import java.util.Map;
import java.util.LinkedList;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    // Recommended: QuadTree instance variable. You'll need to make
    //              your own QuadTree since there is no built-in quadtree in Java.

    QuadTree bearMap = new QuadTree();
    public static final double ROOT_ULLAT = 37.892195547244356, ROOT_ULLON = -122.2998046875,
            ROOT_LRLAT = 37.82280243352756, ROOT_LRLON = -122.2119140625;
    String imgRoot;

    /**
     * imgRoot is the name of the directory containing the images.
     * You may not actually need this for your class.
     */
    public Rasterer(String imgRoot) {
        this.imgRoot = imgRoot;
        LinkedList<Node> q = new LinkedList<>();
        q.add(new Node(ROOT_ULLON, ROOT_ULLAT, ROOT_LRLON, ROOT_LRLAT, "", 0));

        while (!q.isEmpty()) {
            Node temp = q.poll();
            double nWLong = temp.nWLong;
            double nWLat = temp.nWLat;
            double sELong = temp.sELong;
            double sELat = temp.sELat;
            String value = temp.value;
            int depth = temp.depth;
            double midLong = (nWLong + sELong) / 2;
            double midLat = (nWLat + sELat) / 2;
            bearMap.insert(nWLong, nWLat, sELong, sELat, value);
            if (temp.depth != 8) {
                Rectangle curRec = new Rectangle(nWLong, nWLat, sELong, sELat);
                q.add(new Node(nWLong, nWLat, midLong, midLat, value + "1", depth + 1));
                q.add(new Node(midLong, nWLat, sELong, midLat, value + "2", depth + 1));
                q.add(new Node(nWLong, midLat, midLong, sELat, value + "3", depth + 1));
                q.add(new Node(midLong, midLat, sELong, sELat, value + "4", depth + 1));
            }
        }
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     * <p>
     * The grid of images must obey the following properties, where image in the
     * grid is referred to as a "tile".
     * <ul>
     * <li>The tiles collected must cover the most longitudinal distance per pixel
     * (LonDPP) possible, while still covering less than or equal to the amount of
     * longitudinal distance per pixel in the query box for the user viewport size. </li>
     * <li>Contains all tiles that intersect the query bounding box that fulfill the
     * above condition.</li>
     * <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     * </ul>
     * </p>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     * @return A map of results for the front end as specified:
     * "render_grid"   -> String[][], the files to display
     * "raster_ul_lon" -> Number, the bounding upper left longitude of the rastered image <br>
     * "raster_ul_lat" -> Number, the bounding upper left latitude of the rastered image <br>
     * "raster_lr_lon" -> Number, the bounding lower right longitude of the rastered image <br>
     * "raster_lr_lat" -> Number, the bounding lower right latitude of the rastered image <br>
     * "depth"         -> Number, the 1-indexed quadtree depth of the nodes of the rastered image.
     * Can also be interpreted as the length of the numbers in the image
     * string. <br>
     * "query_success" -> Boolean, whether the query was able to successfully complete. Don't
     * forget to set this to true! <br>
     * @see #REQUIRED_RASTER_REQUEST_PARAMS
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        Map<String, Object> results;
        Query q = new Query(bearMap, params, imgRoot);
        results = q.solve();
        return results;
    }

}
