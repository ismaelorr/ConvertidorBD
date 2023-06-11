package org.convertidor.view;

import lombok.Getter;
import lombok.Setter;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Clase que representa la ventana principal de la aplicación.
 *
 *     @Author Ismael Orellana Bello
 *     @Date 12/06/2023
 *     @Version 1.0
 */
public class MainWindow extends JFrame {

    //Ruta con las imágenes
    private String absolutePath = "src/main/resources/images/";

    //ArrayList de botones
    @Getter
    @Setter
    private ArrayList<JButton> buttons = new ArrayList<>();

    //Array con el nombre de los botones
    private String [] names = {"Neodatis","SQL","NOSQL"};

    /**
     * Constructor de la clase MainWindow.
     * Configura las propiedades, crea y añade los botones a la ventana.
     * Establece el aspecto visual del sistema operativo como apariencia.
     */
    public MainWindow(){
            configProperties();
            JPanel centerPanel = new JPanel();
            centerPanel.setOpaque(false);
            centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
            // Creación y adición de botones a la ventana
            for(int i = 0; i < names.length; i++){
                    JButton button = new JButton(names[i]);
                    buttons.add(button);
                    centerPanel.add(button);
            }
            add(centerPanel, BorderLayout.SOUTH);
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Manejo de excepciones en caso de error al establecer el aspecto visual
            }
            setVisible(true);
        }
        /**
         * Configura las propiedades de la ventana principal.
         * Establece el título, el tamaño, la posición, la imagen de fondo y otras propiedades de la ventana.
         */
        private void configProperties() {
                setTitle("Convertidor de bases de datos");
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
