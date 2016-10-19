package project.warehouse.function;

import java.util.HashSet;
import java.util.Random;

public class Cube {

    public static void main(String[] args) {
        Object[][][] cube = new Object[26][26][26];
        Random rm = new Random();
        for (int a = 0; a < cube.length; a++)
            for (int b = 0; b < cube[a].length; b++) {
                for (int c = 0; c < cube[a][b].length; c++) {
                    HashSet<Object> hs = new HashSet();
                    if (rm.nextInt(2) == 0) {
                        hs.add(((char)(rm.nextInt(26) + 'A')) + " , " +
                           ((char)(rm.nextInt(26) + 'A')) + " , " +
                           ((char)(rm.nextInt(26) + 'A')));
                    } else {
                        hs.add(((char)(rm.nextInt(26) + 'A')) + " . " +
                           ((char)(rm.nextInt(26) + 'A')) + " . " +
                           ((char)(rm.nextInt(26) + 'A')));
                    }
                    if (hs.iterator().hasNext()) {
                        cube[a][b][c] = hs.iterator().next();
                    }
            }
        }
        try {
            int[] cur = {rm.nextInt(26), rm.nextInt(26), rm.nextInt(26)};
            System.out.println("\t\t" + cube[cur[0]][cur[1]-1][cur[2]] + "\n\t\t    |");
            System.out.println("\t\t    |" + "  " + cube[cur[0]+1][cur[1]][cur[2]]);
            System.out.println("\t\t    |\t  /");
            System.out.println("\t\t    |\t /");
            System.out.print(cube[cur[0]][cur[1]][cur[2]-1] + " ----- ");
            System.out.print(cube[cur[0]][cur[1]][cur[2]]);
            System.out.println(" ----- " + cube[cur[0]][cur[1]][cur[2]+1]);
            System.out.println("\t       /    |");
            System.out.println("\t      /     |");
            System.out.println("\t " + cube[cur[0]-1][cur[1]][cur[2]] + "  |");
            System.out.println("\t\t    |");
            System.out.println("\t\t" + cube[cur[0]][cur[1]+1][cur[2]]);
            
//            for (Object[][] cube1 : cube) {
//                for (Object[] cube11 : cube1) {
//                    for (Object cube111 : cube11) {
//                        System.out.println(cube111);
//                    }
//                }
//            }
        } catch (Exception ex) {
            
        }
    }
}