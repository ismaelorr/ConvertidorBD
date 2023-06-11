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
 *
 *
 * @Author Ismael Orellana Bello
 * @Date 12/06/2023
 * @Version 1.0
 *
 *  Esta clase representa una conexión a la base de datos y
 *  proporciona métodos para realizar operaciones relacionadas.
 */
@NoArgsConstructor
@AllArgsConstructor
public class Conexion {

    public static final String USER = "root";
    public static final String PASSWORD = "admin";
    private static final String MONGODATABASE = "convertidor";
    private static final String JDBC_MYSQL_LOCALHOST = "jdbc:mysql://localhost/";
    private Connection conexion = null;
    @Getter
    @Setter
    private String schema;

    /**
     * Obtiene una conexión a la base de datos MySQL.
     * @return La conexión establecida.
     */
    public Connection getConextion(){
        try {
            // Carga el controlador de la base de datos MySQL
            Class.forName("com.mysql.jdbc.Driver");
            if(schema == null){
                // Verifica si se ha especificado un esquema (base de datos)
                conexion = DriverManager.getConnection(JDBC_MYSQL_LOCALHOST, USER,PASSWORD);
            }
            else{
                // Establece la conexión sin especificar un esquema
                conexion = DriverManager.getConnection(JDBC_MYSQL_LOCALHOST+schema,USER,PASSWORD);
            }
        } catch (Exception e) {
          JOptionPane.showMessageDialog( null,"No se ha podido conectar con la base de datos");
        }
        return conexion;
    }

    /**
     * Cierra la conexión a la base de datos.
     * @throws SQLException Si ocurre un error al cerrar la conexión.
     */
    public void closeConnection() throws SQLException {
        conexion.close();
    }
    /**
     * Crea un esquema (base de datos) en la conexión actual si no existe.
     * El nombre del esquema se toma de la variable de instancia 'schema'.
     */
    public void createSchema() {
        try {
            // Crea una instancia de Statement para ejecutar consultas SQL
            Statement st = conexion.createStatement();
            // Ejecuta la consulta para crear el esquema si no existe
            st.executeUpdate("Create Schema if not exists " + schema);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * Importa un archivo SQL en la base de datos actual.
     * El archivo SQL se selecciona a través de un cuadro de diálogo de selección de archivo.
     * El esquema (base de datos) se crea automáticamente si no existe.
     * El nombre del esquema se toma del nombre del archivo SQL seleccionado.
     */
    public void importSQL(){
        JFileChooser jf = new JFileChooser();
        // Filtra solo los archivos SQL en el cuadro de diálogo de selección de archivo
        FileNameExtensionFilter filter = new FileNameExtensionFilter("SQL","sql");
        jf.setFileFilter(filter);
        int option = jf.showOpenDialog(null);
        if(option == JFileChooser.APPROVE_OPTION){
            String filePath = jf.getSelectedFile().getPath();
            // Obtén el nombre del archivo seleccionado y elimina la extensión '.sql'
            schema = jf.getSelectedFile().getName().replaceAll(".sql","");
            // Crea el esquema (base de datos) si no existe
            createSchema();
            try {
                // Ejecuta el comando de importación de SQL en la línea de comandos
                String backus = "cmd /c mysql -u"+USER+" -p"+PASSWORD+" "+schema+ " < "+filePath;
                Runtime.getRuntime().exec(backus);
            } catch (Exception e) {

            }
        }
    }

    /**
     * Obtiene una colección de MongoDB para realizar operaciones.
     * @return La colección obtenida.
     */
    public MongoCollection getCollection(){
        MongoCollection collection = null;
        try{
            // Crea un cliente de MongoDB y se conecta al servidor local
            MongoClient client = MongoClients
                    .create("mongodb://localhost:27017/");
            MongoDatabase db = client.getDatabase(MONGODATABASE); // Llamada a la base de datos
            collection = db.getCollection(schema);
        }catch(Exception e){
            JOptionPane.showMessageDialog( null,"No se ha podido conectar con la base de datos");
        }
        return collection;
    }

}
