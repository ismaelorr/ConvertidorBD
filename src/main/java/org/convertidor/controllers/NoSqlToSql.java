package org.convertidor.controllers;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.convertidor.conexion.Conexion;
import org.convertidor.model.TableTypesClass;

import javax.print.Doc;
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
        ArrayList<String> tableNames = new ArrayList<>();
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
                   if(!line.contains("[") && !line.contains("{") && !line.contains("]") && !object){
                      String split[] =  line.split(":");
                      if(split.length > 1){
                          String split2 [] = split[0].split("\"");
                          if(split2[1].contains(" ")){
                              split2[1] = split2[1].replaceAll(" ","_")+"|";
                          }
                          names.add(split2[1]);
                          String split3 [] = split[1].split("\"");
                          if(split3.length>1){
                              types.add("VARCHAR");
                          }
                          else if (line.contains("true") || line.contains("false")){
                              types.add("boolean");
                          }
                          else{
                                types.add("int");
                          }
                      }
                      }
                   else{
                       if(line.contains("{") && !object){
                           object = true;
                           String split[] = line.split(":");
                           names.add(split[0].replaceAll("\"","").trim() + "id");
                            tableNames.add(split[0].replaceAll("\"","").trim());
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
                               tableNames.add(split2[1]);
                               object = true;
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
        Connection connection = new Conexion(null,conexion.getSchema()).getConextion();
        createFirstTable(tableProperties.get(0),connection,collection,conexion.getSchema());
        createOtherTables(tableProperties.get(0),connection,conexion.getSchema(),collection);
        createObjectTables(objects,connection,conexion.getSchema(),collection,tableNames);

    }

    private void createObjectTables(ArrayList<String> objects,Connection connection,String schema,MongoCollection<Document> collection,ArrayList<String> tableNames) throws IOException, SQLException {
       boolean condition = false;
        for(int i = 0 ; i < objects.size();i++) {
            try {
                String line = objects.get(i);
                System.out.println(line);
                ArrayList<String> names = new ArrayList<>();
                ArrayList<String> types = new ArrayList<>();
                if (line.contains("}") && !line.contains("{") && !line.contains("[") ) {
                    String splitParameters[] = line.split(",");
                    for (int x = 0; x < splitParameters.length; x++) {
                        if (splitParameters[x].contains("}")) {
                            splitParameters[x] = splitParameters[x].replaceAll("}", "");
                        }
                        String split2[] = splitParameters[x].split(":");
                        split2[0] = split2[0].replaceAll("\"", "");
                        names.add(split2[0].trim());
                        if (split2[1].contains("\"")) {
                            types.add("VARCHAR(2000)");
                        } else if (split2[1].contains(".")) {
                            types.add("Double");
                        } else {
                            types.add("Int");
                        }
                        System.out.println(splitParameters[x]);
                    }
                    if(tableNames.get(i).contains(" ")){
                        tableNames.set(i,tableNames.get(i).replaceAll(" ","_"));
                        condition = true;
                    }
                    String query = "Create table if not exists " + tableNames.get(i) + " ( " +
                            tableNames.get(i) + "id INT AUTO_INCREMENT PRIMARY KEY,";
                    String preparedQuery = "Insert into " + tableNames.get(i) + " values(?,";
                    for (int x = 0; x < names.size(); x++) {
                        query += names.get(x) + " " + types.get(x) + ",";
                        preparedQuery += "?,";
                    }
                    query += " " + schema + "id INT, FOREIGN KEY (" + schema + "id) REFERENCES " + schema + "(id));";
                    preparedQuery += "?)";
                    Statement st = connection.createStatement();
                    System.out.println(query);
                    System.out.println(preparedQuery);
                    st.executeUpdate(query);
                    st.close();
                    int primaryKey = 1;
                    MongoCursor<Document> cursor = collection.find().iterator();
                    int fk = 1;
                        while (cursor.hasNext()) {
                            try {
                                Document documentoPrincipal = cursor.next();
                                ArrayList<Document> array = (ArrayList<Document>) documentoPrincipal.getList(tableNames.get(i), Document.class);
                                for (Document documento : array) {
                                    PreparedStatement psm = connection.prepareStatement(preparedQuery);
                                    int counter = 1;
                                    psm.setInt(counter++, primaryKey++);
                                    for (int x = 0; x < names.size(); x++) {
                                        if (types.get(x).equalsIgnoreCase("INT")) {
                                            try {
                                                psm.setInt(counter, documento.getInteger(names.get(x)));
                                                counter++;
                                            } catch (Exception e) {
                                                psm.setDouble(counter++, documento.getDouble(names.get(x)));
                                            }
                                        } else {
                                            psm.setString(counter++, documento.getString(names.get(x)));
                                        }
                                    }
                                    psm.setInt(counter, fk);
                                    psm.executeUpdate();
                                    psm.close();
                                }
                                fk++;
                            }catch (Exception ex){
                                insertValues(tableNames.get(i),names,types,connection,collection,condition);
                            }
                        }
                        cursor.close();
                } else {
                   /** System.out.println(line);
                    String splitLines [] = line.split("\"");
                    System.out.println(splitLines[1]);
                    ArrayList<String> values = new ArrayList<>();
                    values.add(splitLines[1]);
                    Statement st = connection.createStatement();
                    String query = "Create table if not exists " + tableNames.get(i) + " ( " +
                            tableNames.get(i) + "id INT AUTO_INCREMENT PRIMARY KEY,"+values.get(0)+" VARCHAR(2000),";
                    String preparedQuery = "Insert into "+tableNames.get(i)+" values(?,?";
                    for(int x = 0; x < splitLines.length; x++) {
                        System.out.println(splitLines[x]);
                        if (splitLines[x].contains("],")) {
                            values.add(splitLines[x + 1]);
                            query+=splitLines[x+1]+" VARCHAR(2000),";
                            preparedQuery += "?,";
                        }
                    }
                    query = query.substring(0,query.length()-1);
                    preparedQuery+="?)";
                    query += " " + schema + "id INT, FOREIGN KEY (" + schema + "id) REFERENCES " + schema + "(id));";
                    st.executeUpdate(query);
                    FindIterable<Document> documents = collection.find();
                    MongoCursor<Document> cursor = documents.iterator();
                    System.out.println(query);
                    while (cursor.hasNext()) {
                        for (int x = 0; x < values.size(); x++) {
                            PreparedStatement psm = connection.prepareStatement(preparedQuery);
                            Document document = cursor.next();
                            ArrayList<Object> array = (ArrayList<Object>) document.getList(values.get(x), Object.class);

                        }
                    }
                    cursor.close();
                }
**/
                }
            } catch (Exception e) {

            }
            condition = false;
        }
    }

    private void insertValues(String tableName, ArrayList<String> names, ArrayList<String> types, Connection connection,
                              MongoCollection<Document> collection,boolean condition) throws SQLException {

        String query = "Insert into "+tableName+" values(?,";
        for(int i = 0; i <names.size(); i++){
            query+="?,";
        }
        query+="?)";
        int primaryKey = 1;
        MongoCursor<Document> cursor = collection.find().iterator();
        int fk = 1;
        if(condition){
            tableName = tableName.replaceAll("_"," ");
        }
        while(cursor.hasNext()) {
            Document documentoPrincipal = cursor.next();
            Document auxDocument = documentoPrincipal.get(tableName, Document.class);
            PreparedStatement psm = connection.prepareStatement(query);
            int counter = 1;
            psm.setInt(counter++, primaryKey++);
            for (int x = 0; x < names.size(); x++) {
                if (types.get(x).equalsIgnoreCase("INT")) {
                    try {
                        psm.setInt(counter, auxDocument.getInteger(names.get(x)));
                        counter++;
                    } catch (Exception e) {
                        psm.setDouble(counter++, auxDocument.getDouble(names.get(x)));
                    }
                } else {
                    psm.setString(counter++, auxDocument.getString(names.get(x)));
                }
            }
            psm.setInt(counter, fk);
            psm.executeUpdate();
            psm.close();
            fk++;
        }
    }

    private void createOtherTables(TableTypesClass tableTypesClass, Connection connection,String schema,MongoCollection<Document> collection) throws SQLException {
        for (int i = 0; i < tableTypesClass.getDataType().size(); i++) {
            boolean condition = false;
            if (!tableTypesClass.getDataType().get(i).equalsIgnoreCase("[{") &&
                    tableTypesClass.getDataType().get(i).contains("[") &&
                    !tableTypesClass.getDataType().get(i).contains("{")) {
                if(tableTypesClass.getName().get(i).contains(" ")){
                    String aux = tableTypesClass.getName().get(i).replaceAll(" ","_");
                    condition = true;
                    tableTypesClass.getName().set(i,aux);
                }
                String tableName = tableTypesClass.getName().get(i).replaceAll("id", "");
                String type = "";
                if (tableTypesClass.getDataType().get(i).equalsIgnoreCase("[S")) {
                    type = "VARCHAR(255)";
                } else if(tableTypesClass.getDataType().get(i).equalsIgnoreCase("boolean")){
                    type = "BOOLEAN";
                }
                else{
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
                    String preparedQuery = "Insert into " + tableName + " values (?,?,?)";
                    System.out.println(preparedQuery);
                    if(condition){
                        tableName = tableName.replaceAll("_"," ");
                    }
                    if (type.equalsIgnoreCase("INT")) {
                        values = (ArrayList<Integer>) document.get(tableName);
                    } else {
                        values = (ArrayList<String>) document.get(tableName);
                    }
                    for (int x = 0; x < values.size(); x++) {
                            PreparedStatement psm = connection.prepareStatement(preparedQuery);
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
                    if(condition){
                        tableName = tableName.replaceAll(" ","_");
                    }
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
                    if(tableTypesClass.getName().get(i).contains("|")){
                      String tableTypename = tableTypesClass.getName().get(i).substring(0,tableTypesClass.getName().get(i).length()-1);
                        query+=tableTypename+" "+type+",";
                    }
                    else{
                        query+= tableTypesClass.getName().get(i)+" "+type+",";
                    }
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
                        if(tableTypesClass.getName().get(i).contains("|")){
                            tableTypesClass.getName().set(i,tableTypesClass.getName().get(i).replaceAll("_"," ").
                                    substring(0,tableTypesClass.getName().get(i).length()-1));

                        }
                        if(tableTypesClass.getDataType().get(i).equalsIgnoreCase("VARCHAR")){
                            psm.setString(counter, document.getString(tableTypesClass.getName().get(i)));
                            counter++;
                        }
                        else if(tableTypesClass.getDataType().get(i).contains("{") ||
                        tableTypesClass.getDataType().get(i).contains("[")){
                            continue;
                        }
                        else if(tableTypesClass.getDataType().get(i).equalsIgnoreCase("boolean")){
                            psm.setBoolean(counter,document.getBoolean(tableTypesClass.getName().get(i)));
                            counter++;
                        }
                        else{
                            psm.setInt(counter,document.getInteger(tableTypesClass.getName().get(i)));
                            counter++;
                        }
                        System.out.println(tableTypesClass.getName().get(i));
                    }
                    psm.executeUpdate();
                    pk++;
                }
            }
        }


