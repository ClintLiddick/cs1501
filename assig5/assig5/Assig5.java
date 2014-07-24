package assig5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Assig5 {
  private File dataFile;
  private UndirectedEdgeGraph graph;
  private Scanner userInput = new Scanner(System.in);
  private static final String NEWLINE = System.getProperty("line.separator");

  public static void main(String[] args) {
    new Assig5().run();
  }

  public void run() {
    getFile();
    runUserActions();
    cleanUp();
    System.out.println("Goodbye!");
  }

  // read open data file from user-specified path
  private void getFile() {
    while (true) {
      System.out.print("Enter route data file: ");
      String filepath = userInput.nextLine();
      this.dataFile = new File(filepath);
      try {
        loadGraph();
        System.out.println("Using data file: " + dataFile.getAbsolutePath());
        break;
      } catch(IOException ex) {
        System.out.println("Unable to read file " + filepath);
      } catch(ParseException | NumberFormatException ex) {
        System.out.println("Incompatible filetype");
        System.exit(1);
      }
    }
  }

  // parse graph from file
  private void loadGraph() throws IOException, ParseException {
    BufferedReader br = new BufferedReader(new FileReader(dataFile));
    // read # verts
    int V = Integer.parseInt(br.readLine());
    graph = new UndirectedEdgeGraph(V);
    // read verts names
    for (int i=0; i<V; i++) {
      graph.addVertice(br.readLine());
    }

    // parse and add edges
    String line;
    while ((line = br.readLine()) != null) {
      String[] tokens = line.split("\\s");
      if (tokens.length != 4) {
        br.close();
        throw new ParseException("unable to parse edge",0);
      }
      try {
        int v = Integer.parseInt(tokens[0]);
        int w = Integer.parseInt(tokens[1]);
        int dist = Integer.parseInt(tokens[2]);
        double cost = Double.parseDouble(tokens[3]);
        graph.addEdge(new Edge(v,w,dist,cost));
      } catch (NumberFormatException ex) {
        br.close();
        throw ex;
      } catch (RuntimeException ex) {
        br.close();
        throw new ParseException("edge/vertice mismatch",0);
      }
    }
    br.close();
  }

  // action switch
  private void runUserActions() {
    menuLoop:
      while (true) {
        // get user's selected action
        Selection selection = getSelection();
        switch (selection) {
        case LIST_DIRECT:
          showGraph();
          break;
        case DIST_MST:
          printDistMST();
          break;
        case SP_MILES: {
          int[] verts = getStartEndVerts();
          printShortestDistSP(verts[0],verts[1]);
          break; 
        }
        case SP_PRICE: {
          int[] verts = getStartEndVerts();
          printLowestPriceSP(verts[0],verts[1]);
          break;
        }
        case SP_STOPS: {
          int[] verts = getStartEndVerts();
          printFewestHopsSP(verts[0],verts[1]);
          break;
        }
        case ALL_TRIPS_UNDER:
          printAllTripsUnderPrice();
          break;
        case ADD_ROUTE:
          addRoute();
          break;
        case REMOVE_ROUTE: {
          int[] verts = getStartEndVerts();
          removeRoute(verts[0],verts[1]);
          break;
        }
        case QUIT:
          break menuLoop;
        }
      }
  }

  // parse user selection
  private Selection getSelection() {
    while (true) {
      showMenu();
      String input = userInput.nextLine();
      try {
        int choice = Integer.parseInt(input);
        if (choice > 9)
          throw new InvalidParameterException();

        return Selection.values()[choice - 1];
        
      } catch (NumberFormatException | InvalidParameterException ex) {
        System.out.println("Invalid selection");
      }
    }
  }

  // print console menu
  private void showMenu() {
    String menu = 
        "\n--Select an option--\n"
            + "1) List all direct flights\n"
            + "2) Show minimum spanning tree\n"
            + "3) Find shortest trip between two cities\n"
            + "4) Find cheapest trip between two cities\n"
            + "5) Find trip with fewest layovers between two cities\n"
            + "6) Find all trips under specified price\n"
            + "7) Add new route to schedule\n"
            + "8) Remove route from schedule\n"
            + "9) Quit\n"
            + ": ";
    System.out.print(menu);
  }

  // print string representation of entire graph
  private void showGraph() {
    System.out.println(graph);
  }

  // print Minimum Spanning Tree using distance as weight
  private void printDistMST() {
    IndexMinPQ<Double> pq = new IndexMinPQ<Double>(graph.getV());
    /***** arrays will be 0 indexed, so use array[vertID - 1] to access vert information *****/
    double[] distTo = new double[graph.getV()];
    boolean[] marked = new boolean[graph.getV()];
    // run Eager Prim algo for each potential starting vert
    // to determine "forest" of MSTs
    for (int v = 1; v <= graph.getV(); v++) {      // run from each vertex to find
      Edge[] edgeTo = new Edge[graph.getV()];

      for (int i = 0; i < graph.getV(); i++) {
        distTo[i] = Double.POSITIVE_INFINITY;
      }
      if (!marked[v-1]) {
        prim(v,distTo,pq,marked, edgeTo);      // minimum spanning forest
        printMST(edgeTo, distTo, v);
      }
    }
  }

  // Eager Prim algo for MST
  private void prim(int s, double[] distTo, IndexMinPQ<Double> pq, boolean[] marked, Edge[] edgeTo) {
    distTo[s-1] = 0.0;
    pq.insert(s, distTo[s-1]);
    while (!pq.isEmpty()) {
      int v = pq.delMin();
      scan(v, marked, distTo, edgeTo, pq);
    } 
  }

  // scan vertex v
  private void scan(int v, boolean[] marked, double[] distTo, Edge[] edgeTo, IndexMinPQ<Double> pq) {
    marked[v-1] = true;
    for (Edge e : graph.getAdj(v)) {
      int w = e.getOtherPoint(v);
      if (marked[w-1]) 
      {
        continue;         // v-w is obsolete edge
      }
      if (e.getDistance() < distTo[w-1]) {
        distTo[w-1] = e.getDistance();
        edgeTo[w-1] = e;
        if (pq.contains(w)) 
        {
          pq.change(w, distTo[w-1]);
        }
        else              
        {
          pq.insert(w, distTo[w-1]);
        }
      }
    }
  }

  // pretty-print MST
  private void printMST(Edge[] edgeTo, double[] distTo, int v) {
    StringBuilder sb = new StringBuilder();
    
    sb.append("Minimum Spanning Tree Starting from: " + graph.getName(v) + "\n");
    for (int i=2; i<=graph.getV(); i++) {
      if (edgeTo[i-1] != null) {
        String end = graph.getName(i);
        String start = graph.getName(edgeTo[i-1].getOtherPoint(i));
        double dist = distTo[i-1]; // also == e.getDistance();
        assert (edgeTo[i-1].getDistance() == dist);
        sb.append(start + "--" + end + " : " + dist + "\n");
      }
    }
    System.out.println(sb.toString());
  }

  // ask user for starting and ending cities, return as int vertIDs tuple
  private int[] getStartEndVerts() {
    int start;
    int end;
    while(true) {
      System.out.println("Enter starting city: ");
      String startName = userInput.nextLine();
      System.out.println("Enter ending city: ");
      String endName = userInput.nextLine();

      start = graph.getNameVert(startName);
      end = graph.getNameVert(endName);
      
      if (start == -1) { // start not found
        System.out.println(startName + " not found");
        continue;
      }

      if (end == -1) { // end not found
        System.out.println(endName + " not found");
        continue;
      }
      break; // valid input
    }
    return new int[] {start, end};
  }

  // print Shortest Path using distance as weight
  private void printShortestDistSP(int start, int end) {
    Edge[] edgeTo = new Edge[graph.getV()];
    double[] distTo = new double[graph.getV()];
    // Dijkstras's algo for weighted shortest path
    shortestPath(start, edgeTo, distTo, new EdgeWeight() { // pseudo-functional programming, 
                                                           // passing function wrapped in anonymous class
      @Override
      public double weight(Edge e) {
        return e.getDistance();
      }
    });

    if (edgeTo[end-1] == null) { // no path found
      System.out.println("No route exists between " + graph.getName(start) + " and " + graph.getName(end));
    } else { // path found
      StringBuilder sb = new StringBuilder();
      int dist = (int) distTo[end-1];
      int currVert = end;
      // build pretty string from results of algo
      while (currVert != start) {
        sb.append(graph.getName(currVert) + " " + edgeTo[currVert-1].getDistance() + " ");
        currVert = edgeTo[currVert-1].getOtherPoint(currVert);
      }
      sb.append(graph.getName(start));
      sb.insert(0,"Shortest Distance Route from " + graph.getName(start) + " to " 
          + graph.getName(end) + " is " + dist + "\nRoute (in reverse order):\n");
      System.out.println(sb.toString());
    }
  }

  // print shortest path using price as weight
  private void printLowestPriceSP(int start, int end) {
    Edge[] edgeTo = new Edge[graph.getV()];
    double[] distTo = new double[graph.getV()];
    shortestPath(start, edgeTo, distTo, new EdgeWeight() { // pseudo-functional programming
                                                           // passing function wrapped in anonymous class
      @Override
      public double weight(Edge e) {
        return e.getCost();
      }
    });

    if (edgeTo[end-1] == null) { // no path found
      System.out.println("No route exists between " + graph.getName(start) + " and " + graph.getName(end));
    } else { // path found
      StringBuilder sb = new StringBuilder();
      double cost = distTo[end-1];
      int currVert = end;
      // build pretty string from results
      while (currVert != start) {
        sb.append(graph.getName(currVert) + " " + edgeTo[currVert-1].getCost() + " ");
        currVert = edgeTo[currVert-1].getOtherPoint(currVert);
      }
      sb.append(graph.getName(start));
      sb.insert(0,"Lowest Cost Route from " + graph.getName(start) + " to " 
          + graph.getName(end) + " is " + cost + "\nRoute (in reverse order):\n");
      System.out.println(sb.toString());
    }
  }

  // print shortest path using unweighted (equal-weighted) edges
  private void printFewestHopsSP(int start, int end) {
    Edge[] edgeTo = new Edge[graph.getV()];
    double[] distTo = new double[graph.getV()];
    shortestPath(start, edgeTo, distTo, new EdgeWeight() { // pseudo-functional programming
                                                           // passing function wrapped in anonymous class
      @Override
      public double weight(Edge e) {
        return 1; // shortest hops = unweighted shortest path
      }
    });
    
    if (edgeTo[end-1] == null) { // no path found
      System.out.println("No route exists between " + graph.getName(start) + " and " + graph.getName(end));
    } else { // path found
      StringBuilder sb = new StringBuilder();
      int dist = (int) distTo[end-1];
      int currVert = end;
      // build pretty string from results
      while (currVert != start) {
        sb.append(graph.getName(currVert) + " ");
        currVert = edgeTo[currVert-1].getOtherPoint(currVert);
      }
      sb.append(graph.getName(start));
      sb.insert(0,"Fewest Stops from " + graph.getName(start) + " to " 
          + graph.getName(end) + " is " + dist + "\nRoute (in reverse order):\n");
      System.out.println(sb.toString());
    }
  }

  // Dijkstra's algorithm for weighted shortest path
  private void shortestPath(int s, Edge[] edgeTo, double[] distTo, EdgeWeight ew) {
    IndexMinPQ<Double> pq = new IndexMinPQ<Double>(graph.getV());
    for (int v = 0; v < graph.getV(); v++)
      distTo[v] = Double.POSITIVE_INFINITY;

    distTo[s-1] = 0.0;

    // relax vertices in order of distance from s
    pq = new IndexMinPQ<Double>(graph.getV());
    pq.insert(s, distTo[s-1]);
    while (!pq.isEmpty()) {
      int v = pq.delMin();
      for (Edge e : graph.getAdj(v))
        relax(e, v, distTo, edgeTo, pq, ew);
    }
    // check optimality conditions
    assert check(s,distTo,edgeTo,ew);
  }

  // relax edge e and update pq if changed
  private void relax(Edge e, int v, double[] distTo, Edge[] edgeTo, IndexMinPQ<Double> pq, EdgeWeight ew) {
    //    int v = e.getPoint();
    int w = e.getOtherPoint(v);
    if (distTo[w-1] > distTo[v-1] + ew.weight(e)) {
      distTo[w-1] = distTo[v-1] + ew.weight(e);
      edgeTo[w-1] = e;
      if (pq.contains(w)) 
        pq.change(w, distTo[w-1]);
      else                
        pq.insert(w, distTo[w-1]);
    }
  }

  //check optimality conditions:
  // (i) for all edges e:            distTo[e.to()] <= distTo[e.from()] + e.weight()
  // (ii) for all edge e on the SPT: distTo[e.to()] == distTo[e.from()] + e.weight()
  private boolean check(int s, double[] distTo, Edge[] edgeTo, EdgeWeight ew) {

    // check that edge weights are nonnegative
    for (Edge e : graph.edges()) {
      if (ew.weight(e) < 0) {
        System.err.println("negative edge weight detected");
        return false;
      }
    }

    // check that distTo[v] and edgeTo[v] are consistent
    if (distTo[s-1] != 0.0 || edgeTo[s-1] != null) {
      System.err.println("distTo[s] and edgeTo[s] inconsistent");
      return false;
    }
    for (int v = 0; v < graph.getV(); v++) {
      if (v == s-1) continue;
      if (edgeTo[v] == null && distTo[v] != Double.POSITIVE_INFINITY) {
        System.err.println("distTo[] and edgeTo[] inconsistent");
        return false;
      }
    }

    // check that all edges e = v->w satisfy distTo[w] <= distTo[v] + e.weight()
    for (int v = 1; v <= graph.getV(); v++) {
      for (Edge e : graph.getAdj(v)) {
        int w = e.getOtherPoint(v);
        if (distTo[v-1] + ew.weight(e) < distTo[w-1]) {
          System.err.println("edge " + e + " not relaxed");
          return false;
        }
      }
    }

    // check that all edges e = v->w on SPT satisfy distTo[w] == distTo[v] + e.weight()
    for (int w = 1; w <= graph.getV(); w++) {
      if (edgeTo[w-1] == null) continue;
      Edge e = edgeTo[w-1];
      int v = e.getPoint();
      if (w != e.getOtherPoint(v)) return false;
      if (distTo[v-1] + ew.weight(e) != distTo[w-1]) {
        System.err.println("edge " + e + " on shortest path not tight");
        return false;
      }
    }
    return true;
  }

  // user-specified route (allows new cities)
  private void addRoute() {
    System.out.println("Starting city: ");
    String startCity = userInput.nextLine();
    System.out.println("Ending city: ");
    String endCity = userInput.nextLine();
    int dist = 0;
    while (true) {
      try {
        System.out.print("Distance (miles): ");
        dist = Integer.parseInt(userInput.nextLine());
        break;
      } catch (NumberFormatException ex) {
        System.out.println("Invalid distance");
      }
    }
    double cost = 0;
    while (true) {
      try {
        System.out.print("Cost (100.00): ");
        cost = Double.parseDouble(userInput.nextLine());
        break;
      } catch(NumberFormatException ex) {
        System.out.println("Invalid cost");
      }
    }

    BufferedReader br = null;
    FileWriter fr = null;
    try {
      br = new BufferedReader(new FileReader(dataFile));
      List<String> lines = new LinkedList<String>();
      String line;
      // buffer entire file
      while ((line = br.readLine()) != null) {
        lines.add(line);
      }
      br.close();

      int verts = Integer.parseInt(lines.get(0));
      int start = graph.getNameVert(startCity);
      int end = graph.getNameVert(endCity);

      // check if adding new cities
      if (start == -1) {
        verts++;
        start = verts;
        graph.addName(startCity);
        lines.add(verts, startCity);
      }

      if (end == -1) {
        verts++;
        end = verts;
        graph.addName(endCity);
        lines.add(verts, endCity);
      }

      // update number of verts
      lines.set(0, String.valueOf(verts));
      // add new edge line
      lines.add(start + " " + end + " " + dist + " " + cost);

      // write all lines back to file
      fr = new FileWriter(dataFile);
      Iterator<String> lineItr = lines.iterator();
      fr.write(lineItr.next());
      while (lineItr.hasNext()) {
        fr.write(NEWLINE + lineItr.next());
      }
      fr.close();
      try {
        loadGraph();
        System.out.println("Schedule updated");
      } catch (ParseException e) {
        System.out.println("datafile corrupted. sorry...");
      }

    } catch (FileNotFoundException ex) {
      System.out.println("Error opening file");
    } catch (IOException ex) {
      System.out.println("Error manipulating file");
      try {
        br.close();
        fr.close();
      } catch (IOException | NullPointerException ee) { }
    }

  }

  // finds all trips under specified price using
  // a branch and prune combination method
  private void printAllTripsUnderPrice() {
    double priceLimit;
    // get user input
    while (true) {
      System.out.print("Enter max price: ");
      try {
        priceLimit = Double.parseDouble(userInput.nextLine());
        break;
      } catch(NumberFormatException ex) {
        System.out.println("Invalid cost");
      }
    }
    StringBuilder sb = new StringBuilder();
    sb.append("All Routes " + priceLimit + " Or Less\n");

    // loop for each starting vertice
    for (int vert=1; vert<=graph.getV(); vert++) {
      findRoutesUnderLimit(vert,priceLimit,sb);
    }
    // print generated routes
    System.out.println(sb.toString());
  }

  // setup for recursive calls
  private void findRoutesUnderLimit(int vert, double priceLimit, StringBuilder sb) {
    boolean[] alreadyUsed = new boolean[graph.getV() + 1];
    alreadyUsed[vert] = true;
    Edge[] edgeTo = new Edge[graph.getV() + 1];
    // begin recursion
    routesUnderLimitRec(vert, edgeTo, 0, priceLimit, sb, alreadyUsed);
    alreadyUsed[vert] = false;
  }

  // recursive pruning method for finding combinations of edges
  private void routesUnderLimitRec(int vert,Edge[] edgeTo, double price, 
      double priceLimit, StringBuilder sb, boolean[] alreadyUsed) {
    // get list of edges
    ArrayList<Edge> adj = graph.getAdj(vert);
    // for each edge, add to potential route if under price and recurse
    for (Edge e : adj) {
      int w = e.getOtherPoint(vert); // get opposite side of edge
      if (!alreadyUsed[w] && price + e.getCost() <= priceLimit) { // if valid
        // add edge
        edgeTo[w] = e;
        alreadyUsed[w] = true;
        // add to list of valid routes (StringBuilder)
        appendRouteString(sb,w,edgeTo,price+e.getCost());
        // recurse
        routesUnderLimitRec(w, edgeTo, price+e.getCost(), priceLimit, sb, alreadyUsed);
        // remove edge
        edgeTo[w] = null;
        alreadyUsed[w] = false;
      }
    }
  }

  // formats and appends a new route to StringBuilder in reverse order
  private void appendRouteString(StringBuilder sb, int end, Edge[] edgeTo, double price) {
    sb.append("Cost: " + price + " Route: ");
    int vert = end;
    while (edgeTo[vert] != null) {
      String name = graph.getName(vert);
      double cost = edgeTo[vert].getCost();
      sb.append(name + " " + cost + " ");
      vert = edgeTo[vert].getOtherPoint(vert);
    }
    String firstName = graph.getName(vert);
    sb.append(firstName + NEWLINE + NEWLINE);
  }

  // removes edge specified by vertices
  private void removeRoute(int start, int end) {

    BufferedReader br = null;
    FileWriter fr = null;
    try {
      br = new BufferedReader(new FileReader(dataFile));
      List<String> lines = new LinkedList<String>();
      String line;
      // buffer entire file
      while ((line = br.readLine()) != null) {
        lines.add(line);
      }
      br.close();

      fr = new FileWriter(dataFile);
      Iterator<String> lineItr = lines.iterator();
      // go past the city list, just write back to file
      int verts = Integer.parseInt(lines.get(0));
      fr.write(lineItr.next());
      for (int i=0; i<verts; i++)
        fr.write(NEWLINE + lineItr.next());

      while (lineItr.hasNext()) {
        line = lineItr.next();
        String[] tokens = line.split("\\s");
        int first = Integer.parseInt(tokens[0]);
        int second = Integer.parseInt(tokens[1]);
        // write remaining lines back to file
        // if not the edge we want to remove
        if ((start == first && end == second) || (start == second && end == first))
          continue; // don't write line
        else
          fr.write(NEWLINE + line);
      }
      fr.close();
      try {
        // reload graph in memory
        loadGraph();
        System.out.println("Schedule updated");
      } catch (ParseException e) {
        System.out.println("datafile corrupted. sorry...");
      }

    } catch (FileNotFoundException ex) {
      System.out.println("Error opening file");
    } catch (IOException ex) {
      System.out.println("Error manipulating file");
      try {
        br.close();
        fr.close();
      } catch (IOException | NullPointerException ee) { }
    }

  }

  private void cleanUp() {
    userInput.close();
  }
}

// used to pas to Dijkstra's algo to determine weight of edge
interface EdgeWeight {
  abstract public double weight(Edge e);
}
