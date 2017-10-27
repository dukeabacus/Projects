import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.TreeSet;
/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {
    /**
     * Return a LinkedList of <code>Long</code>s representing the shortest path from st to dest,
     * where the longs are node IDs.
     */
    public static LinkedList<Long> shortestPath(GraphDB g,
                                                double stlon,
                                                double stlat,
                                                double destlon, double destlat) {
        final double inf = 999999999;
        Set<String> closedSet = new HashSet<>();
        Set<String> openSet = new TreeSet<>();
        Map<String, String> cameFrom = new HashMap<>();
        Map<String, Double> gScore = new HashMap<>();
        Map<String, Double> fScore = new HashMap<>();
        long stID = g.closest(stlon, stlat);
        long destID = g.closest(destlon, destlat);

        gScore.put(Long.toString(stID), 0.0);
        fScore.put(Long.toString(stID), g.distance(stID, destID));
        openSet.add(Long.toString(stID));

        while (!openSet.isEmpty()) {

            double minDist = inf;
            String curID = "";
            for (String s : openSet) {
                if (fScore.get(s) < minDist) {
                    minDist = fScore.get(s);
                    curID = s;
                }
            }
            openSet.remove(curID);

            if (curID.equals(Long.toString(destID))) {
                return reconstructPath(cameFrom, curID);
            }
            closedSet.add(curID);

            double curGScore;
            if (gScore.containsKey(curID)) {
                curGScore = gScore.get(curID);
            } else {
                curGScore = inf;
            }

            for (Long id : g.adjacent(Long.parseLong(curID))) {
                String adjID = Long.toString(id);
                if (closedSet.contains(adjID)) {
                    continue;
                }
                double tentativegScore = curGScore + g.distance(Long.parseLong(curID), 
                        Long.parseLong(adjID));


                if (!openSet.contains(adjID)) {
                    openSet.add(adjID);
                } else if (tentativegScore >= gScore.get(adjID)) {
                    continue;
                }

                cameFrom.put(adjID, curID);
                gScore.put(adjID, tentativegScore);
                fScore.put(adjID, gScore.get(adjID) + g.distance(Long.parseLong(adjID), destID));

            }

        }
        return null;
    }

    public static LinkedList<Long> reconstructPath(Map<String, String> cameFrom, String curID) {
        LinkedList<Long> result = new LinkedList<>();
        result.add(Long.parseLong(curID));
        while (cameFrom.containsKey(curID)) {
            curID = cameFrom.get(curID);
            result.addFirst(Long.parseLong(curID));
        }
        return result;
    }

}
