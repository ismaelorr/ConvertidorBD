package org.convertidor.model;

import lombok.*;




@NoArgsConstructor
public class ClassConstants {

    @Getter
    @Setter
    private final String PACKAGE = "package org.convertidor.neodatis; \n";

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
            "import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;\n";
    @Getter
    @Setter
    private final String PATH = "src/main/java/org/convertidor/neodatis/";


}
