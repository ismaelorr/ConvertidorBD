package org.convertidor.controllers;


import org.convertidor.conexion.Conexion;
import org.convertidor.model.ClassConstants;
import org.convertidor.model.TableOrderClass;
import org.convertidor.neodatis.ClassParams;
import org.convertidor.neodatis.ForeignKeys;
import org.convertidor.neodatis.NeodatisClassGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class SqlToNeodatis {


    public void convert() throws SQLException {
        Conexion conexion = new Conexion();
        conexion.setSchema("frutasyverduras");
        Connection connection = conexion.getConextion();
        DatabaseMetaData metaData = connection.getMetaData();
        String[] tipos = {"TABLE"};
        ResultSet result = metaData.getTables(connection.getCatalog(), connection.getSchema(), "%", tipos);
        ArrayList<String> tablas = new ArrayList<>();
        ArrayList<ClassParams> classParams = new ArrayList<>();
        while (result.next()) {
            String tableName = result.getString("TABLE_NAME");
            tablas.add(tableName.toUpperCase().charAt(0) + tableName.substring(1));
        }

        int iterator [] = new int[tablas.size()];
        for(int i = 0; i < tablas.size();i++){
            int temporal = 0;
            ArrayList<ForeignKeys> copykeys = obtenerClaves(connection,metaData,tablas.get(i));
            if(copykeys.size()!=0){
                temporal = copykeys.size();
            }
            iterator[i] = temporal;
        }

        for (int i = 0; i < tablas.size(); i++) {
            System.out.println("TABLA: " + tablas.get(i));
            ArrayList<ForeignKeys> keys = obtenerClaves(connection, metaData, tablas.get(i));
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("Select * from " + tablas.get(i));
            rs.next();
            ClassParams params = new ClassParams();
            params.setForeignKeys(keys);
            params.setTableName(tablas.get(i));
            for (int x = 1; x <= rs.getMetaData().getColumnCount(); x++) {
                String type = rs.getMetaData().getColumnTypeName(x);
                String columnName = rs.getMetaData().getColumnName(x);
                if (keys.size() != 0) {
                    if (columnName.equalsIgnoreCase(keys.get(0).getColumn())) {
                        params.getTypes().add(keys.get(0).getTableReference().toUpperCase().charAt(0) +
                                keys.get(0).getTableReference().substring(1));
                        params.getValues().add(keys.get(0).getColumn().toLowerCase());
                        keys.remove(keys.get(0));
                    } else {
                        addParams(params, type, columnName);
                    }
                } else {
                    addParams(params, type, columnName);
                }
            }
            classParams.add(params);
            new ClassGenerator().generate(tablas.get(i), params);

            generateFiles(classParams, tablas);
        }
        tablas = orderTables(iterator, tablas);
        classParams = orderParams(tablas,classParams);
        new NeodatisClassGenerator().getReady(tablas, classParams, conexion.getSchema());

    }

    private ArrayList<ClassParams> orderParams(ArrayList<String> tablas,ArrayList<ClassParams> classParams) {
        for (int i = 0; i < tablas.size() - 1; i++) {
            for (int j = 0; j < tablas.size() - i - 1; j++) {
                if (tablas.get(j).equalsIgnoreCase(classParams.get(j + 1).getTableName())) {
                    ClassParams temp = classParams.get(j);
                    classParams.set(j, classParams.get(j + 1));
                    classParams.set(j + 1, temp);
                }
            }

        }
        return classParams;
    }

    private ArrayList<String> orderTables(int [] iterator, ArrayList<String> tablas) {
        ArrayList<String> orderedTables = new ArrayList<>();
        ArrayList<TableOrderClass> order = new ArrayList<>();
        for(int i = 0; i < tablas.size();i++){
            order.add(new TableOrderClass(tablas.get(i),iterator[i]));
        }
        int aux = 0;
        for (int i = 0; i < order.size() - 1; i++) {
            for (int j = 0; j < order.size() - i - 1; j++) {
                if (order.get(j).getCantidad() > order.get(j + 1).getCantidad()) {
                    TableOrderClass temp = order.get(j);
                    order.set(j,order.get(j + 1));
                    order.set(j+1,temp);
                }
            }
        }
        for(int i = 0; i < order.size();i++){
            orderedTables.add(order.get(i).getNombre());
            System.out.println(order.get(i).getNombre());
        }
        return orderedTables;
    }

    private void generateFiles(ArrayList<ClassParams> classParams, ArrayList<String> tablas) {
        ClassConstants constants = new ClassConstants();
        for(int i = 0; i < tablas.size();i++){
            try {
                File file = new File(constants.getPATH() + "/temp/" + tablas.get(i) + ".txt");
                file.createNewFile();
                BufferedWriter bufIn = new BufferedWriter(new FileWriter(file));
                for(int x = 0; x < classParams.get(i).getValues().size(); x++){
                    bufIn.write(classParams.get(i).getTypes().get(x)+"\n");
                }
                bufIn.close();
            }catch (Exception e){

            }
        }

    }

    private void addParams(ClassParams params, String type, String columnName) {
        type = type.toLowerCase();
        if (type.equalsIgnoreCase("varchar") || type.equalsIgnoreCase("date")) {
            type = "String";
        } else if (type.equalsIgnoreCase("smallint")) {
            type = "int";
        }
        else if(type.equalsIgnoreCase("bigint")){
            type = "float";
        }
        params.getTypes().add(type);
        params.getValues().add(columnName.toLowerCase());
    }


    private ArrayList<ForeignKeys> obtenerClaves(Connection connection, DatabaseMetaData metaData, String tableName) throws SQLException {
       ArrayList <ForeignKeys> keys = new ArrayList<>();
        ResultSet foreignKeys = metaData.getImportedKeys(connection.getCatalog(), null, tableName);
        int iterator = 0;
        while (foreignKeys.next()) {
            ForeignKeys key = new ForeignKeys();
            String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
            String pkTableName = foreignKeys.getString("PKTABLE_NAME");
            String pkColumnName = foreignKeys.getString("PKCOLUMN_NAME");
            System.out.println(fkColumnName);
            key.setColumn(fkColumnName);
            key.setTableReference(pkTableName);
            key.setColumnReferenceName(pkColumnName);
            key.setIterator(++iterator);
            keys.add(key);

        }
        foreignKeys.close();
        return keys;
    }


}





