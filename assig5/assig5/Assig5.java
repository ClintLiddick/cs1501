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
  
  private void cleanUp() {
    userInput.close();
  }
  
  

}
