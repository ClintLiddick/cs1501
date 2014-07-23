package assig5;

import java.util.Comparator;

public class Edge {
  private final int v;
  private final int w;
  private final int distance;
  private final double cost;
  
  Edge(int start, int end, int dist, double cost) {
    this.v = start;
    this.w = end;
    this.distance = dist;
    this.cost = cost;
  }

  public int getPoint() {
    return v;
  }

  public int getOtherPoint(int pt) {
    if (pt == v)
      return w;
    else if (pt == w)
      return v;
    else
      throw new RuntimeException("illegal endpoint");
  }

  public int getDistance() {
    return distance;
  }

  public double getCost() {
    return cost;
  }
  
  public String toString() {
    return String.format("%d-%d %d %.2f",v,w,distance,cost);
  }
}


class EdgeDistanceComparator implements Comparator<Edge> {
  @Override
  public int compare(Edge o1, Edge o2) {
    return Double.compare(o1.getDistance(), o2.getDistance());
  }
}

class EdgeCostComparator implements Comparator<Edge> {
  @Override
  public int compare(Edge o1, Edge o2) {
    // TODO Auto-generated method stub
    return Double.compare(o1.getCost(), o2.getCost());
  }
}
