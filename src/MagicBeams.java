import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

record Beam(int r, int c, int length, int id, int[] direction) {
}

public class MagicBeams {
  private final int R, C, N, L;
  private Beam[][] grid;

  public MagicBeams(int R, int C, int N, int L) {
    this.R = R;
    this.C = C;
    this.N = N;
    this.L = L;
    this.grid = new Beam[R][C];
  }

  public void addBeam(int id, int r, int c, int l, char d) {
    int[] direction = convertDirection(d);
    Beam beam = new Beam(r, c, l, id, direction);

    for (int i = 0; i < l; i++) {
      grid[r][c] = beam;
      r += direction[0];
      c += direction[1];
    }
  }

  public List<Integer> solve() throws Exception {
    List<Integer> answer = new LinkedList<>();
    Set<Beam> chosenBeams = new HashSet<>();
    Queue<Beam> notProcessed = new ArrayDeque<>();
    Map<Integer, Integer> degrees = new HashMap<>();
    Map<Integer, Set<Beam>> adj = new HashMap<>(); // Blocker -> Blocked

    for (int c = L; c < N + L; c++) { // get beams in the collumns to be cleared
      for (int r = 0; r < R; r++) {
        Beam beam = grid[r][c];

        if (beam != null) {
          chosenBeams.add(beam);
          notProcessed.add(beam);
        }
      }
    }

    for (Beam beam : chosenBeams) {
      degrees.putIfAbsent(beam.id(), 0);
      adj.putIfAbsent(beam.id(), new HashSet<>());
    }

    if (chosenBeams.isEmpty())
      throw new Exception("False Alarm");

    Set<Beam> processed = new HashSet<>();
    while (!notProcessed.isEmpty()) { // Get beams that block the already choosen beams
      Beam beam = notProcessed.poll();

      if (processed.contains(beam))
        continue;

      processed.add(beam); // Don't need to check if contains, because add already checks it. (Sets can't
                           // have duplicates

      int tailR = beam.r() + (beam.length() - 1) * beam.direction()[0];
      int blockerR = tailR + beam.direction()[0];

      int tailC = beam.c() + (beam.length() - 1) * beam.direction()[1];
      int blockerC = tailC + beam.direction()[1];

      while (blockerR >= 0 && blockerR < R && blockerC >= 0 && blockerC < C) {
        Beam blocker = grid[blockerR][blockerC];

        if (blocker != null && blocker != beam) { // Do we need this != beam
          notProcessed.add(blocker);
          chosenBeams.add(blocker);

          degrees.putIfAbsent(blocker.id(), 0);
          adj.putIfAbsent(blocker.id(), new HashSet<>());

          degrees.put(beam.id(), degrees.get(beam.id()) + 1);
          adj.get(blocker.id()).add(beam);
        }

        blockerR += beam.direction()[0];
        blockerC += beam.direction()[1];
      }

    }

    PriorityQueue<Integer> zeroDegree = new PriorityQueue<>();

    for (Map.Entry<Integer, Integer> e : degrees.entrySet()) {
      if (e.getValue() == 0)
        zeroDegree.add(e.getKey());
    }

    while (!zeroDegree.isEmpty()) {
      int id = zeroDegree.poll();
      answer.add(id);
      for (Beam blocked : adj.get(id)) {
        int newDeg = degrees.merge(blocked.id(), -1, Integer::sum);
        if (newDeg == 0)
          zeroDegree.add(blocked.id());
      }
      degrees.remove(id);
    }

    if (!degrees.isEmpty()) {
      throw new Exception("Disaster");
    }

    return answer;

  }

  static int[] convertDirection(char dir) {
    switch (dir) {
      case 'N':
        return new int[] { -1, 0 };
      case 'S':
        return new int[] { 1, 0 };
      case 'E':
        return new int[] { 0, 1 };
      case 'W':
        return new int[] { 0, -1 };
      default:
        return new int[] { 0, 0 };
    }
  }
}

// My idea, have a way of knowing what beam blocks each beam, for our example
// know that beam 1 is blocked by beam 4
// then go through all the beams in order from the example 1 -> 6
// always grab the lowest with no blockers and remove in our case 3 is the
// lowest and has no blockers
// next step 4 is the lowest and has no blockers
// 1 is the lowest and has no blokcers
// 5 is the lowest and has no blockers
// 6 is the lowest and has no blockers

// result [3,4,1,5,6]

// TODO: if we use a tree Map for the degrees then the keys are always sorted
// from min to max, so we just need to find the first with degree = 0