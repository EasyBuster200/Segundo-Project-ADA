import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

record Beam(int r, int c, int length, int id, int[] direction) {
}

public class MagicBeams {
  private final int R, C, N, L;
  private int[][] grid;
  private Beam[] beams;
  private Queue<Beam> notProcessed = new ArrayDeque<>();
  private Set<Integer> inQueue = new HashSet<>();
  private Map<Integer, Integer> degrees = new HashMap<>();
  // Key: id Beam -> List of Beam ids that the key blocks
  private Map<Integer, Set<Integer>> adj = new HashMap<>();

  public MagicBeams(int R, int C, int N, int L, int B) {
    this.R = R;
    this.C = C;
    this.N = N;
    this.L = L;
    this.grid = new int[R][C];
    beams = new Beam[B + 1];
  }

  /**
   * Adds a new Beam to the program
   * 
   * @param id : unique id of the Beam
   * @param r  : starting line of the Beam
   * @param c  : starting collumn of the Beam
   * @param l  : length of the Beam
   * @param d  : character representing the Beams direction (E, W, S, N)
   */
  public void addBeam(int id, int r, int c, int l, char d) {
    int[] direction = convertDirection(d);
    Beam beam = new Beam(r, c, l, id, direction);
    beams[id] = beam;

    for (int i = 0; i < l; i++) {
      grid[r][c] = id;

      // When adding a new beam, we check if its in one of the collumns to be cleared,
      // and if it is we add it to the queue of beams to be processed
      if (L <= c && c < N + L) {
        if (inQueue.add(beam.id()))
          notProcessed.add(beam);

        degrees.putIfAbsent(beam.id(), 0);
        adj.putIfAbsent(beam.id(), new HashSet<>());
      }

      r += direction[0];
      c += direction[1];
    }
  }

  /**
   * Calculates in what order beams need to be removed to clear certain collumns of Beams
   * 
   * @return list with the ids of the beams to be removed, in the order they should be removed 
   */
  public List<Integer> solve() throws Exception {
    List<Integer> answer = new ArrayList<>();

    // First check if there are Beams to remove
    if (inQueue.isEmpty())
      throw new Exception("False alarm");

    /*
     * Next we need to find any beams that block the ones to be removed
     */
    while (!notProcessed.isEmpty()) {
      Beam beam = notProcessed.poll();

      // Calculate where the first possible position a blocker could be in (the first
      // square after the end of the beam, in the beams direction).
      int tailR = beam.r() + (beam.length() - 1) * beam.direction()[0];
      int blockerR = tailR + beam.direction()[0];

      int tailC = beam.c() + (beam.length() - 1) * beam.direction()[1];
      int blockerC = tailC + beam.direction()[1];

      // From that position walk the beams direction until the end of the board saving
      // any blockers found.
      while (blockerR >= 0 && blockerR < R && blockerC >= 0 && blockerC < C) {
        int blocker = grid[blockerR][blockerC];

        if (blocker != 0 && blocker != beam.id()) {

          if (inQueue.add(blocker))
            notProcessed.add(beams[blocker]);

          degrees.putIfAbsent(blocker, 0);
          adj.putIfAbsent(blocker, new HashSet<>());

          // Only if the blocker hadn't already been added to the beam do we increase the
          // degree of the beam
          if (adj.get(blocker).add(beam.id())) {
            degrees.put(beam.id(), degrees.get(beam.id()) + 1);
          }
        }

        blockerR += beam.direction()[0];
        blockerC += beam.direction()[1];
      }

    }

    // List with all the Beams that have no blockers (degree == 0)
    PriorityQueue<Integer> zeroDegree = new PriorityQueue<>();

    for (Map.Entry<Integer, Integer> e : degrees.entrySet()) {
      if (e.getValue() == 0)
        zeroDegree.add(e.getKey());
    }

    /*
     * Filling the answer list
     * 
     * As long as we have beams to be removed with degree == 0 we:
     * - Pick the lowest id beam with degree 0, and add it to the answer
     * - Lower the degree of any beams it blocks
     * - If the lowered degrees become 0, they get added to the list of zeroDegree
     */

    while (!zeroDegree.isEmpty()) {
      int id = zeroDegree.poll();

      answer.add(id);

      for (int blockedId : adj.get(id)) {
        int newDeg = degrees.get(blockedId) - 1;
        degrees.put(blockedId, newDeg);

        if (newDeg == 0)
          zeroDegree.add(blockedId);
      }

      degrees.remove(id);
    }

    // If we tried to remove all the beams that needed to be removed, and there asre
    // still beams left, then there is no way to remove them without a disaster
    if (!degrees.isEmpty()) {
      throw new Exception("Disaster");
    }

    return answer;

  }

  /**
   * Helper method that converts the direction char into an array of integers
   * representing the direction
   * 
   * @param dir : Character representing the direction of a Beam
   * @return a numerical representation of that direction 
   */
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