import java.io.*;
import org.junit.Test;
import java.io.BufferedReader;
import java.io.IOException;

public class MatrixTest {
    BufferedReader s;
    SMatrix ASparse1;
    DMatrix ADense1;
    SMatrix ASparse2;
    DMatrix ADense2;
    SMatrix result;
    DMatrix result1;
    
    public MatrixTest() {
        try {
            s = new BufferedReader(new FileReader("src/tests/test1.txt"));
            ASparse1 = new SMatrix(s);
            s = new BufferedReader(new FileReader("src/tests/test1.txt"));
            ADense1 = new DMatrix(s);

            s = new BufferedReader(new FileReader("src/tests/test2.txt"));
            ASparse2 = new SMatrix(s);
            s = new BufferedReader(new FileReader("src/tests/test2.txt"));
            ADense2 = new DMatrix(s);

            s = new BufferedReader(new FileReader("src/tests/test_result.txt"));
            result = new SMatrix(s);
            s = new BufferedReader(new FileReader("src/tests/test_result.txt"));
            result1 = new DMatrix(s);
        } catch (IOException e)

        {
            e.printStackTrace();
        }
    }


    /*@Test
    public void mulS_D() {
        SMatrix s_d = (SMatrix) ASparse1.mul(ADense2);
        result.equals(s_d);
    }*/

    @Test
    public void mulS_S() {
        SMatrix s_s = (SMatrix) ASparse1.mul(ASparse2);
        result.equals(s_s);
        ;

    }

    /*@Test
    public void mulD_S() {
        SMatrix d_s = (SMatrix) ADense1.mul(ASparse2);
        result.equals(d_s);
        ;
    }*/

    @Test
    public void mulD_D() {
        DMatrix d_d = (DMatrix) ADense1.mul(ADense2);
        result1.equals(d_d);
        ;

    }


}

