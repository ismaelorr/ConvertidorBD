import org.convertidor.controllers.ClassGenerator;
import org.convertidor.controllers.SqlToNeodatis;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws Exception{

        // new SqlToNoSql().startConversion();
            new SqlToNeodatis().convert();
    }
}
