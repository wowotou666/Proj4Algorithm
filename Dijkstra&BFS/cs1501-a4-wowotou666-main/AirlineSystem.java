import java.util.Set;
import java.util.ArrayList;
import java.util.*;
import java.io.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

final public class AirlineSystem implements AirlineInterface {
  private List<String> cityNames = null;
  private Digraph G = null;
  private static Scanner scan = null;
  private static int INFINITY = Integer.MAX_VALUE;


  public boolean loadRoutes(String fileName) {
    Scanner fileScan = null;
    try {
      fileScan = new Scanner(new FileInputStream(fileName));
    } catch (FileNotFoundException e) {
      return false;
    }
    int v = fileScan.nextInt();
    G = new Digraph(v);
    cityNames = new ArrayList<String>(v);
    String st = fileScan.nextLine();
    for(int i = 0; i < v; i++)
    {
      cityNames.add(fileScan.nextLine());
    }
    while(fileScan.hasNext()) {
      int from = fileScan.nextInt();
      int to = fileScan.nextInt();
      int distance = fileScan.nextInt();
      double cost = fileScan.nextDouble();
      G.addEdge(new WeightedDirectedEdge(from - 1, to - 1, distance, cost));
      G.addEdge(new WeightedDirectedEdge(to - 1, from - 1, distance, cost));
    }
    fileScan.close();
    return true;
  }


  public Set<String> retrieveCityNames() {
    return new HashSet<>(cityNames);
  }


  public Set<Route> retrieveDirectRoutesFrom(String city) throws CityNotFoundException {
    HashSet<Route> routes = new HashSet<>();
    for(int i = 0; i < G.v; i++) {
      for(WeightedDirectedEdge e : G.adj(i)) {
        if(Objects.equals(cityNames.get(e.from()), city)){
          routes.add(new Route(city,cityNames.get(e.to()),e.distance,e.cost));
        }
      }
    }
    return routes;
  }


  public Set<ArrayList<String>> fewestStopsItinerary(String source, String destination) throws CityNotFoundException {
    HashSet<ArrayList<String>> hopList = new HashSet<ArrayList<String>>();
    ArrayList<String> hop = new ArrayList<>();
    if (G == null) {
      System.out.println("please import a file first");
    } else{
      int from = cityNames.indexOf(source);
      int to = cityNames.indexOf(destination);

      G.bfs(from);

      if(G.marked[to]){
        Stack<Integer> path = new Stack<>();
        for (int x = to; x != from; x = G.edgeTo[x]){
          path.push(x);
        }
        path.push(from);
        while(!path.empty()){
          hop.add(cityNames.get(path.pop()));
        }
      }
      hopList.add(hop);
    }
    return hopList;
  }


  public Set<ArrayList<Route>> shortestDistanceItinerary(String source, String destination) throws CityNotFoundException {
    HashSet<ArrayList<Route>> routeList = new HashSet<>();
    ArrayList<Route> route = new ArrayList<>();
    if (G == null) {
      System.out.println("please import a file first");
    } else {
      int from = cityNames.indexOf(source);
      int to = cityNames.indexOf(destination);
      G.dijkstras(from, to, false);
      if (G.marked[to]) {
        Stack<Integer> path = new Stack<>();
        for (int x = to; x != from; x = G.edgeTo[x]) {
          path.push(x);
        }
        int prevVert = from;
        while (!path.empty()) {
          int vert = path.pop();
          for (WeightedDirectedEdge directedEdge : G.adj(prevVert)){
            if(directedEdge.to() == vert){
              route.add(new Route(cityNames.get(prevVert),cityNames.get(vert),directedEdge.distance,directedEdge.cost));
            }
          }
          prevVert = vert;
        }
      }
    }
    routeList.add(route);
    return routeList;
  }


  public Set<ArrayList<Route>> shortestDistanceItinerary(String source, String transit, String destination) throws CityNotFoundException {
    Set<ArrayList<Route>> route1 = shortestDistanceItinerary(source, transit);
    Set<ArrayList<Route>> route2 = shortestDistanceItinerary(transit, destination);

    ArrayList<Route> next1 = route1.iterator().next();
    ArrayList<Route> next2 = route2.iterator().next();

    HashSet<ArrayList<Route>> routeSet = new HashSet<>();
    next1.addAll(next2);
    routeSet.add(next1);
    return routeSet;
  }


  public boolean addCity(String city){
    for (int i = 0; i < G.v; i++){
    if(!cityNames.get(i).equals(city)){
      try {
        cityNames.add(city);
        G.v ++;
        return true;
      }catch (Exception e){
        return false;
        }
      }
    }
    return false;
  }


  public boolean addRoute(String source, String destination, int distance, double price) throws CityNotFoundException {
    boolean addExist = false;
    int from = cityNames.indexOf(source);
    int to = cityNames.indexOf(destination);
    for(int i = 0; i < G.v; i++) {
      for(WeightedDirectedEdge e : G.adj(i)) {
        if(Objects.equals(e.from(), from) && Objects.equals(e.to(), to)){
          addExist = true;
        }
      }
    }
    if (addExist){
      return false;
    }else {
      G.addEdge(new WeightedDirectedEdge(from,to,distance,price));
      G.addEdge(new WeightedDirectedEdge(to,from,distance,price));
      return true;
    }
  }


  public boolean updateRoute(String source, String destination, int distance, double price) throws CityNotFoundException {
    boolean updateExist = false;
    int from = cityNames.indexOf(source);
    int to = cityNames.indexOf(destination);
    for(int i = 0; i < G.v; i++) {
      for(WeightedDirectedEdge e : G.adj(i)) {
        if(Objects.equals(e.from(), from) && Objects.equals(e.to(), to)){
          updateExist = true;
        }
      }
    }
    if(updateExist){
      for(int i = 0; i < G.v; i++) {
        for(WeightedDirectedEdge e : G.adj(i)) {
          if(e.from() == from && e.to() == to) {
            e.distance = distance;
            e.cost = price;
          }
          if(e.from() == to && e.to() == from) {
            e.distance = distance;
            e.cost = price;
          }
        }
      }
      return true;
    }
    return false;
  }


  private class Digraph {
    private int v;
    private int e;
    private LinkedList<WeightedDirectedEdge>[] adj;
    private boolean[] marked;
    private int[] edgeTo;
    private int[] distTo;

    public Digraph(int v) {
      if(v<0) throw new RuntimeException("Number of vertices must be nonnegative");
      this.v=v;
      this.e=0;
      @SuppressWarnings("unchecked")
      LinkedList<WeightedDirectedEdge>[] temp = (LinkedList<WeightedDirectedEdge>[]) new LinkedList[v];
      adj = temp;
      for(int i = 0; i<v; i++)
        adj[i] = new LinkedList<WeightedDirectedEdge>();
    }

    public void addEdge(WeightedDirectedEdge edge) {
      int from = edge.from();
      adj[from].add(edge);
      e++;
    }
    public int v() {
      return this.v;
    }

    public Iterable<WeightedDirectedEdge> adj(int v) {
      return adj[v];
    }

    public Iterable<WeightedDirectedEdge> edges() {
      ArrayList<WeightedDirectedEdge> list = new ArrayList<WeightedDirectedEdge>();
      for (int v = 0; v < v; v++) {
        int selfLoops = 0;
        for (WeightedDirectedEdge e : adj(v)) {
          if (e.other(v) > v) {
            list.add(e);
          }

          else if (e.other(v) == v) {
            if (selfLoops % 2 == 0)
              list.add(e);
            selfLoops++;
          }
        }
      }
      return list;
    }

    public void bfs(int source)
    {
      marked = new boolean[this.v];
      distTo = new int[this.v];
      edgeTo = new int[this.v];
      Queue<Integer> q = new LinkedList<Integer>();
      for(int i = 0; i<v; i++)
      {
        distTo[i] = INFINITY;
        marked[i] = false;
      }
      distTo[source] = 0;
      marked[source] = true;
      q.add(source);
      while(!q.isEmpty()) {
        int v = q.remove();
        for(WeightedDirectedEdge w : adj(v)) {
          if(!marked[w.to()]) {
            edgeTo[w.to()] = v;
            distTo[w.to()] = distTo[v]+1;
            marked[w.to()] = true;
            q.add(w.to());
          }
        }
      }
    }

    public void dijkstras(int source, int destination, boolean costNotDistance) {
      marked = new boolean[this.v];
      distTo = new int[this.v];
      edgeTo = new int[this.v];
      for(int i = 0; i<v; i++) {
        distTo[i] = INFINITY;
        marked[i] = false;
      }
      distTo[source] = 0;
      marked[source] = true;
      int nMarked = 1;
      int current = source;
      while(nMarked < this.v) {
        for(WeightedDirectedEdge w : adj(current)) {
          if(costNotDistance) {
            if(distTo[current] + w.cost() < distTo[w.to()]) {
              edgeTo[w.to()] = current;
              distTo[w.to()] = distTo[current] + (int)w.cost;
            }
          }
          else {
            if(distTo[current] + w.distance() < distTo[w.to()]) {
              edgeTo[w.to()] = current;
              distTo[w.to()] = distTo[current] + w.distance;
            }
          }
        }
        int min = INFINITY;
        current = -1;
        for(int i = 0; i<distTo.length; i++) {
          if(marked[i]) continue;
          if(distTo[i] < min) {
            min = distTo[i];
            current = i;
          }
        }
        if(current>= 0) {
          marked[current] = true;
          nMarked++;
        }
        else break;
      }
    }
  }


  private class WeightedDirectedEdge {
    private int v;
    private int w;
    private double cost;
    private int distance;

    public WeightedDirectedEdge(int v, int w,  int distance, double cost) {
      this.v=v;
      this.w=w;
      this.setCost(cost);;
      this.setDistance(distance);
    }

    public int from() {
      return v;
    }

    public int to() {
      return w;
    }

    public void setCost(double cost) {
      this.cost = cost;
    }

    public void setDistance(int distance) {
      this.distance = distance;
    }

    public int distance() {
      return distance;
    }

    public double cost() {
      return cost;
    }

    public int other(int vertex) {
      if (vertex == v)
        return w;
      else if (vertex == w)
        return v;
      else
        throw new RuntimeException("Illegal endpoint");
    }
  }








}



