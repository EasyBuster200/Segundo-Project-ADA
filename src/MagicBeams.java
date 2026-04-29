import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

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

  public List<Integer> solve() throws Exception{
    List<Integer> answer = new LinkedList<>();
    Set<Beam> chosenBeams = new HashSet<>();
    Queue<Beam> notProcessed = new LinkedList<>();

    for (int c = L; c < N+L; c++) { // get chosen beams
        for (int r = 0; r < R; r++) {
            Beam beam = grid[r][c];

            if (beam != null) {
                chosenBeams.add(beam);
                notProcessed.add(beam);
            }
        }
    }

    if(chosenBeams.isEmpty())
        throw new Exception("False Alarm");

    Set<Beam> processed = new HashSet<>();
    while (!notProcessed.isEmpty()) {
        Beam beam = notProcessed.poll();

        if (processed.contains(beam))
            continue;

        processed.add(beam);

        int tailR = beam.r() + (beam.length() - 1) * beam.direction()[0];
        int blockerR = tailR + beam.direction()[0];
        
        int tailC = beam.c() + (beam.length() - 1) * beam.direction()[1];
        int blockerC = tailC + beam.direction()[1];

        while (blockerR >= 0 && blockerR < R && blockerC >= 0 && blockerC < C) {
            Beam blocker = grid[blockerR][blockerC];

            if(blocker != null && blocker != beam){ //Do we need this != beam
                notProcessed.add(blocker);
                chosenBeams.add(blocker);
            }
            // notProcesed
            // chosenBeams = 1,3,5,6,4
            blockerR += beam.direction()[0];
            blockerC += beam.direction()[1];
        }
        // Set<Intenger>  dontHaveBlocker 3,5,
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
