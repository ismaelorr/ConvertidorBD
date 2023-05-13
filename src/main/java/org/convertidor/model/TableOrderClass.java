package org.convertidor.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class TableOrderClass implements Comparable<TableOrderClass>{

    @Getter
    @Setter
    private String nombre;

    @Getter
    @Setter
    private int cantidad;

    @Override
    public int compareTo(TableOrderClass o) {
        return Integer.compare(this.cantidad, o.cantidad);
    }
}
