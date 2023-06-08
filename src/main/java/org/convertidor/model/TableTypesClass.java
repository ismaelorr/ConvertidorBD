package org.convertidor.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
/**
 * Clase que representa los nombres y los tipos de datos de una tabla.
 *
 *     @Author Ismael Orellana Bello
 *     @Date 12/06/2023
 *     @Version 1.0
 */

@AllArgsConstructor
public class TableTypesClass {

    /**
     * Lista de nombres de columnas de la tabla.
     */
    @Getter
    @Setter
    private ArrayList<String> name;

    /**
     * Lista de tipos de datos de las columnas de la tabla.
     */
    @Getter
    @Setter
    private ArrayList<String> dataType;
}
