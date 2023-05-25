package org.convertidor.querys;

import org.convertidor.conexion.Conexion;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

public class NeodatisQuerys {

    public ResultSet getResultSet(String tableName,String schema) throws SQLException {
        Conexion conexion = new Conexion();
        conexion.setSchema(schema);
        Connection connection = conexion.getConextion();
        Statement st = connection.createStatement();
        return st.executeQuery("Select * from "+tableName.toLowerCase());
    }

}
