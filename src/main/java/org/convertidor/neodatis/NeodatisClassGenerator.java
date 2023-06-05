package org.convertidor.neodatis;

import org.convertidor.conexion.Conexion;
import org.convertidor.model.ClassConstants;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class NeodatisClassGenerator {




    public void getReady(ArrayList<String> tablas, ArrayList<ClassParams> classParams,String schema,String path) {
        ClassConstants constants = new ClassConstants();
        File neodatisClass = new File(constants.getPATH() + "Convertidor.java");
        try {
            neodatisClass.createNewFile();
            BufferedWriter bufIn = new BufferedWriter(new FileWriter(neodatisClass));
            bufIn.write(constants.getPACKAGE() + constants.getIMPORTS());
            bufIn.write(metodoMain(path));
            bufIn.write(getDatosFicheros(tablas,constants,classParams,schema));
            bufIn.write("odb.close();\n}\n}");
            bufIn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String metodoMain(String path) {
        System.out.println(path);
       String absolutePath []= path.split("\\\\");
       String convertedPath = "";
       for(int i = 0; i < absolutePath.length; i++){
           convertedPath+= absolutePath[i] +File.separator+File.separator;
       }
        convertedPath = convertedPath.substring(0,convertedPath.length()-2);
        return "public class Convertidor { \n public static void main (String[] args) throws SQLException {\n" +
                "String path = \""+convertedPath+"\";\n" +
                "ODB odb = ODBFactory.open(path);\n";

    }

    private String getDatosFicheros(ArrayList<String> tablas,ClassConstants constants,ArrayList<ClassParams> classParams,String schema) throws FileNotFoundException, SQLException {
        File f1 = new File(constants.getPATH()+"/temp");
        String text = "";
        int aux = 1;
        int order = 1;
        String list []= bubbleSort(tablas);
        if(list.length>0) {
            for (int i = 0; i < tablas.size(); i++) {
                File f2 = new File(f1.getPath()+"\\"+list[i]+".txt");
                String tableName = tablas.get(i);
                int counter = 1;
                text+="ResultSet rs"+aux +" = new NeodatisQuerys().getResultSet(\""+tableName.toLowerCase()+"\","+"\""+schema+"\"); \n";
                text+="while(rs"+aux+".next()) {\n";
                BufferedReader buf = new BufferedReader(new FileReader(f2));
                ArrayList<String> types = new ArrayList<>();
                text += getMayus(tableName) +" " +tableName.toLowerCase() +" = new "+getMayus(tableName)+"();\n";
                try {
                    while (true) {
                        types.add(getMayus(buf.readLine()));
                    }
                } catch (Exception e) {

                }
                int objectOrder = 0;
                for(int x = 0; x < types.size();x++){
                    ArrayList<String> identificadores = new ArrayList<>();
                    if(!types.get(x).equalsIgnoreCase("String") && !types.get(x).equalsIgnoreCase("Int")
                        && !types.get(x).equalsIgnoreCase("float") && !types.get(x).equalsIgnoreCase("double")){
                        Connection con = new Conexion(null,schema).getConextion();
                        DatabaseMetaData metaData = con.getMetaData();
                        ResultSet foreignKeys = metaData.getImportedKeys(con.getCatalog(), null, tableName);
                        while (foreignKeys.next()) {
                            String columnName =  foreignKeys.getString("PKCOLUMN_NAME").toLowerCase();
                            identificadores.add(columnName);
                        }
                        foreignKeys.close();
                        ResultSet resultSet = con.createStatement().executeQuery("Select * from "+tableName.toLowerCase());
                        ResultSetMetaData metaDataRs = resultSet.getMetaData();
                        String typeName = metaDataRs.getColumnTypeName(counter);
                        resultSet.close();
                        con.close();
                        String object = types.get(x);
                        if(typeName.equalsIgnoreCase("VARCHAR") || typeName.equalsIgnoreCase("date")){
                            typeName = "String";
                        }
                        else if(typeName.equalsIgnoreCase("smallint")){
                            typeName = "int";
                        }
                        else if(typeName.equalsIgnoreCase("bigint")){
                            typeName = "float";
                        }
                        text += "IQuery query"+object+order+" = new CriteriaQuery("+object+".class,Where.equal(\""+
                                identificadores.get(objectOrder++).toLowerCase() + //El fallo esta aquí
                                "\", " +
                                "rs"+aux+".get"+getMayus(typeName.toLowerCase())+"("+counter+")));\n" +
                                "Objects<Object> objects"+object+order+" = odb.getObjects(query"+object+order+");\n" +
                                getMayus(object)+" "+ object.toLowerCase()+order + " = ("+getMayus(object)+") objects"+object+order+".next();\n" +
                                tableName.toLowerCase() + ".set"+getMayus(classParams.get(i).getValues().get(x))+"("+object.toLowerCase()+order+");\n";
                        order++;
                        counter++;
                    }
                    else{
                        text+= tableName.toLowerCase() + ".set"+getMayus(classParams.get(i).getValues().get(x))+"(rs"+aux+".get"+getMayus(
                                types.get(x))+"("+counter++ +"));\n";
                    }
                }
                text+="odb.store("+tableName.toLowerCase()+");\n}\n";
                text+="rs"+aux+".close();\n";
                aux++;
            }
        }
        return text;
    }

    private String[] bubbleSort(ArrayList<String> tablas) {
            String [] list = tablas.toArray(new String[tablas.size()]);
            return list;
    }


    private static String getMayus(String word){
        return word.toUpperCase().charAt(0) + word.substring(1);
    }

}
