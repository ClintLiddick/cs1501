package assig5;

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
  
  @Override
  public int hashCode() {
    return Integer.parseInt(""+v+w+distance);
  }
}
