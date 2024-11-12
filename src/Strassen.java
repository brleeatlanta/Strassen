import java.io.*;
import java.util.*;

public class Strassen
{
    public static class Node extends Thread {
        Node left, right, parent;
        int[][] data;
        int weight;

        public Node(Node parent) {
            this.parent = parent;
            this.left = null;
            this.right = null;
            data = null;
            weight = 0;
        }
        public void run() {}
    }

    public static Node createBiTree(int n) {
        n += 1;
        n *= 2;
        Node base = new Node(null);
        for (int i = 0; i < n; i++) {
            insertNode(base);
        }
        return base;
    }

    public static void insertNode(Node start) {
        if (start.left == null) {
            start.left = new Node(start);
            start.weight++;
        } else if (start.right == null) {
            start.right = new Node(start);
            start.weight++;
        } else if (start.left.weight >= start.right.weight) {
            insertNode(start.left);
        } else {
            insertNode(start.right);
        }
    }
    
    /** Function to multiply two matrices
     * @param A first matrix to be multiplied
     * @param B second matrix to be multiplied
     * @return result of the multiplied matrices
     */
    public int[][] multiply(int[][] A, int[][] B)
    {
        int n = A.length;
        int[][] R = new int[n][n];
        //base case
        if (n == 1)
            R[0][0] = A[0][0] * B[0][0];
        else
        {
            int[][] A11 = new int[n/2][n/2];
            int[][] A12 = new int[n/2][n/2];
            int[][] A21 = new int[n/2][n/2];
            int[][] A22 = new int[n/2][n/2];
            int[][] B11 = new int[n/2][n/2];
            int[][] B12 = new int[n/2][n/2];
            int[][] B21 = new int[n/2][n/2];
            int[][] B22 = new int[n/2][n/2];

            //Dividing matrix A into 4 halves
            split(A, A11, 0 , 0);
            split(A, A12, 0 , n/2);
            split(A, A21, n/2, 0);
            split(A, A22, n/2, n/2);
            //Dividing matrix B into 4 halves
            split(B, B11, 0 , 0);
            split(B, B12, 0 , n/2);
            split(B, B21, n/2, 0);
            split(B, B22, n/2, n/2);

            /*
             M1 = (A11 + A22)(B11 + B22)
             M2 = (A21 + A22) B11
             M3 = A11 (B12 - B22)
             M4 = A22 (B21 - B11)
             M5 = (A11 + A12) B22
             M6 = (A21 - A11) (B11 + B12)
             M7 = (A12 - A22) (B21 + B22)
             */

            int [][] M1 = multiply(add(A11, A22), add(B11, B22));
            int [][] M2 = multiply(add(A21, A22), B11);
            int [][] M3 = multiply(A11, sub(B12, B22));
            int [][] M4 = multiply(A22, sub(B21, B11));
            int [][] M5 = multiply(add(A11, A12), B22);
            int [][] M6 = multiply(sub(A21, A11), add(B11, B12));
            int [][] M7 = multiply(sub(A12, A22), add(B21, B22));

            /*
             C11 = M1 + M4 - M5 + M7
             C12 = M3 + M5
             C21 = M2 + M4
             C22 = M1 - M2 + M3 + M6
             */
            int [][] C11 = add(sub(add(M1, M4), M5), M7);
            int [][] C12 = add(M3, M5);
            int [][] C21 = add(M2, M4);
            int [][] C22 = add(sub(add(M1, M3), M2), M6);

            //Join 4 halves into one result matrix
            join(C11, R, 0 , 0);
            join(C12, R, 0 , n/2);
            join(C21, R, n/2, 0);
            join(C22, R, n/2, n/2);
        }
        //Return result
        return R;
    }
    /** Function to subtract two matrices
     * @param A first matrix to be multiplied
     * @param B second matrix to be multiplied
     * @return result of the multiplied matrices
     */
    public int[][] sub(int[][] A, int[][] B)
    {
        int n = A.length;
        int[][] C = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                C[i][j] = A[i][j] - B[i][j];
        return C;
    }
    /** Function to add two matrices
     * @param A matrix to be subtracted from
     * @param B matrix to subtract with
     * @return result of the subtracted matrices
     */
    public int[][] add(int[][] A, int[][] B)
    {
        int n = A.length;
        int[][] C = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                C[i][j] = A[i][j] + B[i][j];
        return C;
    }
    /** Function to split parent matrix into child matrices
     * @param P parent matrix
     * @param C child matrix
     * @param iB which column to start at in the parent matrix
     * @param jB which row to start at in the parent matrix
     */
    public void split(int[][] P, int[][] C, int iB, int jB)
    {
        for(int i1 = 0, i2 = iB; i1 < C.length; i1++, i2++)
            for(int j1 = 0, j2 = jB; j1 < C.length; j1++, j2++)
                C[i1][j1] = P[i2][j2];
    }
    /** Function to join child matrices into parent matrix
     * @param C child matrix
     * @param P parent matrix
     * @param iB which column to start at in the parent matrix
     * @param jB which row to start at in the parent matrix
     */
    public void join(int[][] C, int[][] P, int iB, int jB)
    {
        for(int i1 = 0, i2 = iB; i1 < C.length; i1++, i2++)
            for(int j1 = 0, j2 = jB; j1 < C.length; j1++, j2++)
                P[i2][j2] = C[i1][j1];
    }

    /** Function to read matrices from file
     * @param fileName name of the file to be read from
     * @return a matrix
     */
    public static String readMatricesFromFile(String fileName) {
        try {
            String result = "";
            File file = new File(fileName);
            Scanner fileReader = new Scanner(file);
            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                result = result.concat(line);
            }
            return result;
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return "";
    }

    /** Function to split a string matrix into a 2D array
     * @param combined the string matrix
     * @param size the size of the matrix
     * @param firstHalf whether the resulting matrix is the first or second half
     * @return a matrix in a 2D array
     */
    public static int[][] splitMatrices(String combined, int size, boolean firstHalf) {
        int[][] matrix = new int[size][size];
        String line;

        if (firstHalf) {line = combined.substring(0, combined.length()/2);}
        else {line = combined.substring(combined.length()/2);}

        int i = 0;
        for (int j = 0; j < size; j++) {
            for (int k = 0; k < size; k++) {
                matrix[j][k] = Integer.parseInt(String.valueOf(line.charAt(i++)));
            }
        }
        return matrix;
    }

    /** Function to write a matrix into a file (result.txt)
     * @param result the matrix to be written
     */
    public static void writeResultToFile(int[][] result) {
        try {
            FileWriter myWriter = new FileWriter("result.txt");
            for (int i = 0; i < result.length; i++) {
                myWriter.write(Arrays.deepToString(result));
            }
            myWriter.close();
            System.out.println("\nSuccessfully wrote result to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /** Main function **/
    public static void main (String[] args)
    {
        Scanner scan = new Scanner(System.in);
        System.out.println("Strassen Multiplication Algorithm Test\n");
        // Make an object of Strassen class
        Strassen s = new Strassen();

        //initializing N for later
        int N = 0, choice = 0;
        int[][] A = null, B = null, result = null;
        String test;
        long t0, t1 = 0, t;
        boolean cont = true;
        Node root = null;

        while (cont) {
            System.out.print("Which matrices would you like to calculate?\n" +
                    "0) test\n" +
                    "1) 1k x 1k\n" +
                    "2) 2k x 2k\n" +
                    "3) 4k x 4k\n" +
                    "4) 8k x 8k\n" +
                    "5) 16k x 16k\n" +
                    "Choice: ");
            choice = scan.nextInt();
            if (choice >= 0 && choice <= 5) {
                cont = false;
            } else {
                System.out.println("Invalid input. Please try again.");
            }
        }

        switch (choice) {
            case 0: test = readMatricesFromFile("testmatrix.txt");
                A = splitMatrices(test,8,true);
                B = splitMatrices(test,8,false);
                result = new int[8][8];
                break;
            case 1: //will be 1k x 1k
                //to be added
                break;
            case 2: //will be 2k x 2k
                //to be added
                break;
            case 3: //will be 4k x 4k
                //to be added
                break;
            case 4: //will be 8k x 8k
                //to be added
                break;
            case 5: //will be 16k x 16k
                //to be added
                break;
        } //2 4 8 16 32 64 128 256 512 1024 2048 4096 8192 16384 32768
        //  1 2 3  4  5  6   7   8   9   10   11   12   13    14    15

        cont = true;
        while (cont) {
            System.out.print("How many cores/threads should be used?\n" +
                    "0) 1 core/threads\n" +
                    "1) 3 cores/threads\n" +
                    "2) 7 cores/threads\n" +
                    "3) 15 cores/threads\n" +
                    "4) 31 cores/threads\n" +
                    "Choice: ");
            choice = scan.nextInt();
            if (choice >= 0 && choice <= 4) {
                cont = false;
            } else {
                System.out.println("Invalid input. Please try again.");
            }
        }

        switch (choice) {
            case 0 -> {root = createBiTree(1);}
            case 1 -> {root = createBiTree(3);}
            case 2 -> {root = createBiTree(7);}
            case 3 -> {root = createBiTree(15);}
            case 4 -> {root = createBiTree(31);}
        }

        //checking array sizes
        int Ar = A.length;
        int Ac = A[0].length;
        int Br = B.length;
        int Bc = B[0].length;

        if (Ar == Ac && Ar == Br && Ar == Bc) {
            N = Ar;
        }

        System.out.println("\nArray A =>");

        for (int i = 0; i < N; i++)
        {
            for (int j = 0; j < N; j++)
                System.out.print(A[i][j] +" ");
            System.out.println();
        }

        System.out.println("\nArray B =>");
        for (int i = 0; i < N; i++)
        {
            for (int j = 0; j < N; j++)
                System.out.print(B[i][j] +" ");
            System.out.println();
        }

        t0 = System.currentTimeMillis();
        int[][] C = s.multiply(A, B);
        result = C;
        t1 = System.currentTimeMillis();

        System.out.println("\nProduct of matrices A and  B : ");
        for (int i = 0; i < N; i++)
        {
            for (int j = 0; j < N; j++)
                System.out.print(C[i][j] +" ");
            System.out.println();
        }

        writeResultToFile(result);

    }
}
