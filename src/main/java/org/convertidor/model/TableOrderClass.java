package org.convertidor.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Clase que representa una tabla y su cantidad asociada.
 * Implementa la interfaz Comparable para permitir la comparación y clasificación de objetos TableOrderClass.
 *
 *      @Author Ismael Orellana Bello
 *      @Date 12/06/2023
 *      @Version 1.0
 */
@Getter
@Setter
@AllArgsConstructor
public class TableOrderClass implements Comparable<TableOrderClass>{

    /**
     * Nombre de la tabla.
     */
    @Getter
    @Setter
    private String nombre;

    /**
     * Cantidad asociada a la tabla.
     */
    @Getter
    @Setter
    private int cantidad;

    /**
     * Compara este objeto TableOrderClass con otro objeto TableOrderClass.
     * La comparación se realiza en función del valor de la cantidad.
     *
     * @param o objeto TableOrderClass a comparar
     * @return un valor negativo si este objeto es menor que el objeto dado,
     *         cero si son iguales, o un valor positivo si este objeto es mayor
     */
    @Override
    public int compareTo(TableOrderClass o) {
        return Integer.compare(this.cantidad, o.cantidad);
    }
}
