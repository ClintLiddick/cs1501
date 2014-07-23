package assig5;

import java.util.ArrayList;

public class UndirectedEdgeGraph {
  private int V;
  private int E;
  private ArrayList<Edge>[] adj;
  private ArrayList<String> names;
  
  public UndirectedEdgeGraph(int vertices) {
    assert (vertices >= 0);
    
    this.V = vertices;
    this.E = 0;
    adj = (ArrayList<Edge>[]) new ArrayList[this.V+1];
    for (int i=0; i<this.V+1; i++) {
      adj[i] = new ArrayList<Edge>();
    }
    names = new ArrayList<String>();
  }

  public void addVertice(String name) {
    names.add(name);    
  }
  
  public void addEdge(Edge e) {
    int v = e.getPoint();
    int w = e.getOtherPoint(v);
    
    if (v > adj.length || w > adj.length)
      throw new RuntimeException("unknown vertice");
    
    adj[v].add(e);
    adj[w].add(e);
    E++;
  }
  
  public String toString() {
    String NEWLINE = System.getProperty("line.separator");
    StringBuilder s = new StringBuilder();
    s.append(V + " " + E + NEWLINE);
    for (int v = 0; v < V; v++) {
        s.append(v + ": ");
        for (Edge e : adj[v]) {
            s.append(e + "  ");
        }
        s.append(NEWLINE);
    }
    return s.toString();
}

}
