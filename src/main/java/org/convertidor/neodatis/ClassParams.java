package org.convertidor.neodatis;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@NoArgsConstructor
public class ClassParams {

    @Getter
    @Setter
    private ArrayList<String> types = new ArrayList<>();

    @Getter
    @Setter
    private ArrayList<String> values = new ArrayList<>();

    @Getter
    @Setter
    private ArrayList<ForeignKeys> foreignKeys = new ArrayList<>();

    @Getter
    @Setter
    private String tableName;


}
