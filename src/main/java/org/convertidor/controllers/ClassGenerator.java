package org.convertidor.controllers;

import org.convertidor.model.ClassConstants;
import org.convertidor.neodatis.ClassParams;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ClassGenerator {




    public void generate(String className, ClassParams variables){
        ClassConstants constants = new ClassConstants();
        File neodatisClass = new File(constants.getPATH() + className+".java");
        try {
            neodatisClass.createNewFile();
            BufferedWriter bufIn = new BufferedWriter(new FileWriter(neodatisClass));
            bufIn.write(constants.getPACKAGE() + constants.getIMPORTS());
            bufIn.write("@NoArgsConstructor \n@AllArgsConstructor \n" +
                    "public class "+className +" { \n \n");
            for(int i = 0; i < variables.getTypes().size(); i++){

                bufIn.write("@Getter \n@Setter \n");
                bufIn.write("private "+variables.getTypes().get(i)+" "+variables.getValues().get(i)+"; \n \n");
            }
            bufIn.write("}");
            bufIn.close();
            System.out.println(neodatisClass.getAbsolutePath());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
