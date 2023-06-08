package org.convertidor.view;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MainWindow extends JFrame {

    private String absolutePath = "src/main/resources/images/";

    @Getter
    @Setter
    private ArrayList<JButton> buttons = new ArrayList<>();

    private String [] names = {"Neodatis","SQL","NOSQL"};

    public MainWindow(){
            configProperties();
            JPanel centerPanel = new JPanel();
            centerPanel.setOpaque(false);
            centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
            for(int i = 0; i < names.length; i++){
                    JButton button = new JButton(names[i]);
                    buttons.add(button);
                    centerPanel.add(button);
            }
            add(centerPanel, BorderLayout.SOUTH);
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {

            }
            setVisible(true);
        }

        private void configProperties() {
                setTitle("Ventana con Imagen de Fondo");
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setSize(400, 350);
                setLocationRelativeTo(null);
                setResizable(false);
                setLayout(new BorderLayout());
                ImageIcon backgroundImage = new ImageIcon("src/main/resources/images/fondo.gif"); // Reemplaza "background.jpg" con la ruta de tu imagen de fondo
                JLabel backgroundLabel = new JLabel(backgroundImage);
                backgroundLabel.setBounds(0, 0, backgroundImage.getIconWidth(), backgroundImage.getIconHeight());
                add(backgroundLabel);

        }
}
