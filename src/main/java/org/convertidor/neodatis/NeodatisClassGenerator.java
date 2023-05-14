package org.convertidor.neodatis;

import org.convertidor.model.ClassConstants;

import java.io.*;
import java.util.ArrayList;

public class NeodatisClassGenerator {

    private String fumada = "public static void main (String [] args) { \n" +
           // "File f1 = new File("+constants.getPATH()+") \nString[] list = f1.list();\n" +
            "\t\t\tif(list.length>0) {\n" +
            "\t\t\tfor (int i = 0; i < list.length; i++) { \n" +
            "String tableName = list[i].replaceAll(.txt,"+""+"); \n" +
            "";

    public void getReady(ArrayList<String> tablas, ArrayList<ClassParams> classParams) {
        ClassConstants constants = new ClassConstants();
        File neodatisClass = new File(constants.getPATH() + "Convertidor.java");
        try {
            neodatisClass.createNewFile();
            BufferedWriter bufIn = new BufferedWriter(new FileWriter(neodatisClass));
            bufIn.write(constants.getPACKAGE() + constants.getIMPORTS());
            bufIn.write(metodoMain());
            bufIn.write(getDatosFicheros(constants,tablas,classParams));
            bufIn.write("}\n}");
            bufIn.close();
        }catch (Exception e){

        }
    }

    private String metodoMain() {
        String path = "\"C:\\Users\\ismaelor\\Desktop\"";
        return "public class Convertidor { \n public static void main (String[args]) {\n" +
                "ODB odb = ODBFactory.open("+path+");\n";

    }

    private String getDatosFicheros(ClassConstants constants,ArrayList<String> tablas,ArrayList<ClassParams> classParams) throws FileNotFoundException {
        File f1 = new File(constants.getPATH()+"/temp");
        String text = "";
        String[] list = f1.list();
        if(list.length>0) {
            for (int i = 0; i < list.length; i++) {
                File f2 = new File(f1, list[i]);
                String tableName = f2.getName().replaceAll(".txt","");
                int counter = 1;
                text+="ResulSet rs = new NeodatisQuerys().getResultSet(\""+tableName+"\"); \n";
                text+="while(rs.next()) {\n";
                BufferedReader buf = new BufferedReader(new FileReader(f2));
                ArrayList<String> types = new ArrayList<>();
                text += getMayus(tableName) + tableName +" = new "+getMayus(tableName)+"();\n";
                try {
                    while (true) {
                        types.add(getMayus(buf.readLine()));
                    }
                } catch (Exception e) {

                }
                for(int x = 0; x < types.size();x++){
                    if(!types.get(x).equalsIgnoreCase("String") && !types.get(x).equalsIgnoreCase("int")
                        && !types.get(x).equalsIgnoreCase("float") && !types.get(x).equalsIgnoreCase("double")){
                        //Fumada de porro
                    }
                    else{
                        text+= tableName + ".set"+getMayus(classParams.get(i).getValues().get(x))+"(rs.get"+getMayus(
                                types.get(x))+"("+counter++ +"));\n";
                    }
                }
                text+="odb.store("+tableName+");\n}\n";
                text+="rs.close();\n";
            }
        }
        return text;
    }
    private static String getMayus(String word){
        return word.toUpperCase().charAt(0) + word.substring(1);
    }

}
