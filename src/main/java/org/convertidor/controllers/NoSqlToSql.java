package org.convertidor.controllers;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.convertidor.conexion.Conexion;
import org.convertidor.model.TableTypesClass;
import javax.swing.*;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Clase que contiene métodos para migrar datos de una base de datos NoSQL a una base de datos SQL.
 *
 *   @Author Ismael Orellana Bello
 *   @Date 12/06/2023
 *   @Version 1.0
 */

public class NoSqlToSql {

    //ArrayList de TableTypeClass con las propiedades de la tabla
    private ArrayList<TableTypesClass> tableProperties = new ArrayList<>();
    //ArrayList de nombres
    private ArrayList<String> names = new ArrayList<>();
    //ArrayList de tipos
    private ArrayList<String> types = new ArrayList<>();
    //ArrayList de objetos a crear
    private ArrayList<String> objects = new ArrayList<>();
    //ArrayList de nombre de tablas
    private  ArrayList<String> tableNames = new ArrayList<>();

    /**
     * Genera una conversión a SQL a partir de los documentos de una colección de MongoDB.
     * El archivo JSON se guarda en la ubicación especificada.
     * Además, se invoca al método generateTables() para generar las tablas correspondientes.
     *
     * @throws IOException  Si ocurre un error de lectura o escritura en el archivo.
     * @throws SQLException Si ocurre un error al obtener la conexión a la base de datos.
     */
    public void generateDataBase(String fileName) throws IOException, SQLException {
        deleteFiles();
        Conexion conexion = new Conexion(null, fileName);
        //Obtener colección seleccionada
        MongoCollection<Document> collection = conexion.getCollection();
        Document document = collection.find().first();
        String jsonName = collection.getNamespace().getCollectionName();
        //Crear TXT para acceder a sus datos
        File f1 = new File("src/main/java/org/convertidor/model/json/"+jsonName+".txt");
        f1.createNewFile();
        BufferedWriter bufIn = new BufferedWriter(new FileWriter(f1));
        String json = "";
        if (document != null) {
            //Generar JSON
            JsonWriterSettings settings = JsonWriterSettings.builder().indent(true).build();
            json = document.toJson(settings);
        } else {
            JOptionPane.showMessageDialog(null,"No se ha encontrado la colección seleccionada");
        }
        bufIn.write(json);
        bufIn.close();
        generateTables(jsonName,f1,collection);
    }

    /**
     * Método que borra los archivos temporales utilizados previamente
     */
    private void deleteFiles() {
        File f1 = new File("src/main/java/org/convertidor/model/json");
        if(f1.isDirectory()) {
            File f1List[] = f1.listFiles();
            if (f1List.length > 0) {
                for (int i = 0; i < f1List.length; i++) {
                   f1List[i].delete();
                }
            }
        }
    }

    /**
     * Genera las tablas correspondientes en la base de datos a partir del archivo JSON y la colección de MongoDB.
     * Se extraen los nombres y tipos de las columnas de las propiedades del archivo JSON y se crean las tablas en la base de datos.
     * También se crean tablas para los objetos anidados en el archivo JSON.
     *
     * @param jsonName    El nombre del archivo JSON y de la tabla principal a crear.
     * @param f1          El archivo JSON que contiene los datos.
     * @param collection  La colección de MongoDB que se utilizó para generar el archivo JSON.
     * @throws IOException  Si ocurre un error de lectura o escritura en el archivo JSON.
     * @throws SQLException Si ocurre un error al obtener la conexión a la base de datos.
     */
    private void generateTables(String jsonName,File f1,MongoCollection<Document> collection) throws IOException, SQLException {
        //Crear conexión
        Conexion conexion = new Conexion();
        conexion.getConextion();
        conexion.setSchema(jsonName);
        conexion.createSchema();
        conexion.closeConnection();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(f1));
        boolean object = false;
        String objectText = "";
        int counter = 0;
        try{
            while(true){
                //Lectura de todas las líneas del JSON
               String line = bufferedReader.readLine();
               if(counter<4){
                   counter++;
                   continue;
               }
               else{
                   if(!line.contains("[") && !line.contains("{") && !line.contains("]") && !object){
                       //Añadir nombre y tipo que no sea ni objeto ni Array
                      String split[] =  line.split(":"); //Formateo de datos
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
                           //Añadir parámetros de tipo objeto
                           object = true;
                           String split[] = line.split(":"); //Formateo de datos
                           names.add(split[0].replaceAll("\"","").trim() + "id");
                            tableNames.add(split[0].replaceAll("\"","").trim());
                           types.add("{");
                       }
                       else if(object){
                           //Añadir líneas para posterioremente sacar los datos
                           objectText+=line;
                           if(line.contains("}")){
                               objects.add(objectText);
                               object = false;
                               objectText="";
                           }
                       }
                       else if(line.contains("[") && !object) {
                           //Añadir tipos Array
                           String split[] = line.split(":");
                           String split2[] = split[0].split("\"");
                           names.add(split2[1] + "id"); //Formateo de datos
                           String nextLine = bufferedReader.readLine();
                           String type = "";
                           //Añadir tipo
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
    /**
     * Crea las tablas correspondientes a los objetos anidados en el archivo JSON.
     *
     * @param objects      Lista de cadenas que representan los objetos anidados en el archivo JSON.
     * @param connection   Conexión a la base de datos.
     * @param schema       Nombre del esquema de la base de datos.
     * @param collection   Colección de MongoDB que se utilizó para generar el archivo JSON.
     * @param tableNames   Lista de nombres de las tablas a crear.
     * @throws IOException     Si ocurre un error de lectura o escritura en el archivo JSON.
     * @throws SQLException    Si ocurre un error al ejecutar consultas SQL.
     */
    private void createObjectTables(ArrayList<String> objects,Connection connection,String schema,MongoCollection<Document> collection,ArrayList<String> tableNames) throws IOException, SQLException {
       boolean condition = false;
       //Recorrer todos los objetos que se encuentran en el JSON
        for(int i = 0 ; i < objects.size();i++) {
            try {
                String line = objects.get(i);
                ArrayList<String> names = new ArrayList<>();
                ArrayList<String> types = new ArrayList<>();
                if (line.contains("}") && !line.contains("{") && !line.contains("[") ) {
                    //Saber si es un objeto normal o un objeto con arrays
                    String splitParameters[] = line.split(",");
                    for (int x = 0; x < splitParameters.length; x++) {
                        if (splitParameters[x].contains("}")) {
                            //Formateo de datos
                            splitParameters[x] = splitParameters[x].replaceAll("}", "");
                        }
                        String split2[] = splitParameters[x].split(":");
                        split2[0] = split2[0].replaceAll("\"", "");
                        names.add(split2[0].trim());
                        //Añadir el tipo de dato
                        if (split2[1].contains("\"")) {
                            types.add("VARCHAR(2000)");
                        } else if (split2[1].contains(".")) {
                            types.add("Double");
                        } else {
                            types.add("Int");
                        }
                    }
                    //Formateo de datos
                    if(tableNames.get(i).contains(" ")){
                        tableNames.set(i,tableNames.get(i).replaceAll(" ","_"));
                        condition = true;
                    }
                    //Creación de la query para crear la tabla y para insertar los datos posteriormente
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
                    st.executeUpdate(query);
                    st.close();
                    int primaryKey = 1;
                    //Cursor para acceder a todos los objetos de la base ded atos
                    MongoCursor<Document> cursor = collection.find().iterator();
                    int fk = 1;
                        while (cursor.hasNext()) {
                            try {
                                Document documentoPrincipal = cursor.next();
                                ArrayList<Document> array = (ArrayList<Document>) documentoPrincipal.getList(tableNames.get(i), Document.class);
                                for (Document documento : array) {
                                    //Preparar los datos para la sentencia
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
                }
            } catch (Exception e) {

            }
            condition = false;
        }
    }
    /**
     * Inserta los valores en una tabla específica en la base de datos.
     *
     * @param tableName    Nombre de la tabla en la que se insertarán los valores.
     * @param names        Lista de nombres de las columnas en la tabla.
     * @param types        Lista de tipos de datos de las columnas en la tabla.
     * @param connection   Conexión a la base de datos.
     * @param collection   Colección de MongoDB que se utilizó para generar el archivo JSON.
     * @param condition    Condición para determinar si se debe realizar una modificación en el nombre de la tabla.
     * @throws SQLException    Si ocurre un error al ejecutar consultas SQL.
     */
    private void insertValues(String tableName, ArrayList<String> names, ArrayList<String> types, Connection connection,
                              MongoCollection<Document> collection,boolean condition) throws SQLException {
        //Preparar sentencias de inserción
        String query = "Insert into "+tableName+" values(?,";
        for(int i = 0; i <names.size(); i++){
            query+="?,";
        }
        query+="?)";
        int primaryKey = 1;
        //Recorrer todos los objetos
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
            //Saber el tipo de dato
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
    /**
     * Crea tablas adicionales en la base de datos según las propiedades de los objetos de tipo TableTypesClass.
     *
     * @param tableTypesClass   Objeto TableTypesClass que contiene las propiedades de las tablas adicionales.
     * @param connection        Conexión a la base de datos.
     * @param schema            Esquema de la base de datos.
     * @param collection        Colección de MongoDB que se utilizó para generar el archivo JSON.
     * @throws SQLException    Si ocurre un error al ejecutar consultas SQL.
     */
    private void createOtherTables(TableTypesClass tableTypesClass, Connection connection,String schema,MongoCollection<Document> collection) throws SQLException {
        //Recorrer todos los tipos de datos que son Arrays
        for (int i = 0; i < tableTypesClass.getDataType().size(); i++) {
            boolean condition = false;
            //Saber el tipo de dato
            if (!tableTypesClass.getDataType().get(i).equalsIgnoreCase("[{") &&
                    tableTypesClass.getDataType().get(i).contains("[") &&
                    !tableTypesClass.getDataType().get(i).contains("{")) {
                if (tableTypesClass.getName().get(i).contains(" ")) {
                    String aux = tableTypesClass.getName().get(i).replaceAll(" ", "_");
                    condition = true;
                    tableTypesClass.getName().set(i, aux);
                }
                String tableName = tableTypesClass.getName().get(i).replaceAll("id", "");
                String type = "";
                if (tableTypesClass.getDataType().get(i).equalsIgnoreCase("[S")) {
                    type = "VARCHAR(255)";
                } else if (tableTypesClass.getDataType().get(i).equalsIgnoreCase("boolean")) {
                    type = "BOOLEAN";
                } else {
                    type = "INT";
                }
                //Preparar Querys para la inserción de los datos y la creación de las tablas
                Statement st = connection.createStatement();
                String query = "Create table  if not exists " + tableName + " ( " +
                        tableTypesClass.getName().get(i) + " INT AUTO_INCREMENT PRIMARY KEY," +
                        tableName + " " + type + ", " + schema + "id INT, FOREIGN KEY (" + schema + "id) REFERENCES " +
                        schema + "(id));";
                st.executeUpdate(query);
                st.close();
                FindIterable<Document> documents = collection.find();
                MongoCursor<Document> cursor = documents.iterator();
                int counter = 1;
                int pk = 1;
                //Recorrer todos los Arrays de la base de datos
                while (cursor.hasNext()) {
                    try {
                        Document document = cursor.next();
                        ArrayList<?> values = null;
                        //Saber que tipo de dato es
                        String preparedQuery = "Insert into " + tableName + " values (?,?,?)";
                        if (condition) {
                            tableName = tableName.replaceAll("_", " ");
                        }
                        if (type.equalsIgnoreCase("INT")) {
                            values = (ArrayList<Integer>) document.get(tableName);
                        } else {
                            values = (ArrayList<String>) document.get(tableName);
                        }
                        //Insertar valores
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
                        if (condition) {
                            tableName = tableName.replaceAll(" ", "_");
                        }
                    } catch (Exception e) {

                    }
                }
            }
            }
        }
    /**
     * Crea la primera tabla en la base de datos según las propiedades de los objetos de tipo TableTypesClass.
     *
     * @param tableTypesClass   Objeto TableTypesClass que contiene las propiedades de la tabla principal.
     * @param connection        Conexión a la base de datos.
     * @param collection        Colección de MongoDB que se utilizó para generar el archivo JSON.
     * @param schema            Esquema de la base de datos.
     * @throws SQLException    Si ocurre un error al ejecutar consultas SQL.
     */
            private void createFirstTable (TableTypesClass tableTypesClass, Connection connection,MongoCollection<Document> collection,String schema) throws SQLException {
                //preparar sentencia
                String query = "Create table if not exists " + schema + " (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY,";
                for (int i = 0; i < tableTypesClass.getDataType().size(); i++) {
                    String type = "";
                    String name = "";
                    //Saber el tipo de dato
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
                st.executeUpdate(query);
                st.close();
                FindIterable<Document> documents = collection.find();
                MongoCursor<Document> cursor = documents.iterator();
                int pk = 1;
                //Recorrer toda la base de datos mongoDB
                while (cursor.hasNext()) {
                    try {
                        String queryInsert = "Insert into " + schema + " values(";
                        for (int i = 0; i < tableTypesClass.getDataType().size(); i++) {
                            if (tableTypesClass.getDataType().get(i).contains("[") || tableTypesClass.getDataType().get(i).contains("{")) {
                                continue;
                            } else {
                                queryInsert += "?";
                                queryInsert += ",";
                            }
                        }
                        queryInsert += "?);";
                        Document document = cursor.next();
                        PreparedStatement psm = connection.prepareStatement(queryInsert);
                        psm.setInt(1, pk);
                        int counter = 2;
                        //Recorrer todos los tipos de dato y formatearlos
                        for (int i = 0; i < tableTypesClass.getName().size(); i++) {
                            if (tableTypesClass.getName().get(i).contains("|")) {
                                tableTypesClass.getName().set(i, tableTypesClass.getName().get(i).replaceAll("_", " ").
                                        substring(0, tableTypesClass.getName().get(i).length() - 1));

                            }
                            if (tableTypesClass.getDataType().get(i).equalsIgnoreCase("VARCHAR")) {
                                psm.setString(counter, document.getString(tableTypesClass.getName().get(i)));
                                counter++;
                            } else if (tableTypesClass.getDataType().get(i).contains("{") ||
                                    tableTypesClass.getDataType().get(i).contains("[")) {
                                continue;
                            } else if (tableTypesClass.getDataType().get(i).equalsIgnoreCase("boolean")) {
                                psm.setBoolean(counter, document.getBoolean(tableTypesClass.getName().get(i)));
                                counter++;
                            } else {
                                psm.setInt(counter, document.getInteger(tableTypesClass.getName().get(i)));
                                counter++;
                            }
                        }
                        psm.executeUpdate();
                        pk++;
                    } catch (Exception e) {

                    }
                }
            }
        }


