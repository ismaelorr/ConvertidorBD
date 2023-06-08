package org.convertidor.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase que representa una clave externa.
 *      @Author Ismael Orellana Bello
 *      @Date 12/06/2023
 *      @Version 1.0
 */
@NoArgsConstructor
public class ForeignKeys {

    /**
     * Columna de la clave externa.
     */
    @Getter
    @Setter
    private String column;

    /**
     * Tabla de referencia de la clave externa.
     */
    @Getter
    @Setter
    private String tableReference;

    /**
     * Nombre de la columna de referencia de la clave externa.
     */
    @Getter
    @Setter
    private String columnReferenceName;

    /**
     * Iterador para las claves externas.
     */
    @Getter
    @Setter
    private int iterator;


}
