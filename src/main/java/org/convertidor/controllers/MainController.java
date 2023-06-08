package org.convertidor.controllers;
import org.convertidor.view.MainWindow;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;

public class MainController implements ActionListener {

    public MainController(){
        MainWindow mainWindow = new MainWindow();
        for(int i = 0; i<mainWindow.getButtons().size();i++){
            mainWindow.getButtons().get(i).addActionListener(this);
        }
        mainWindow.setVisible(true);
    }

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
                        new NoSqlToSql().generateDataBase(filePath);
                    } catch (Exception ex) {
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
                    try{
                        new NoSqlToSql().generateDataBase(filePath);
                        new SqlToNeodatis().convert(filePath);
                    }catch (Exception ex){
                        JOptionPane.showMessageDialog(null,"No se ha podido completar la conversión");
                    }

                }
                else{
                   filePath =  filePath.replaceAll(".sql","");
                    try{
                        new SqlToNeodatis().convert(filePath);
                    }catch (Exception ex){
                        JOptionPane.showMessageDialog(null,"No se ha podido completar la conversión");
                    }
                }
            }
            else{
                filePath = filePath.replaceAll(".sql","");
                try{
                    new SqlToNoSql().startConversion(filePath);
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
