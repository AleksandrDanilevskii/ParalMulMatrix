import java.lang.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
/*import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;*/

public class DMatrix implements Matrix {
    public int size;
    public int matrix[][];

    public DMatrix(int[][] matrix, int size) {
        this.size = size;
        this.matrix = matrix;
    }

    public DMatrix(int size) {
        this.matrix = new int[size][size];
        this.size = size;
    }

    public DMatrix(BufferedReader s) {
        try {
            String t = s.readLine();
            String[] array = t.split(" ");
            int k = array.length;
            this.size = k;
            this.matrix = new int[size][size];
            int number;

            for (int j = 0; j < k; j++) {
                number = Integer.parseInt(array[j]);
                this.matrix[0][j] = number;
            }

            for (int i = 1; i < k; i++) {
                t = s.readLine();
                array = t.split(" ");

                for (int j = 0; j < k; j++) {
                    number = Integer.parseInt(array[j]);
                    this.matrix[i][j] = number;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Matrix mul(Matrix other) {
        if (other instanceof DMatrix) try {
            return this.mulDenseDense((DMatrix) other);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        //else return this.mulDenceSparse((SMatrix) other);
        return other;
    }

    public DMatrix mulDenseDense(DMatrix other) throws InterruptedException {
        other = other.MatrixSTrans();
        DMatrix result = new DMatrix(size);
        mulDD t = new mulDD(this.matrix, other.matrix, result.matrix);
        Thread t1 = new Thread(t);
        Thread t2 = new Thread(t);
        Thread t3 = new Thread(t);
        Thread t4 = new Thread(t);

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t1.join();
        t2.join();
        t3.join();
        t4.join();
        return result;
    }

    public class mulDD implements Runnable {
        int[][] A;
        int[][] B;
        int[][] result;
        int num = 0;

        public mulDD(int[][] A, int[][] B, int[][] result) {
            this.A = A;
            this.B = B;
            this.result = result;
        }

        public void run() {
            for (int i = next(); i < size; i = next()) {
                for (int j = 0; j < size; j++) {
                    for (int k = 0; k < size; k++) {
                        result[i][j] += A[i][k] * B[j][k];
                    }
                }
            }
        }

        public int next() {
            synchronized (this) {
                return num++;
            }
        }
    }

    /*public SMatrix mulDenceSparse(SMatrix other) {
        other = other.MatrixSTrans();
        SMatrix res = new SMatrix(size);
        for (int i = 0; i < size; i++) {
            row resRow = new row();
            Iterator<Map.Entry<Integer, row>> iter1 = other.map.entrySet().iterator();// итератор строк
            while (iter1.hasNext()) {
                Map.Entry entry1 = iter1.next();
                Integer key1 = (Integer) entry1.getKey();// ключ строки
                HashMap<Integer, Integer> value1 = (HashMap<Integer, Integer>) entry1.getValue();// сама строка
                Iterator iterElement = value1.entrySet().iterator();// итератор элементов
                int resValue = 0;
                while (iterElement.hasNext()) {
                    Map.Entry entryElement = (Map.Entry) iterElement.next();
                    Integer keyElement = (Integer) entryElement.getKey();// ключ элемента
                    Integer valueElement = (Integer) entryElement.getValue();//значение элемента
                    resValue = resValue + this.matrix[i][keyElement] * valueElement;
                }
                if (resValue != 0) {
                    resRow.put(key1, resValue);
                }
            }
            if (resRow != null) {
                res.map.put(i, resRow);
            }
        }
        return res;
    }*/

    public DMatrix MatrixSTrans() {
        int[][] mTr = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = i; j < size; j++) {
                int aT = this.matrix[i][j];
                mTr[i][j] = this.matrix[j][i];
                mTr[j][i] = aT;
            }
        }
        return new DMatrix(mTr, size);
    }

    public void matOut(BufferedWriter dn) {
        try {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    dn.write(matrix[i][j] + " ");
                }
                dn.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean equals(Object o) {
        boolean t = true;
        if (!(o instanceof DMatrix)) {
            return false;
        }
        DMatrix other = (DMatrix) o;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (this.matrix[i][j] != other.matrix[i][j]) {
                    t = false;
                }
            }
        }
        return t;
    }
}