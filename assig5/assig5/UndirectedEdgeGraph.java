package assig5;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
  
  public String getName(int v) {
    if (v <= names.size())
      return names.get(v-1);
    else
      return "BLAH NO NAME"; // DEBUG
  }
  
  public int getNameVert(String name) {
    if (names.contains(name))
      return names.indexOf(name) + 1;
    else
      return -1;
      
  }
  
  public String toString() {
    String NEWLINE = System.getProperty("line.separator");
    StringBuilder s = new StringBuilder();
    s.append("Route                         Dist   Cost" + NEWLINE);
    for (int v = 1; v <= V; v++) {
      for (Edge e : adj[v]) {
        System.out.println("V:"+v); // DEBUG
        s.append(String.format("%-30s%4d%7.1f\n",names.get(v-1) + " -- " + names.get(e.getOtherPoint(v)-1),
            e.getDistance(),e.getCost()));
      }
      s.append(NEWLINE);
    }
    return s.toString();
  }

  public int getV() {
    return V;
  }

  public void setV(int v) {
    V = v;
  }

  public int getE() {
    return E;
  }
  
  public ArrayList<Edge> getAdj(int v) {
    return adj[v];
  }
  
  public void addName(String name) {
    names.add(name);
  }
  
  public Set<Edge> edges() {
    Set<Edge> set = new HashSet<Edge>();
    for (int i=0; i<adj.length; i++) {
      for (Edge e : adj[i])
        set.add(e);
    }
    return set;
  }
  

}
