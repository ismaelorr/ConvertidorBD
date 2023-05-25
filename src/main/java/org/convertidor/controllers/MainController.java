package org.convertidor.controllers;
import org.convertidor.view.MainWindow;
import org.convertidor.view.SqlToNoSqlWindow;

public class MainController {

    public MainController(){
       //SqlToNoSqlWindow sqlToNoSqlWindow = new SqlToNoSqlWindow();
        MainWindow mainWindow = new MainWindow();
        mainWindow.setVisible(true);
    }

}
