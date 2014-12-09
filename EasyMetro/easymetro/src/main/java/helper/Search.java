package helper;

import com.thoughtworks.easymetro.EasyMetroActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import metromap.Edge;
import metromap.Graph;
import metromap.Vertex;
import path.Path;
import stratergyengine.DijkstraAlgorithm;

/**
 * Created by ashwin on 15/7/14.
 */
public class Search {
    private static List<Vertex> nodes;
    private  static List<Edge> edges;
    private  static Map<String, ArrayList<Integer>> station_lane;
    private static EasyMetroActivity easyActivity;
    public static Path doSearch(EasyMetroActivity easyActivity1,String source,String destination)
    {
        easyActivity=easyActivity1;
        nodes = new ArrayList<Vertex>();
        edges = new ArrayList<Edge>();
        station_lane = new HashMap<String, ArrayList<Integer>>();
        try {
            final InputStream in = easyActivity.getResources().getAssets().open("stations");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = new String();
            int laneCount = 0;
            while ((line = reader.readLine()) != null) {
                Vertex previousVertex = null;
                ++laneCount;
                for (String station : line.split(",")) {
                    station = station.replaceAll("^\"|\"$", "");
                    //HASHMAP TO STORE THE LANE OF A STATION
                    ArrayList<Integer> values = station_lane.get(station);
                    if (values == null) {
                        values = new ArrayList<Integer>();
                    }
                    values.add(laneCount);
                    station_lane.put(station, values);
                    Vertex location = new Vertex(station, station);
                    nodes.add(location);
                    if (previousVertex == null) {

                        previousVertex = location;
                    } else {
                        addLane(trim(previousVertex.getName() + location.getName()), previousVertex, location, 1);
                        previousVertex = location;
                    }
                }
            }

            reader.close();
            //MODIFY FEW EDGES TO INCLUDE LANES
            //modifyEdges();
            //MODIFIED

        } catch (Exception e) {
            System.out.println(e);
        }

        //CALCULATE SHORTEST PATH
        Graph graph = new Graph(nodes, edges);
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
        dijkstra.execute(new Vertex(source, source));
        LinkedList<Vertex> path = dijkstra.getPath(new Vertex(destination, destination));

        //assertNotNull(path);
        //assertTrue(path.size() > 0);

        //COST


        String[] st = new String[path.size()];
        int i = 0;
        for (Vertex vertex : path) {
            System.out.println(vertex);
            st[i] = vertex.toString();
            i++;
        }

        //Find out the number of lane changes and the point of lane change
        Lanes lanesChanged = findOutLaneChanges(st);
        Path pt = new Path(st, (st.length - 1) * 5, lanesChanged);
        return pt;
    }
    private static void addLane(String laneId, Vertex source, Vertex destination,
                                int cost) {
        Edge lane = new Edge(laneId, source, destination, cost);
        edges.add(lane);
    }
    static private String trim(String string) {
        return string.replaceAll("\\s+", "");
    }
    private static Lanes findOutLaneChanges(String[] path) {

        if (path.length > 2) {
            String pointsOfLaneChange = new String();
            int i = 0;
            int change = 0;
            ArrayList<Integer> previous = new ArrayList<Integer>();
            ArrayList<Integer> prePrevious = new ArrayList<Integer>();
            for (String station : path) {
                if (previous.size() == 0) {

                    previous = station_lane.get(station);
                } else if (prePrevious.size() == 0) {

                    prePrevious = previous;
                    previous = station_lane.get(station);
                } else if (isLaneChange(previous = station_lane.get(station), prePrevious)) {
                    pointsOfLaneChange += Integer.toString(i);
                    prePrevious = previous;
                    previous = station_lane.get(station);
                    change++;
                }

                i++;
            }

            return new Lanes(change, pointsOfLaneChange);
        } else {
            String pointsOfLaneChane = new String();
            return new Lanes(0, pointsOfLaneChane);
        }
    }
    private static boolean isLaneChange(ArrayList<Integer> current,
                                        ArrayList<Integer> prePrevious) {
        // TODO Auto-generated method stub
        boolean laneChange = true;
        for (int a : current) {
            for (int b : prePrevious) {
                if (a == b) {
                    laneChange = false;
                    break;
                }
            }
        }
        return laneChange;
    }
    public static String findLineColor(String station) {
        try {
            final InputStream in = easyActivity.getResources().getAssets().open("lines");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = new String();
            int laneCount = 0;
            String color = new String("");
            while ((line = reader.readLine()) != null) {
                if (line.contains(station)) {
                    String[] temp = line.split("-");
                    color = temp[0];
                }
            }
            return color;
        } catch (IOException e) {
            //Toast.makeText(EasyMetroActivity.this, "Sorry, something went wrong. Try again", Toast.LENGTH_LONG).show();
            return station;

        }
    }
}
