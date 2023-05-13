package org.convertidor.neodatis;

import org.convertidor.model.ClassConstants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class NeodatisClassGenerator {

    private String fumada = "public static void main (String[args]) { \n" +
           // "File f1 = new File("+constants.getPATH()+") \nString[] list = f1.list();\n" +
            "\t\t\tif(list.length>0) {\n" +
            "\t\t\tfor (int i = 0; i < list.length; i++) { \n" +
            "String tableName = list[i].replaceAll(.txt,"+""+"); \n" +
            "";

    public void getReady() {
        ClassConstants constants = new ClassConstants();
        File neodatisClass = new File(constants.getPATH() + "convertidor.java");
        try {
            neodatisClass.createNewFile();
            BufferedWriter bufIn = new BufferedWriter(new FileWriter(neodatisClass));
            bufIn.write(constants.getPACKAGE() + constants.getIMPORTS());

        }catch (Exception e){

        }
    }

}
