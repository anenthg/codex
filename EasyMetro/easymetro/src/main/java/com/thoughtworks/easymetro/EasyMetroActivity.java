package com.thoughtworks.easymetro;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import helper.Helper;
import helper.Lanes;
import helper.Search;
import metromap.Edge;
import metromap.Graph;
import metromap.Vertex;
import path.Path;
import stratergyengine.DijkstraAlgorithm;

public class EasyMetroActivity extends ActionBarActivity {

    TextView way;
    private static List<Vertex> nodes;
    private static List<Edge> edges;
    private static Map<String, ArrayList<Integer>> station_lane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_metro);


        way = (TextView) findViewById(R.id.textView);
        //ASSIGN THE ARRAY VALUES FOR AUTOCOMPLETE
        final AutoCompleteTextView source = (AutoCompleteTextView) findViewById(R.id.editText);
        final AutoCompleteTextView destination = (AutoCompleteTextView) findViewById(R.id.editText2);
        final String[] stations = getResources().getStringArray(R.array.stations);

        if (getApplicationContext() == null) {
            System.out.println("WTF");
        }
        ArrayAdapter<String> metroAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, stations);
        source.setThreshold(1);
        destination.setThreshold(1);
        source.setAdapter(metroAdapter);
        destination.setAdapter(metroAdapter);


        Button go = (Button) findViewById(R.id.button);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //PERFORM VALIDATIONS

                //Toast.makeText(EasyMetroActivity.this, source.getText(), Toast.LENGTH_LONG).show();

                if ((source.getText().toString().equals("")) && (destination.getText().toString().equals(""))) {
                    //WTF
                    Toast.makeText(EasyMetroActivity.this, "Please enter source and destination", Toast.LENGTH_LONG).show();
                    way.setText("");
                } else if (source.getText().toString().equals("")) {
                    //WTF
                    Toast.makeText(EasyMetroActivity.this, "Please enter a source", Toast.LENGTH_LONG).show();
                    way.setText("");
                } else if (destination.getText().toString().equals("")) {
                    //WTF
                    Toast.makeText(EasyMetroActivity.this, "Please enter a destination", Toast.LENGTH_LONG).show();
                    way.setText("");
                } else if (!Helper.isValidStation(source.getText().toString(), stations) && !Helper.isValidStation(destination.getText().toString(), stations)) {
                    Toast.makeText(EasyMetroActivity.this, "Not a valid source and destination", Toast.LENGTH_LONG).show();
                    way.setText("");
                } else if (!Helper.isValidStation(source.getText().toString(), stations)) {
                    Toast.makeText(EasyMetroActivity.this, "Not a valid source", Toast.LENGTH_LONG).show();
                    way.setText("");
                } else if (!Helper.isValidStation(destination.getText().toString(), stations)) {
                    Toast.makeText(EasyMetroActivity.this, "Not a valid destination", Toast.LENGTH_LONG).show();
                    way.setText("");
                } else if (source.getText().toString().equals(destination.getText().toString())) {
                    Toast.makeText(EasyMetroActivity.this, "You are at the destination!", Toast.LENGTH_LONG).show();
                    way.setText("");
                } else {
                    //SEARCH THE SHORTEST PATH
                    FindShortestWay task = new FindShortestWay();
                    task.execute(source.getText().toString(), destination.getText().toString());


                }

            }


        });
    }

    /*public Path doSearch(String source, String destination) {
        nodes = new ArrayList<Vertex>();
        edges = new ArrayList<Edge>();
        station_lane = new HashMap<String, ArrayList<Integer>>();
        try {
            final InputStream in = getResources().getAssets().open("stations");
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
                        Search.addLane(trim(previousVertex.getName() + location.getName()), previousVertex, location, 1);
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
    }*/

    private class FindShortestWay extends AsyncTask<String, Void, Path> {
        @Override

        protected Path doInBackground(String... stations) {

            Path pt = Search.doSearch(EasyMetroActivity.this,stations[0], stations[1]);

            return pt;

        }

        @Override
        protected void onPostExecute(Path result) {
            //Calculate the cost

            int cost = (result.getPath().length - 1) + result.getLanesChanged().getLanesChanged();
            StringBuilder sb = new StringBuilder("");
            String a = "Time it would take = " + result.getTime() + " minutes";
            String b = "\nCost = " + cost + " $";
            sb.append(a);
            sb.append(b);

            int i = 0;
            String prevStation = new String();
            for (String temp : result.getPath()) {


                String istring = Integer.toString(i);
                if (i == 0) {
                    prevStation = temp;
                    sb.append("\nTake " + Search.findLineColor(temp) + " line at " + temp);
                } else if (i == result.getPath().length - 1 && result.getLanesChanged().getPointOfLaneChange().contains(istring)) {
                    sb.append("(change line to " + Search.findLineColor(temp) + ") ->" + " to reach " + temp);
                } else if (i == result.getPath().length - 1) {
                    sb.append(" to reach " + temp);
                } else if (result.getLanesChanged().getPointOfLaneChange().contains(istring)) {
                    sb.append("(change line to " + Search.findLineColor(temp) + ") ->" + temp);
                } else {
                    sb.append(" -> " + temp);
                }
                i++;
                prevStation = temp;
            }
            way.setVisibility(View.VISIBLE);
            way.setText(sb.toString());
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(way.getWindowToken(), 0);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.easy_metro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
    /*private static Lanes findOutLaneChanges(String[] path) {

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
*/
    /*private static boolean isLaneChange(ArrayList<Integer> current,
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

    private static void addLane(String laneId, Vertex source, Vertex destination,
                                int cost) {
        Edge lane = new Edge(laneId, source, destination, cost);
        edges.add(lane);
    }

    private String findLineColor(String station) {
        try {
            final InputStream in = getResources().getAssets().open("lines");
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
            Toast.makeText(EasyMetroActivity.this, "Sorry, something went wrong. Try again", Toast.LENGTH_LONG).show();
            return station;

        }
    }

    static private String trim(String string) {
        return string.replaceAll("\\s+", "");
    }
    *//**
     * A placeholder fragment containing a simple view.
     *//*

}
*/