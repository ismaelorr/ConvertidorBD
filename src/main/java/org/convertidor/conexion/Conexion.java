package org.convertidor.conexion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

@NoArgsConstructor
@AllArgsConstructor
public class Conexion {

    public static final String USER = "root";
    public static final String PASSWORD = "admin";
    private Connection conexion = null;
    @Getter
    @Setter
    private String schema;

    public Connection getConextion(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            if(schema == null){
                conexion = DriverManager.getConnection("jdbc:mysql://localhost/", USER,PASSWORD);
            }
            else{
                conexion = DriverManager.getConnection("jdbc:mysql://localhost/"+schema,USER,PASSWORD);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conexion;
    }

    public void closeConnection() throws SQLException {
        conexion.close();
    }

    public void createSchema() {
        try {
            Statement st = conexion.createStatement();
            st.executeUpdate("Create Schema if not exists " + schema);
            System.out.println("Modificaciones");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void importSQL(){
        JFileChooser jf = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("SQL","sql");
        jf.setFileFilter(filter);
        int option = jf.showOpenDialog(null);
        if(option == JFileChooser.APPROVE_OPTION){
            String filePath = jf.getSelectedFile().getPath();
            try {
                String backus = "cmd /c mysql -u"+USER+" -p"+PASSWORD+" basketlite < "+filePath;
                Runtime.getRuntime().exec(backus);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
