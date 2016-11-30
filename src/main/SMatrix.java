import java.util.*;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

public class SMatrix implements Matrix {

    public int size;
    public ConcurrentHashMap<Integer, row> map;

    public SMatrix(ConcurrentHashMap<Integer, row> m, int size) {
        this.size = size;
        this.map = m;
    }

    public SMatrix(int size) {
        this.size = size;
        this.map = new ConcurrentHashMap<>();
    }

    public SMatrix(BufferedReader s) {
        try {
            String temp = s.readLine();
            String[] arr = temp.split(" ");
            int k = arr.length;
            int number;
            size = k;
            map = new ConcurrentHashMap<Integer, row>();
            row tmap = new row();

            for (int j = 0; j < size; j++) {
                number = Integer.parseInt(arr[j]);
                if (number != 0) {
                    tmap.put(j, number);
                }
            }
            if (tmap != null) {
                map.put(0, tmap);
            }


            for (int i = 1; i < size; i++) {

                temp = s.readLine();
                arr = temp.split(" ");
                tmap = new row();
                for (int j = 0; j < size; j++) {
                    number = Integer.parseInt(arr[j]);
                    if (number != 0) {
                        tmap.put(j, number);
                    }
                }
                if (tmap != null) {
                    map.put(i, tmap);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Matrix mul(Matrix other) {
        if (other instanceof SMatrix) try {
            return this.mulSparseSparse((SMatrix) other);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        //else return this.mulSparseDence((DMatrix) other);
        return other;
    }

    public SMatrix mulSparseSparse(SMatrix other) throws InterruptedException {

        other = other.MatrixSTrans();
        SMatrix result = new SMatrix(size);
        Iterator<ConcurrentHashMap.Entry<Integer, row>> iter1 = this.map.entrySet().iterator();
        MulSS t = new MulSS(this.map, other.map, result.map, iter1);

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

    class MulSS implements Runnable {
        ConcurrentHashMap<Integer, row> A;
        ConcurrentHashMap<Integer, row> B;
        ConcurrentHashMap<Integer, row> result;
        Iterator<ConcurrentHashMap.Entry<Integer, row>> iter1;

        public MulSS(ConcurrentHashMap<Integer, row> A, ConcurrentHashMap<Integer, row> B, ConcurrentHashMap<Integer, row> result, Iterator<ConcurrentHashMap.Entry<Integer, row>> iter1) {
            this.A = A;
            this.B = B;
            this.result = result;
            this.iter1 = iter1;
        }

        public void run() {
            while (iter1.hasNext()) {
                Map.Entry entry1 = iter1.next();
                Integer key1 = (Integer) entry1.getKey();
                HashMap<Integer, Integer> value1 = (HashMap<Integer, Integer>) entry1.getValue();// строки первой матрицы
                Iterator<HashMap.Entry<Integer, row>> iter2 = B.entrySet().iterator();
                row resRow = new row();
                while (iter2.hasNext()) {
                    HashMap.Entry entry2 = iter2.next();
                    Integer key2 = (Integer) entry2.getKey();
                    HashMap<Integer, Integer> value2 = (HashMap<Integer, Integer>) entry2.getValue();// строки второй матрицы
                    Iterator iterElement = value1.entrySet().iterator();
                    int resValue = 0;
                    while (iterElement.hasNext()) {
                        HashMap.Entry entryElement = (HashMap.Entry) iterElement.next();
                        Integer keyElement1 = (Integer) entryElement.getKey();
                        Integer valueElement1 = (Integer) entryElement.getValue();
                        if (value2.get(keyElement1) != null) {
                            int a = value2.get(keyElement1);
                            resValue = resValue + valueElement1 * a;
                        }
                    }
                    if (resValue != 0) {
                        resRow.put(key2, resValue);
                    }
                }
                if (resRow != null) {
                    result.put(key1, resRow);
                }
            }
        }
    }

    public SMatrix MatrixSTrans() {
        Iterator<Map.Entry<Integer, row>> iter = map.entrySet().iterator(); // получаем строки
        ConcurrentHashMap<Integer, row> matrixTr = new ConcurrentHashMap<Integer, row>();
        while (iter.hasNext()) {
            Map.Entry entry = iter.next();
            Integer keyRow = (Integer) entry.getKey();// получаем номер строки
            HashMap<Integer, row> value = (HashMap<Integer, row>) entry.getValue(); // получаем элементы определенной строки
            Iterator iterRow = value.entrySet().iterator(); // берем по элементно
            while (iterRow.hasNext()) {
                row RowTr = new row();
                Map.Entry entryRow = (Map.Entry) iterRow.next();
                Integer keyElements = (Integer) entryRow.getKey();
                Integer valueElements = (Integer) entryRow.getValue();
                RowTr = matrixTr.get(keyElements);
                if (RowTr == null) {
                    RowTr = new row();
                }
                RowTr.put(keyRow, valueElements);
                matrixTr.put(keyElements, RowTr);
            }
        }
        return new SMatrix(matrixTr, size);
    }

    /*public SMatrix mulSparseDence(DMatrix other) {
        SMatrix res = new SMatrix(size);
        other = other.MatrixSTrans();
        int[][] a = other.matrix;
        Iterator<Map.Entry<Integer, row>> iter1 = this.map.entrySet().iterator();// итератор спарс матрицы
        while (iter1.hasNext()) {
            Map.Entry entry1 = iter1.next();
            Integer key1 = (Integer) entry1.getKey();
            HashMap<Integer, Integer> value1 = (HashMap<Integer, Integer>) entry1.getValue();// получаем определенную строку
            row resRow = new row();
            for (int i = 0; i < size; i++) {
                int resValue = 0;
                Iterator iterElement = value1.entrySet().iterator(); // получаем элементы определенной строки
                while (iterElement.hasNext()) {
                    Map.Entry entryElement = (Map.Entry) iterElement.next();
                    Integer keyElement = (Integer) entryElement.getKey();// столбец элемента
                    Integer valueElement = (Integer) entryElement.getValue();// сам элемент
                    if (other.matrix[i][keyElement] != 0.0) {
                        resValue = resValue + valueElement * a[i][keyElement];
                    }
                }
                if (resValue != 0.0) {
                    resRow.put(i, resValue);
                }
            }
            if (resRow != null) {
                res.map.put(key1, resRow);
            }
        }
        return res;
    }*/


    public void mapOut(BufferedWriter sp) {
        try {
            int e;
            for (int i = 0; i < size; i++) {
                row a = map.get(i);
                if (a != null) {
                    for (int j = 0; j < size; j++) {
                        if (a.get(j) != null) {
                            e = a.get(j);
                            sp.write(e + " ");
                        } else {
                            sp.write("0" + " ");
                        }
                    }
                    sp.write("\n");

                } else {
                    for (int j = 0; j < size; j++) {
                        sp.write("0 ");
                    }
                    sp.write("\n");
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }


    public boolean equals(SMatrix other) {
        boolean t = true;
        for (int i = 0; i < size; i++) {
            row a = this.map.get(i);
            row b = other.map.get(i);
            for (int j = 0; j < size; j++) {
                if (a.get(j) != b.get(j)) {
                    t = false;
                }
            }
        }
        return t;
    }
}