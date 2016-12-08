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
            String temp = s.readLine(); //читаем строку
            String[] arr = temp.split(" "); //разбиваем на числа
            int k = arr.length; //длина строки
            int number; //элемент
            size = k; //размер матрицы
            map = new ConcurrentHashMap<Integer, row>();
            row tmap = new row();

            for (int j = 0; j < size; j++) {
                number = Integer.parseInt(arr[j]); //переводим в int
                if (number != 0) {
                    tmap.put(j, number);//если не нуль, добавляем в tmap
                }
            }
            if (tmap != null) {
                map.put(0, tmap);//добавляем строку в map
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
        return other;
    }

    public SMatrix mulSparseSparse(SMatrix other) throws InterruptedException {

        other = other.MatrixSTrans();
        SMatrix result = new SMatrix(size);
        Iterator<ConcurrentHashMap.Entry<Integer, row>> iter1 = this.map.entrySet().iterator();//получаем ключи и значения map. множество строк
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
            //разбиваем iter1 на строки
            while (iter1.hasNext()) { //пока не конец
                Map.Entry entry1 = iter1.next(); //берем пару
                Integer key1 = (Integer) entry1.getKey(); //получаем номер строки
                HashMap<Integer, Integer> value1 = (HashMap<Integer, Integer>) entry1.getValue();// строки
                row resRow = new row();
                //разбиваем iter2
                for (Map.Entry entry2 : B.entrySet()) {
                    Integer key2 = (Integer) entry2.getKey();
                    HashMap<Integer, Integer> value2 = (HashMap<Integer, Integer>) entry2.getValue();
                    Iterator iterElement = value1.entrySet().iterator(); //множество ключей и зачений
                    int resValue = 0;
                    while (iterElement.hasNext()) {
                        HashMap.Entry entryElement = (HashMap.Entry) iterElement.next(); // берем элемент
                        Integer keyElement = (Integer) entryElement.getKey(); //получаем ключ
                        Integer valueElement = (Integer) entryElement.getValue();  //получаем значение. 1
                        if (value2.get(keyElement) != null) {
                            int a = value2.get(keyElement); //умнлжаем
                            resValue += valueElement * a;
                        }
                    }
                    if (resValue != 0) {
                        resRow.put(key2, resValue); //если не 0, добавляем
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
                Map.Entry entryRow = (Map.Entry) iterRow.next();
                Integer keyElements = (Integer) entryRow.getKey();
                Integer valueElements = (Integer) entryRow.getValue();
                row RowTr = matrixTr.get(keyElements);
                if (RowTr == null) {
                    RowTr = new row();
                }
                RowTr.put(keyRow, valueElements);
                matrixTr.put(keyElements, RowTr);
            }
        }
        return new SMatrix(matrixTr, size);
    }


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