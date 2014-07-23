package assig5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.Scanner;

public class Assig5 {
  private File dataFile;
  private UndirectedEdgeGraph graph;
  private Scanner userInput = new Scanner(System.in);


  public static void main(String[] args) {
    new Assig5().run();
  }

  public void run() {
    getFile();
    runUserActions();

    cleanUp();
  }

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

  private void loadGraph() throws IOException, ParseException {
    BufferedReader br = new BufferedReader(new FileReader(dataFile));
    int V = Integer.parseInt(br.readLine());
    graph = new UndirectedEdgeGraph(V);
    for (int i=0; i<V; i++) {
      graph.addVertice(br.readLine());
    }

    String line;
    while ((line = br.readLine()) != null) {
      String[] tokens = line.split("\\s");
      if (tokens.length != 4) {
        br.close();
        throw new ParseException("unable to parse edge",0);
      }
      int v = Integer.parseInt(tokens[0]);
      int w = Integer.parseInt(tokens[1]);
      int dist = Integer.parseInt(tokens[2]);
      double cost = Double.parseDouble(tokens[3]);
      try {
        graph.addEdge(new Edge(v,w,dist,cost));
      } catch (RuntimeException ex) {
        br.close();
        throw new ParseException("edge/vertice mismatch",0);
      }
    }
    br.close();
  }

  private void runUserActions() {
    menuLoop:
      while (true) {
        Selection selection = getSelection();
        switch (selection) {
        case LIST_DIRECT:
          showGraph();
          break;
        case DIST_MST:
          printDistMST();
          break;
        case SP_MILES:
        case SP_PRICE:
        case SP_STOPS:
        case ALL_TRIPS:
        case ADD_ROUTE:
        case REMOVE_ROUTE:
        case QUIT:
          break menuLoop;
        }
      }
  }

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

  private void showMenu() {
    String menu = 
        "--Select an option--\n"
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

  private void showGraph() {
    System.out.println(graph);
  }

  private void printDistMST() {
    IndexMinPQ<Double> pq = new IndexMinPQ<Double>(graph.getV(), new EdgeDistanceComparator());
    // arrays will be 0 indexed, so array[vertID - 1] to access
    double[] distTo = new double[graph.getV()];
    boolean[] marked = new boolean[graph.getV()];
    Edge[] edgeTo = new Edge[graph.getV()];
    
    for (int v = 0; v < graph.getV(); v++) {
      distTo[v] = Double.POSITIVE_INFINITY;
    }
    
    for (int v = 1; v <= graph.getV(); v++) {      // run from each vertex to find
      if (!marked[v-1]) {
        prim(v,distTo,pq,marked, edgeTo);      // minimum spanning forest
      }
    }
    printMST(edgeTo, distTo);
  }
  
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
  
  private void printMST(Edge[] edgeTo, double[] distTo) {
    StringBuilder sb = new StringBuilder();
    sb.append("Minimum Spanning Tree\n");
    sb.append("Starting from: " + graph.getName(1) + "\n");
    for (int i=2; i<=graph.getV(); i++) {
      String end = graph.getName(i);
      String start = graph.getName(edgeTo[i-1].getOtherPoint(i));
      double dist = distTo[i-1]; // also == e.getDistance();
      assert (edgeTo[i-1].getDistance() == dist);
      sb.append(start + "--" + end + " : " + dist + "\n");
    }
    System.out.println(sb.toString());
  }

  private void cleanUp() {
    userInput.close();
  }



}
