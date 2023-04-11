package org.convertidor.conexion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@NoArgsConstructor
@AllArgsConstructor
public class Conexion {

    private Connection conexion = null;
    @Getter
    @Setter
    private String schema;

    public Connection getConextion(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            if(schema == null){
                conexion = DriverManager.getConnection("jdbc:mysql://localhost/","root","");
            }
            else{
                conexion = DriverManager.getConnection("jdbc:mysql://localhost/"+schema,"root","");
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

}
