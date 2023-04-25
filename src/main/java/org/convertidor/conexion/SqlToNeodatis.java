package org.convertidor.conexion;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SqlToNeodatis {

    public void convert() throws IOException {
         File archivo = new File("C:\\Users\\ismaelor\\Desktop\\MiArchivo.java");
        if (archivo.createNewFile()) {
            System.out.println("Archivo creado");
            FileWriter escritor = new FileWriter(archivo);
            escritor.write("import javax.swing.JFrame;\n" +
                    "\n" +
                    "public class Ventana extends JFrame {\n" +
                    "\n" +
                    "    public Ventana() {\n" +
                    "        // Configurar la ventana\n" +
                    "        setTitle(\"Mi Ventana\");\n" +
                    "        setSize(500, 500);\n" +
                    "        setLocationRelativeTo(null);\n" +
                    "        setDefaultCloseOperation(EXIT_ON_CLOSE);\n" +
                    "        setVisible(true);\n" +
                    "    }\n" +
                    "\n" +
                    "    public static void main(String[] args) {\n" +
                    "        new Ventana();\n" +
                    "    }\n" +
                    "}");
            escritor.close();
            System.out.println("Contenido del archivo escrito");
        } else {
            System.out.println("El archivo ya existe");
        }
        try {
            Process proceso = new ProcessBuilder("java", "C:\\Users\\ismaelor\\Desktop\\MiArchivo.java").start();
            int resultado = proceso.waitFor();
            System.out.println("El proceso terminó con el código de salida " + resultado);
        } catch (IOException e) {
            System.out.println("Error al ejecutar el archivo");
        } catch (InterruptedException e) {
            System.out.println("El proceso fue interrumpido");
        }
    }
}


