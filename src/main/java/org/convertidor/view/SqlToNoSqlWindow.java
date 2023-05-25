package org.convertidor.view;

import lombok.Getter;
import lombok.Setter;
import org.convertidor.controllers.SqlToNoSql;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SqlToNoSqlWindow extends JFrame {

    @Getter
    @Setter
    private ArrayList<JPanel> panels = new ArrayList<>();

    @Getter
    @Setter
    private ArrayList<JButton> buttons = new ArrayList<>();

    public SqlToNoSqlWindow(){
        super("SQLTONOSQL");
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        setSize(400,400);
        setLayout(new BorderLayout());
        createJPanels();
        createJButtons();
        JLabel imagenLabel = new JLabel();
        ImageIcon imagen = new ImageIcon("src/main/resources/images/logo.png");
        imagenLabel.setIcon(imagen);
        panels.get(0).add(imagenLabel);
        add(panels.get(0), BorderLayout.NORTH);
        add(panels.get(1), BorderLayout.CENTER);
        setVisible(true);
    }

    public void createJPanels(){
        for(int i = 0; i < 2; i++){
            JPanel panel = new JPanel();
            panel.setBackground(new Color(64, 128, 191));
            panels.add(panel);
        }
    }

    public void createJButtons(){
        panels.get(1).setBackground(new Color(191, 191, 255));
        JButton button = new JButton("Seleccionar archivo");
        buttons.add(button);
        panels.get(1).add(button);
    }


}
