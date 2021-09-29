package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class HelperClass {



    public boolean binarySearch (List<Produkt> listPro, int u, int o, int gesucht){

        int m = u + ( (o - u) / 2 );
        boolean barcodefound = false;

        if (gesucht > Integer.parseInt(listPro.get(m).getBarcode())) {
            binarySearch (listPro, m+1, o, gesucht);
        }
        else if (gesucht < Integer.parseInt(listPro.get(m).getBarcode()) && u != m) {

            binarySearch (listPro, u, m-1, gesucht);
        }
        else if ( gesucht == Integer.parseInt(listPro.get(m).getBarcode())) {
            barcodefound = true;
            System.out.println(gesucht + " an Index " + m + " enthalten");

        } else {
            barcodefound = false;
        }
        return barcodefound;
    }

}
