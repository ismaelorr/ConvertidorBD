package org.convertidor.querys;

import org.convertidor.conexion.Conexion;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *  Esta clase proporciona métodos para realizar consultas y operaciones relacionadas con Neodatis.
 *
 *     @Author Ismael Orellana Bello
 *     @Date 12/06/2023
 *     @Version 1.0
 */
public class NeodatisQuerys {

    /**
     * Obtiene un conjunto de resultados (ResultSet) para una tabla específica en un esquema dado.
     *
     * @param tableName Nombre de la tabla.
     * @param schema    Esquema de la base de datos.
     * @return Conjunto de resultados (ResultSet) obtenido.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public ResultSet getResultSet(String tableName,String schema) throws SQLException {
        Conexion conexion = new Conexion();
        conexion.setSchema(schema);
        Connection connection = conexion.getConextion();
        Statement st = connection.createStatement();
        return st.executeQuery("Select * from "+tableName.toLowerCase());
    }

}
