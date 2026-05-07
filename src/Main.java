
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        int T = Integer.parseInt(in.readLine());

        for (int t = 0; t < T; t++) {
            String[] line1 = in.readLine().split(" ");
            int R = Integer.parseInt(line1[0]);
            int C = Integer.parseInt(line1[1]);

            String[] line2 = in.readLine().split(" ");
            int N = Integer.parseInt(line2[0]);
            int L = Integer.parseInt(line2[1]);

            int B = Integer.parseInt(in.readLine());

            MagicBeams magic = new MagicBeams(R, C, N, L);

            for (int i = 1; i <= B; i++) {
                String[] line3 = in.readLine().split(" ");
                int r = Integer.parseInt(line3[0]);
                int c = Integer.parseInt(line3[1]);
                int l = Integer.parseInt(line3[2]);
                char directionChar = line3[3].charAt(0);

                magic.addBeam(i, r, c, l, directionChar);
            }

            try {
                List<Integer> answer = magic.solve();

                for (int i = 0; i < answer.size() - 1; i++) {
                    System.out.print(answer.get(i) + " ");
                }

                System.out.println(answer.get(answer.size() - 1));

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
