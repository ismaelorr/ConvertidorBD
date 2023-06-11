package org.convertidor.controllers;
import org.convertidor.view.MainWindow;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Clase principal que controla la aplicación.
 * Implementa la interfaz ActionListener para manejar los eventos de los botones.
 *
 * @Author Ismael Orellana Bello
 * @Date 12/06/2023
 * @Version 1.0
 */
public class MainController implements ActionListener {

    /**
     * Constructor de la clase MainController.
     * Inicializa el controlador principal de la aplicación.
     * Crea una instancia de la ventana principal y configura los listeners de los botones.
     * Hace visible la ventana principal.
     */
    public MainController(){
        MainWindow mainWindow = new MainWindow();
        // Configurar listeners de los botones de la ventana principal
        for(int i = 0; i<mainWindow.getButtons().size();i++){
            mainWindow.getButtons().get(i).addActionListener(this);
        }
        mainWindow.setVisible(true);
    }

    /**
     * Método que se ejecuta cuando se produce un evento de acción.
     * Realiza la lógica correspondiente según la acción realizada.
     *
     * @param e El evento de acción que se ha producido.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser jf = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Data","sql","json");
        jf.setFileFilter(filter);
        int option = jf.showOpenDialog(null);
        if(option == JFileChooser.APPROVE_OPTION) {
            String filePath = jf.getSelectedFile().getPath();
            if(e.getActionCommand() == "SQL"){
                if(filePath.contains("json")){
                    filePath = filePath.replaceAll(".json","");
                    try {
                        String filePathSplit [] = filePath.split("\\\\");
                        filePath = filePathSplit[filePathSplit.length-1];
                        new NoSqlToSql().generateDataBase(filePath);
                        JOptionPane.showMessageDialog(null,"Se ha convertido la base de datos correctamente");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                      JOptionPane.showMessageDialog(null,"No se ha podido completar la conversión");
                    }
                }
                else{
                    JOptionPane.showMessageDialog(null,"Debes seleccionar una base de datos JSON");
                }
            }
            else if (e.getActionCommand() == "Neodatis"){
                if(filePath.contains("json")){
                    filePath = filePath.replaceAll(".json","");
                    String filePathSplit [] = filePath.split("\\\\");
                    filePath = filePathSplit[filePathSplit.length-1];
                    try{
                        new NoSqlToSql().generateDataBase(filePath);
                        new SqlToNeodatis().convert(filePath);
                        JOptionPane.showMessageDialog(null,"Se ha convertido la base de datos correctamente");
                    }catch (Exception ex){
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null,"No se ha podido completar la conversión");
                    }

                }
                else{
                   filePath =  filePath.replaceAll(".sql","");
                    String filePathSplit [] = filePath.split("\\\\");
                    filePath = filePathSplit[filePathSplit.length-1];
                    try{
                        new SqlToNeodatis().convert(filePath);
                        JOptionPane.showMessageDialog(null,"Se ha convertido la base de datos correctamente");
                    }catch (Exception ex){
                        JOptionPane.showMessageDialog(null,"No se ha podido completar la conversión");
                    }
                }
            }
            else{
                filePath = filePath.replaceAll(".sql","");
                String filePathSplit [] = filePath.split("\\\\");
                filePath = filePathSplit[filePathSplit.length-1];
                try{
                    new SqlToNoSql().startConversion(filePath);
                    JOptionPane.showMessageDialog(null,"Se ha convertido la base de datos correctamente");
                }catch (Exception ex){
                    JOptionPane.showMessageDialog(null,"No se ha podido completar la conversión");
                }
            }
        }
        else{
            JOptionPane.showMessageDialog(null,"Operación cancelada");
        }
    }
}
