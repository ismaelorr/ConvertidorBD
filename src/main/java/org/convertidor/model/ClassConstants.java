package org.convertidor.model;
import lombok.*;
/**
 * Clase que define constantes utilizadas en el proyecto de conversión.
 * Contiene rutas de paquetes, importaciones y ubicaciones de archivos.
 *
 *     @Author Ismael Orellana Bello
 *     @Date 12/06/2023
 *     @Version 1.0
 */

@NoArgsConstructor
public class ClassConstants {

    /**
     * Ruta del paquete utilizado en el código generado.
     */
    @Getter
    @Setter
    private final String PACKAGE = "package org.convertidor.neodatis; \n";

    /**
     * Importaciones utilizadas en el código generado.
     */
    @Getter
    @Setter
    private final String IMPORTS = "import lombok.*; \n" +
            "import org.convertidor.querys.*;\n" +
            "import org.neodatis.odb.ODB;\n" +
            "import org.neodatis.odb.ODBFactory;\n" +
            "import org.neodatis.odb.Objects;\n" +
            "import org.neodatis.odb.core.query.IQuery;\n" +
            "import org.neodatis.odb.core.query.criteria.Where;\n " +
            "import java.sql.*; \n" +
            "import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;\n" +
            "import javax.swing.*;\n";
    /**
     * Ubicación de los archivos en el proyecto.
     */
    @Getter
    @Setter
    private final String PATH = "src/main/java/org/convertidor/neodatis/";


}
