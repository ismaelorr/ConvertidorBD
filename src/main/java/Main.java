import org.convertidor.conexion.Conexion;
import org.convertidor.conexion.SqlToNeodatis;

import java.sql.Connection;

public class Main {

    public static void main(String[] args) throws Exception{
      //  new SqlToNoSql().startConversion();
        new SqlToNeodatis().convert();
    }
}
