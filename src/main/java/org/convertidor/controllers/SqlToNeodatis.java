package org.convertidor.controllers;


import org.convertidor.conexion.Conexion;
import org.convertidor.model.ClassConstants;
import org.convertidor.model.TableOrderClass;
import org.convertidor.model.ClassParams;
import org.convertidor.model.ForeignKeys;
import org.convertidor.neodatis.NeodatisClassGenerator;
import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.util.ArrayList;

/**
 * Clase encargada de convertir datos de una base de datos SQL a un formato compatible con Neodatis.
 *
 *    @Author Ismael Orellana Bello
 *    @Date 12/06/2023
 *    @Version 1.0
 */

public class SqlToNeodatis {

    /**
     * Convierte la base de datos actual a un formato compatible con el sistema NeoDatis.
     *
     * @throws SQLException   Si ocurre un error al ejecutar consultas SQL.
     */
    public void convert(String fileName) throws SQLException {
        //Borrar archivos temporales usados anteriormente
        deleteFiles();
        Conexion conexion = new Conexion();
        conexion.setSchema(fileName);
        //Obtener los metadatos de la base de datos
        Connection connection = conexion.getConextion();
        DatabaseMetaData metaData = connection.getMetaData();
        String[] tipos = {"TABLE"};
        ResultSet result = metaData.getTables(connection.getCatalog(), connection.getSchema(), "%", tipos);
        ArrayList<String> tablas = new ArrayList<>();
        ArrayList<ClassParams> classParams = new ArrayList<>();
        //Obtener los nombres de las tablas
        while (result.next()) {
            String tableName = result.getString("TABLE_NAME");
            tablas.add(tableName.toUpperCase().charAt(0) + tableName.substring(1));
        }

        int iterator [] = new int[tablas.size()];
        //Obtener claves foráneas
        for(int i = 0; i < tablas.size();i++){
            int temporal = 0;
            ArrayList<ForeignKeys> copykeys = obtenerClaves(connection,metaData,tablas.get(i));
            if(copykeys.size()!=0){
                temporal = copykeys.size();
            }
            iterator[i] = temporal;
        }
        for (int i = 0; i < tablas.size(); i++) {
            //Rellenar Clases con el tipo de dato y el nombre del campo
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
        }
        //Ordenar tablas según el número de claves foráneas que tenga
        tablas = orderTables(iterator, tablas);
        classParams = orderParams(tablas,classParams);
        generateFiles(classParams, tablas);
        //Guardar archivo
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar archivo");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int userSelection = fileChooser.showSaveDialog(null);
        String path = "";
        //Seleccionar si le ha dado a guardar o a cancelar
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            File fileToSave = new File(selectedDirectory, conexion.getSchema()+".neo");
            JOptionPane.showMessageDialog(null, "Archivo guardado exitosamente en: " + fileToSave.getAbsolutePath());
            path = fileToSave.getAbsolutePath();
            new NeodatisClassGenerator().getReady(tablas, classParams, conexion.getSchema(),path);
        }
        else{
            JOptionPane.showMessageDialog(null,"Operación cancelada");
        }
    }

    /**
     * Borrar archivos que se hayan usado temporalmente
     */
    private void deleteFiles() {
        String path = new ClassConstants().getPATH();
        File f1 = new File(path);
        if(f1.isDirectory()) {
            //Borrar clases
            File listF1 [] = f1.listFiles();
            if (listF1.length > 2) {
                for (int i = 0; i < listF1.length; i++) {
                    if (!listF1[i].getName().equalsIgnoreCase("temp") && !listF1[i].getName().equalsIgnoreCase("NeodatisClassGenerator.java")) {
                        listF1[i].delete();
                    }
                }
                File f2 = new File(path + "/temp");
                if (f2.isDirectory()) {
                    File listF2[] = f2.listFiles();
                    //Borrar txt
                    for (int i = 0; i < listF1.length; i++) {
                        try {
                            listF2[i].delete();
                        }catch (Exception e){

                        }
                    }
                }
            }
        }
    }

    /**
     * Ordena los parámetros de clase en función del orden de las tablas.
     *
     * @param tablas      Lista de tablas ordenadas.
     * @param classParams Lista de parámetros de clase.
     * @return Una nueva lista de parámetros de clase ordenada según el orden de las tablas.
     */
    private ArrayList<ClassParams> orderParams(ArrayList<String> tablas,ArrayList<ClassParams> classParams) {
        ArrayList<ClassParams> classParamsCopy = new ArrayList<>();
        //Ordenar parámetros
        for(int i = 0; i < tablas.size(); i++){
            for(int x = 0; x < classParams.size(); x++){
                if(classParams.get(x).getTableName().equalsIgnoreCase(tablas.get(i))){
                    classParamsCopy.add(classParams.get(x));
                }
            }
        }
        return classParamsCopy;
    }
    /**
     * Ordena las tablas en función del número de iteraciones requeridas.
     *
     * @param iterator Un array de enteros que representa el número de iteraciones requeridas para cada tabla.
     * @param tablas   Lista de tablas a ordenar.
     * @return Una nueva lista de tablas ordenadas según el número de iteraciones requeridas.
     */
    private ArrayList<String> orderTables(int [] iterator, ArrayList<String> tablas) {
        ArrayList<String> orderedTables = new ArrayList<>();
        ArrayList<TableOrderClass> order = new ArrayList<>();
        //Añadir tablas y número de claves foráneas
        for(int i = 0; i < tablas.size();i++){
            order.add(new TableOrderClass(tablas.get(i),iterator[i]));
        }
        int aux = 0;
        //Ordenar por el método de la burbuja en base a la cantidad de claves foráneas
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
        }
        return orderedTables;
    }

    /**
     * Genera archivos de texto para cada tabla con los tipos de datos correspondientes.
     *
     * @param classParams Lista de objetos ClassParams que contienen información de las tablas y sus tipos de datos.
     * @param tablas      Lista de nombres de tablas.
     */
    private void generateFiles(ArrayList<ClassParams> classParams, ArrayList<String> tablas) {
        ClassConstants constants = new ClassConstants();
        for(int i = 0; i < classParams.size();i++){
            try {
                //Generar archivos temporales
                File file = new File(constants.getPATH() + "/temp/" + classParams.get(i).getTableName() + ".txt");
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

    /**
     * Agrega los parámetros de tipo y nombre de columna a un objeto ClassParams.
     *
     * @param params      Objeto ClassParams al que se agregarán los parámetros.
     * @param type        Tipo de dato de la columna.
     * @param columnName  Nombre de la columna.
     */
    private void addParams(ClassParams params, String type, String columnName) {
        //Añadir tipos de datos
        type = type.toLowerCase();
        if (type.equalsIgnoreCase("varchar") || type.equalsIgnoreCase("date")
                || type.equalsIgnoreCase("text") || type.equalsIgnoreCase("time")) {
            type = "String";
        } else if (type.equalsIgnoreCase("smallint") ||
                type.equalsIgnoreCase("bigInt") || type.equalsIgnoreCase("tniyInt") ||
                type.equalsIgnoreCase("bit")){
            type = "int";
        }
        else if(type.equalsIgnoreCase("bigint")){
            type = "float";
        }
        else if(type.equalsIgnoreCase("decimal")){
            type ="double";
        }
        params.getTypes().add(type);
        params.getValues().add(columnName.toLowerCase());
    }

    /**
     * Obtiene las claves foráneas de una tabla.
     *
     * @param connection  Conexión a la base de datos.
     * @param metaData    Metadatos de la base de datos.
     * @param tableName   Nombre de la tabla.
     * @return            Lista de claves extranjeras de la tabla.
     * @throws SQLException Si ocurre un error al obtener las claves extranjeras.
     */
    private ArrayList<ForeignKeys> obtenerClaves(Connection connection, DatabaseMetaData metaData, String tableName) throws SQLException {
       ArrayList <ForeignKeys> keys = new ArrayList<>();
        ResultSet foreignKeys = metaData.getImportedKeys(connection.getCatalog(), null, tableName);
        int iterator = 0;
        //Obtener metadatos como las claves foráneas, a que tabla pertenecen...
        while (foreignKeys.next()) {
            ForeignKeys key = new ForeignKeys();
            String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
            String pkTableName = foreignKeys.getString("PKTABLE_NAME");
            String pkColumnName = foreignKeys.getString("PKCOLUMN_NAME");
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





