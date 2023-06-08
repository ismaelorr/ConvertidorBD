package org.convertidor.controllers;

import org.convertidor.model.ClassConstants;
import org.convertidor.model.ClassParams;
import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Esta clase se encarga de generar una clase Java a partir de un nombre y parámetros de
 * clase especificados. La clase generada incluirá anotaciones y variables según los
 * parámetros proporcionados.
 *
 * @Author Ismael Orellana Bello
 * @Date 12/06/2023
 * @Version 1.0
 */
public class ClassGenerator {


    /**
     * Genera una clase Java con el nombre especificado y las variables proporcionadas.
     *
     * @param className El nombre de la clase a generar.
     * @param variables Los parámetros de clase que se utilizarán para generar las variables.
     */
    public void generate(String className, ClassParams variables){
        ClassConstants constants = new ClassConstants();
        File neodatisClass = new File(constants.getPATH() + className+".java");
        try {
            neodatisClass.createNewFile();
            // Escribir anotaciones y encabezado de la clase
            BufferedWriter bufIn = new BufferedWriter(new FileWriter(neodatisClass));
            bufIn.write(constants.getPACKAGE() + constants.getIMPORTS());
            bufIn.write("@NoArgsConstructor \n@AllArgsConstructor \n" +
                    "public class "+className +" { \n \n");
            // Escribir variables de la clase
            for(int i = 0; i < variables.getTypes().size(); i++){
                bufIn.write("@Getter \n@Setter \n");
                bufIn.write("private "+variables.getTypes().get(i)+" "+variables.getValues().get(i)+"; \n \n");
            }
            bufIn.write("}");
            bufIn.close();
            System.out.println(neodatisClass.getAbsolutePath());

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,"No se ha podido generar las clases correspondientes");
        }
    }

}
