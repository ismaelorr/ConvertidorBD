package org.convertidor.controllers;

import com.mongodb.client.MongoCollection;
import lombok.NoArgsConstructor;
import org.bson.Document;
import org.convertidor.conexion.Conexion;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Clase que proporciona funcionalidades para convertir datos de una base de datos SQL
 * a una base de datos NoSQL.
 *
 *    @Author Ismael Orellana Bello
 *    @Date 12/06/2023
 *    @Version 1.0
 *
 */

@NoArgsConstructor
public class SqlToNoSql {


    /**
     * Inicia el proceso de conversión de datos de la base de datos SQL a MongoDB.
     *
     * @throws SQLException Si ocurre un error al interactuar con la base de datos SQL.
     */
    public void startConversion(String fileName) throws SQLException {
        String schema = "";
        //Importar o obtener schema
        if(fileName.length() == 0){
           schema = importSql();
        }else{
            schema = fileName;
        }
        //Crear la conexión
        Conexion con = new Conexion();
        con.setSchema(schema);
        Connection connection = con.getConextion();
        DatabaseMetaData metadata = connection.getMetaData();
        String[] tipos = {"TABLE"};
        ResultSet result = metadata.getTables(connection.getCatalog(), connection.getSchema(), "%", tipos);
        ArrayList<String> tablas = new ArrayList<>();
        //Obtener nombres de las tablas de la base de datos
        while (result.next()) {
            tablas.add(result.getString("TABLE_NAME"));
        }
        MongoCollection <Document> collection = con.getCollection();
        ArrayList <Document> docs = new ArrayList<>();
        Document mainDocument = new Document();
        //Recorrer todas las tablas
        for(int x = 0; x<tablas.size();x++) {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("Select * from " + tablas.get(x));
            ArrayList<Document> registros = new ArrayList<>();
            //Obtener todos los registros de la base de datos
            while (rs.next()) {
                //Crear documento principal
                Document document = new Document();
                //Saber el tipo de dato
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    String type = rs.getMetaData().getColumnTypeName(i);
                    if (type.equals("VARCHAR") || type.equalsIgnoreCase("Text")) {
                        document.append(rs.getMetaData().getColumnName(i),rs.getString(i));
                    } else if (type.equals("INT") || type.equalsIgnoreCase("smallint") ||
                            type.equalsIgnoreCase("bigint")) {
                       document.append(rs.getMetaData().getColumnName(i),rs.getInt(i));
                    } else if (type.equals("FLOAT")) {
                        document.append(rs.getMetaData().getColumnName(i),rs.getFloat(i));
                    } else if (type.equals("DATE")) {
                        document.append(rs.getMetaData().getColumnName(i),rs.getDate(i));
                    }
                    else if(type.equalsIgnoreCase("decimal")){
                        document.append(rs.getMetaData().getColumnName(i),rs.getBigDecimal(i));
                    }
                    else if(type.equals("TIME")){
                        Time time = rs.getTime(i);
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        String formattedTime = sdf.format(time);
                        document.append(rs.getMetaData().getColumnName(i),formattedTime);
                    }
                }
                registros.add(document);
            }
            st.close();
            rs.close();
            //Añadir a la base de datos
            mainDocument.append(tablas.get(x),registros);
        }
        try {
            collection.insertOne(mainDocument);
        }catch (Exception e){

        }
    }

    /**
     * Importa el archivo SQL en la base de datos y devuelve el esquema utilizado.
     *
     * @return El esquema utilizado para importar el archivo SQL.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     */
    private String importSql() throws SQLException {
        Conexion con = new Conexion();
        Connection connection = con.getConextion();
        con.importSQL();
        String schema = con.getSchema();
        connection.close();
        return schema;
    }

}
