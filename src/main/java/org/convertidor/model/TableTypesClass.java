package org.convertidor.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor
public class TableTypesClass {

    @Getter
    @Setter
    private ArrayList<String> name;

    @Getter
    @Setter
    private ArrayList<String> dataType;
}
