package org.convertidor.controllers;

import com.mongodb.client.MongoCollection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bson.Document;
import org.convertidor.conexion.Conexion;

import javax.print.Doc;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;


@NoArgsConstructor
public class SqlToNoSql {


    public void startConversion() throws SQLException {
        String schema = importSql();
        Conexion con = new Conexion();
        con.setSchema(schema);
        Connection connection = con.getConextion();
        System.out.println(schema);
        DatabaseMetaData metadata = connection.getMetaData();
        String[] tipos = {"TABLE"};
        ResultSet result = metadata.getTables(connection.getCatalog(), connection.getSchema(), "%", tipos);
        ArrayList<String> tablas = new ArrayList<>();
        while (result.next()) {
            tablas.add(result.getString("TABLE_NAME"));
        }
        MongoCollection <Document> collection = con.getCollection();
        ArrayList <Document> docs = new ArrayList<>();
        Document mainDocument = new Document();
        for(int x = 0; x<tablas.size();x++) {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("Select * from " + tablas.get(x));
            ArrayList<Document> registros = new ArrayList<>();
            while (rs.next()) {
                System.out.println(rs.getMetaData().getColumnCount());
                Document document = new Document();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    String type = rs.getMetaData().getColumnTypeName(i);
                    System.out.println(type);
                    if (type.equals("VARCHAR")) {
                        //System.out.println(rs.getString(i));
                        document.append(rs.getMetaData().getColumnName(i),rs.getString(i));
                    } else if (type.equals("INT")) {
                        //System.out.println(rs.getInt(i));
                       document.append(rs.getMetaData().getColumnName(i),rs.getInt(i));
                    } else if (type.equals("FLOAT")) {
                        //System.out.println(rs.getFloat(i));
                        document.append(rs.getMetaData().getColumnName(i),rs.getFloat(i));
                    } else if (type.equals("DATE")) {
                       // System.out.println(rs.getDate(i).toLocalDate().toString());
                        document.append(rs.getMetaData().getColumnName(i),rs.getDate(i));
                    }
                }
                registros.add(document);
            }
            st.close();
            rs.close();
            mainDocument.append(tablas.get(x),registros);
        }
        try {
            collection.insertOne(mainDocument);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String importSql() throws SQLException {
        Conexion con = new Conexion();
        Connection connection = con.getConextion();
        con.importSQL();
        String schema = con.getSchema();
        connection.close();
        return schema;
    }

}