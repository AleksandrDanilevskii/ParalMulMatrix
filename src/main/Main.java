import java.io.*;

public class Main {
    public static void main(String[] args) {

        try {
            BufferedReader s = new BufferedReader(new FileReader("src/main/in1.txt"));
            SMatrix matrixASparse = new SMatrix(s);

            s = new BufferedReader(new FileReader("src/main/in2.txt"));
            SMatrix matrixBSparse = new SMatrix(s);

            s = new BufferedReader(new FileReader("src/main/in3.txt"));
            DMatrix matrixADense = new DMatrix(s);

            s = new BufferedReader(new FileReader("src/main/in4.txt"));
            DMatrix matrixBDense = new DMatrix(s);

            SMatrix s_s = (SMatrix) matrixASparse.mul(matrixBSparse);
            BufferedWriter sp = new BufferedWriter(new FileWriter(("src/main/SxS.txt")));
            s_s.mapOut(sp);
            sp.close();

            DMatrix d_d = (DMatrix) matrixADense.mul(matrixBDense);
            BufferedWriter dn = new BufferedWriter(new FileWriter(("src/main/DxD.txt")));
            d_d.matOut(dn);
            dn.close();

            /*SMatrix d_s = (SMatrix) matrixADense.mul(matrixBSparse);
            sp = new BufferedWriter(new FileWriter(("src/main/DxS.txt")));
            d_s.mapOut(sp);
            sp.close();*/

            /*SMatrix s_d = (SMatrix) matrixASparse.mul(matrixBDense);
            sp = new BufferedWriter(new FileWriter(("src/main/SxD.txt")));
            s_d.mapOut(sp);
            sp.close();*/

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
