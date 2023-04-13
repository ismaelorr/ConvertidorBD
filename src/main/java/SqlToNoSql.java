import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.convertidor.conexion.Conexion;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;


@NoArgsConstructor
public class SqlToNoSql {

    private String types [] = {"String","int","double","date"};

    public void startConversion() throws SQLException {
        Conexion con = new Conexion();
        File f1 = new File("");
        Connection connection = null; //con.getConextion();
       // con.setSchema("basketlite");
        // con.createSchema();
        con.importSQL();
        DatabaseMetaData metadata = connection.getMetaData();
        String[] tipos = {"TABLE"};
        ResultSet result = metadata.getTables(connection.getCatalog(), connection.getSchema(), "%", tipos);
        ArrayList<String> tablas = new ArrayList<>();
        while (result.next()) {
            tablas.add(result.getString("TABLE_NAME"));
        }
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery("Select * from "+tablas.get(2));
        while(rs.next()){
            System.out.println(rs.getMetaData().getColumnCount());
            for(int i = 1;i<rs.getMetaData().getColumnCount();i++){
                String type = rs.getMetaData().getColumnTypeName(i);
                System.out.println(type);
                if(type.equals("VARCHAR")){
                    System.out.println(rs.getString(i));
                }
                else if(type.equals("INT")){
                    System.out.println(rs.getInt(i));
                }
                else if(type.equals("FLOAT")){
                    System.out.println(rs.getFloat(i));
                }
                else if(type.equals("DATE")){
                    System.out.println(rs.getDate(i).toLocalDate().toString());
                }
            }

        }

    }

}
