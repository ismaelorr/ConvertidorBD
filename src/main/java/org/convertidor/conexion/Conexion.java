package org.convertidor.conexion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @Author Ismael Orellana Bello
 */
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
                System.out.println("Hago esta");
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
            schema = jf.getSelectedFile().getName().replaceAll(".sql","");
            createSchema();
            try {
                String backus = "cmd /c mysql -u"+USER+" -p"+PASSWORD+" "+schema+ " < "+filePath;
                Runtime.getRuntime().exec(backus);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public MongoCollection getCollection(){
        MongoCollection collection = null;
        try{
            MongoClient client = MongoClients
                    .create("mongodb://localhost:27017/");
            MongoDatabase db = client.getDatabase("practica04"); // Llamada a la base de datos
            System.out.println("accede a bd " + db.getName()); // Muestra nombre de la base de datos
            //db.createCollection(schema);
            collection = db.getCollection(schema);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("No se ha podido conectar con la base de datos");
        }
        return collection;
    }

}
