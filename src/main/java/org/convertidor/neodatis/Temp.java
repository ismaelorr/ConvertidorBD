package org.convertidor.neodatis;

import org.convertidor.model.ClassConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Locale;

public class Temp {

    public static void main(String[] args) throws FileNotFoundException {
        ClassConstants classConstants = new ClassConstants();
        File f1 = new File(classConstants+"temp");
        String[] list = f1.list();
        if(list.length>0) {
            for (int i = 0; i < list.length; i++) {
                File f2 = new File(f1, list[i]);
                String tableName = f2.getName();
                BufferedReader buf = new BufferedReader(new FileReader(f2));
                ArrayList<String> types = new ArrayList<>();
                try{
                    while(true){
                        types.add(getMayus(buf.readLine()));
                    }
                }catch (Exception e){

                }

            }
        }
    }

    private static String getMayus(String word){
        return word.toUpperCase().charAt(0) + word.substring(1);
    }

}
