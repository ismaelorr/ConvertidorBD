package org.convertidor.model;

import lombok.*;


@NoArgsConstructor
public class ClassConstants {

    @Getter
    @Setter
    private final String PACKAGE = "package org.convertidor.neodatis; \n";

    @Getter
    @Setter
    private final String IMPORTS = "import lombok.*; \nimport org.convertidor.querys.*\n";
    @Getter
    @Setter
    private final String PATH = "src/main/java/org/convertidor/neodatis/";


}
