import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /** Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc. */

    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     *
     * @param dbPath Path to the XML file to be parsed.
     */
    private final Map<String, GNode> nodes = new LinkedHashMap<>();
    private Way curWay;
    private GNode curNode;

    public void setCurNode(String id, String lon, String lat) {
        curNode = new GNode(id, lon, lat);
    }

    public void setCurWay() {
        curWay = new Way();
    }

    public void wayAddVertex(String id) {
        curWay.vertices.add(id);
    }

    public void setWayValid(boolean valid) {
        curWay.isValid = valid;
    }

    public Way getCurWay() {
        return curWay;
    }

    public GNode getCurNode() {
        return curNode;
    }

    private Map<String, String> locations = new LinkedHashMap<>();

    public Map<String, String> getLocation() {
        return locations;
    }

    public void addNode(String id, String lon, String lat) {
        nodes.put(id, new GNode(id, lon, lat));
    }

    public void addNode(String id, GNode g) {
        nodes.put(id, g);
    }

    public GNode getNode(String id) {
        return nodes.get(id);
    }

    public GraphDB(String dbPath) {
        try {
            File inputFile = new File(dbPath);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputFile, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     *
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     * Remove nodes with no connections from the graph.
     * While this does not guarantee that any two nodes in the remaining graph are connected,
     * we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        for (Iterator<Map.Entry<String, GNode>> it = nodes.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, GNode> entry = it.next();
            if (entry.getValue().adj.size() == 0) {
                it.remove();
            }
        }
    }

    /**
     * Returns an iterable of all vertex IDs in the graph.
     */
    Iterable<Long> vertices() {
        //YOUR CODE HERE, this currently returns only an empty list.
        ArrayList<Long> ans = new ArrayList<>();
        for (Map.Entry<String, GNode> entry : nodes.entrySet()) {
            String id = entry.getKey();
            Long temp = Long.parseLong(id);
            ans.add(temp);
        }
        return ans;
    }

    /**
     * Returns ids of all vertices adjacent to v.
     */
    Iterable<Long> adjacent(long v) {
        String id = Long.toString(v);
        return nodes.get(id).adj;
    }

    /**
     * Returns the Euclidean distance between vertices v and w, where Euclidean distance
     * is defined as sqrt( (lonV - lonV)^2 + (latV - latV)^2 ).
     */
    double distance(long v, long w) {
        String V = Long.toString(v);
        String W = Long.toString(w);
        GNode a = nodes.get(V);
        GNode b = nodes.get(W);
        return Math.sqrt(Math.pow(Double.parseDouble(a.lat) - Double.parseDouble(b.lat), 2)
                + Math.pow(Double.parseDouble(a.lon) - Double.parseDouble(b.lon), 2));
    }

    /**
     * Returns the vertex id closest to the given longitude and latitude.
     */
    long closest(double lon, double lat) {
        long id = 0;
        double minDist = 999999999;
        for (Map.Entry<String, GNode> entry : nodes.entrySet()) {
            GNode cur = entry.getValue();
            if (Math.pow(Double.parseDouble(cur.lon) - lon, 2)
                    + Math.pow(Double.parseDouble(cur.lat) - lat, 2) < minDist) {
                minDist = Math.pow(Double.parseDouble(cur.lon) - lon, 2)
                        + Math.pow(Double.parseDouble(cur.lat) - lat, 2);
                id = Long.parseLong(entry.getKey());
            }
        }
        return id;
    }

    /**
     * Longitude of vertex v.
     */
    double lon(long v) {
        GNode cur = nodes.get(Long.toString(v));
        return Double.parseDouble(cur.lon);
    }

    /**
     * Latitude of vertex v.
     */
    double lat(long v) {
        GNode cur = nodes.get(Long.toString(v));
        return Double.parseDouble(cur.lat);
    }
}
