package org.convertidor.controllers;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriter;
import org.bson.json.JsonWriterSettings;
import org.convertidor.conexion.Conexion;
import org.convertidor.model.TableTypesClass;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class NoSqlToSql {

    public void generateDataBase() throws IOException, SQLException {
        Conexion conexion = new Conexion(null, "recetas");
        MongoCollection<Document> collection = conexion.getCollection();
        FindIterable<Document> documents = collection.find();
        Document document = collection.find().first();
        String jsonName = collection.getNamespace().getCollectionName();
        File f1 = new File("src/main/java/org/convertidor/model/json/"+jsonName+".txt");
        f1.createNewFile();
        BufferedWriter bufIn = new BufferedWriter(new FileWriter(f1));
        String json = "";
        if (document != null) {
            JsonWriterSettings settings = JsonWriterSettings.builder().indent(true).build();
            json = document.toJson(settings);
        } else {
            System.out.println("No se encontraron documentos en la colección.");
        }
        System.out.println(json);
        bufIn.write(json);
        bufIn.close();
        generateTables(jsonName,f1,collection);
    }

    private void generateTables(String jsonName,File f1,MongoCollection<Document> collection) throws IOException, SQLException {
        Conexion conexion = new Conexion();
        conexion.getConextion();
        conexion.setSchema(jsonName);
        conexion.createSchema();
        conexion.closeConnection();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(f1));
        ArrayList<TableTypesClass> tableProperties = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> types = new ArrayList<>();
        ArrayList<String> objects = new ArrayList<>();
        boolean object = false;
        String objectText = "";
        int counter = 0;
        try{
            while(true){
               String line = bufferedReader.readLine();
                System.out.println(line);
               if(counter<4){
                   counter++;
                   continue;
               }
               else{
                   if(!line.contains("[") && !line.contains("{") && !line.contains("]")){
                      String split[] =  line.split(":");
                      if(split.length > 1){
                          String split2 [] = split[0].split("\"");
                          names.add(split2[1]);
                          String split3 [] = split[0].split("\"");
                          if(split3.length>1){
                              types.add("VARCHAR");
                          }
                          else{
                              types.add("int");
                          }
                      }
                      }
                   else{
                       if(line.contains("{")){
                           object = true;
                           String split[] = line.split(":");
                           names.add(split[0].replaceAll("\"","").trim() + "id");
                           types.add("{");
                       }
                       else if(object){
                           objectText+=line;
                           if(line.contains("}")){
                               objects.add(objectText);
                               object = false;
                               objectText="";
                           }
                       }
                       else if(line.contains("[") && !object) {
                           String split[] = line.split(":");
                           String split2[] = split[0].split("\"");
                           names.add(split2[1] + "id");
                           String nextLine = bufferedReader.readLine();
                           String type = "";
                           if(nextLine.contains("{")){
                               type = "[{";
                           }
                           else if(nextLine.contains("\"")){
                               type="[S";
                           }
                           else{
                               type="[I";
                           }
                           types.add(type);
                       }

                   }
               }
               }
            } catch (Exception e){

        }
        tableProperties.add(new TableTypesClass(names,types));
        for(int i = 0; i < names.size(); i++){
            System.out.println("Nombre: "+names.get(i)+ " tipo: "+types.get(i));
        }
        Connection connection = new Conexion(null,conexion.getSchema()).getConextion();
        createFirstTable(tableProperties.get(0),connection,collection,conexion.getSchema());
        createObjectTables(objects,f1);
        createOtherTables(tableProperties.get(0),connection,conexion.getSchema(),collection);

    }

    private void createObjectTables(ArrayList<String> objects,File f1) throws IOException {
        for(int i = 0 ; i < objects.size();i++){
            File f2 = new File(f1.getAbsolutePath()+"/auxiliar"+i+".txt");
            f2.createNewFile();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(f2));
            bufferedWriter.write(objects.get(i));
            bufferedWriter.close();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(f2));
            try{
                while(true){
                    String line = bufferedReader.readLine();
                    if(line.contains(":")){

                    }
                }
            }catch (Exception e){

            }
        }
    }

    private void createOtherTables(TableTypesClass tableTypesClass, Connection connection,String schema,MongoCollection<Document> collection) throws SQLException {
        for (int i = 0; i < tableTypesClass.getDataType().size(); i++) {
            if (!tableTypesClass.getDataType().get(i).equalsIgnoreCase("[{") &&
                    tableTypesClass.getDataType().get(i).contains("[")) {
                String tableName = tableTypesClass.getName().get(i).replaceAll("id", "");
                String type = "";
                if (tableTypesClass.getDataType().get(i).equalsIgnoreCase("[S")) {
                    type = "VARCHAR(255)";
                } else {
                    type = "INT";
                }
                Statement st = connection.createStatement();
                String query = "Create table  if not exists " + tableName + " ( " +
                        tableTypesClass.getName().get(i) + " INT AUTO_INCREMENT PRIMARY KEY," +
                        tableName + " " + type + ", " + schema + "id INT, FOREIGN KEY ("+ schema+"id) REFERENCES "+
                        schema+"(id));";
                System.out.println(query);
                st.executeUpdate(query);
                st.close();
                FindIterable<Document> documents = collection.find();
                MongoCursor<Document> cursor = documents.iterator();
                int counter = 1;
                int pk = 1;
                while (cursor.hasNext()) {
                    Document document = cursor.next();
                    ArrayList<?> values = null;
                    if (type.equalsIgnoreCase("INT")) {
                        values = (ArrayList<Integer>) document.get(tableName);
                    } else {
                        values = (ArrayList<String>) document.get(tableName);
                    }
                    for (int x = 0; x < values.size(); x++) {
                            PreparedStatement psm = connection.prepareStatement("Insert into " + tableName + " values (?,?,?)");
                            psm.setInt(1, pk++);
                            if (type.equalsIgnoreCase("INT")) {
                                psm.setInt(2, (Integer) values.get(x));
                            } else {
                                psm.setString(2, (String) values.get(x));
                            }
                            psm.setInt(3, counter);
                            psm.executeUpdate();
                        }
                        counter++;
                    }
                }
            }
        }


            private void createFirstTable (TableTypesClass tableTypesClass, Connection connection,MongoCollection<Document> collection,String schema) throws SQLException {
                String query = "Create table if not exists " + schema + " (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY,";
                for (int i = 0; i < tableTypesClass.getDataType().size(); i++) {
                    String type = "";
                    String name = "";
                    if(tableTypesClass.getDataType().get(i).contains("[") ||
                            tableTypesClass.getDataType().get(i).contains("{")){
                        continue;
                    }
                    else if(tableTypesClass.getDataType().get(i).equalsIgnoreCase("VARCHAR")){
                       type= "VARCHAR(2000)";
                    }
                    else{
                        type = tableTypesClass.getDataType().get(i);
                    }
                    query+= tableTypesClass.getName().get(i)+" "+type;
                    query+=",";
                }
                query=query.substring(0,query.length()-1)+" );";
                Statement st = connection.createStatement();
                System.out.println(query);
                st.executeUpdate(query);
                st.close();
                FindIterable<Document> documents = collection.find();
                MongoCursor<Document> cursor = documents.iterator();
                int pk = 1;
                while (cursor.hasNext()) {
                    String queryInsert = "Insert into "+schema+" values(";
                    for(int i = 0; i < tableTypesClass.getDataType().size(); i++){
                        if(tableTypesClass.getDataType().get(i).contains("[") || tableTypesClass.getDataType().get(i).contains("{")){
                            continue;
                        }else{
                            queryInsert+="?";
                                queryInsert+=",";
                        }
                    }
                    queryInsert+="?);";
                    System.out.println(queryInsert);
                    Document document = cursor.next();
                    PreparedStatement psm = connection.prepareStatement(queryInsert);
                    psm.setInt(1,pk);
                    int counter = 2;
                    for(int i = 0; i < tableTypesClass.getName().size(); i++){
                        if(tableTypesClass.getDataType().get(i).equalsIgnoreCase("VARCHAR")){
                            psm.setString(counter, document.getString(tableTypesClass.getName().get(i)));
                            counter++;
                        }
                        else if(tableTypesClass.getDataType().get(i).contains("{") ||
                        tableTypesClass.getDataType().get(i).contains("[")){
                            continue;
                        }
                        else {
                            psm.setInt(counter,document.getInteger(document.get(tableTypesClass.getName().get(i))));
                            counter++;
                        }
                        System.out.println(tableTypesClass.getName().get(i));
                    }
                    psm.executeUpdate();
                    pk++;
                }
            }
        }


