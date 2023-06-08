package org.convertidor.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;

/**
 * Clase que representa los parámetros de una clase.
 *
 *  @Author Ismael Orellana Bello
 *  @Date 12/06/2023
 *  @Version 1.0
 */
@NoArgsConstructor
public class ClassParams {

    /**
     * Lista de tipos de datos de los atributos de la clase.
     */
    @Getter
    @Setter
    private ArrayList<String> types = new ArrayList<>();

    /**
     * Lista de valores de los atributos de la clase.
     */
    @Getter
    @Setter
    private ArrayList<String> values = new ArrayList<>();

    /**
     * Lista de claves externas de la clase.
     */
    @Getter
    @Setter
    private ArrayList<ForeignKeys> foreignKeys = new ArrayList<>();

    /**
     * Nombre de la tabla correspondiente a la clase.
     */
    @Getter
    @Setter
    private String tableName;


}
