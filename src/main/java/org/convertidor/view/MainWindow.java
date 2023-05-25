package org.convertidor.view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MainWindow extends JFrame {

    private String absolutePath = "src/main/resources/images/";

    private ArrayList<JButton> buttons = new ArrayList<>();

    private String paths[] = {"logoSql.png","logoNeodatis.png","logoMongoDb.png"};

    public MainWindow(){
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        setTitle("Ventana con Botones de Imagen de Fondo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        createButtons(container);
    }

    private void createButtons(Container container){
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        for(int i = 0; i < paths.length; i++){
            JButton button = new JButton();
            ImageIcon image = new ImageIcon(absolutePath+paths[i]);
            button.setIcon(image);
            buttons.add(button);
            buttonPanel.add(button);
        }
        container.add(buttonPanel, BorderLayout.SOUTH);
    }
}
