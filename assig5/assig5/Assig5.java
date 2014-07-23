package assig5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Scanner;

public class Assig5 {
  private File dataFile;
  private UndirectedEdgeGraph graph;
  
  
  public static void main(String[] args) {
    new Assig5().run();
  }
  
  public void run() {
    getFile();
  }
  
  private void getFile() {
    Scanner sc = new Scanner(System.in);
    while (true) {
      System.out.print("Enter route data file: ");
      String filepath = sc.nextLine();
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
  
  private void showMenu() {
    // TODO
  }
  
  private Selection getSelection() {
    // TODO
    return null;
  }
  
  

}
