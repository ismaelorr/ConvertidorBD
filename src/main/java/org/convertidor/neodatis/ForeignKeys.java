package org.convertidor.neodatis;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class ForeignKeys {

    @Getter
    @Setter
    private String column;

    @Getter
    @Setter
    private String tableReference;

    @Getter
    @Setter
    private String columnReferenceName;

    @Getter
    @Setter
    private int iterator;


}
