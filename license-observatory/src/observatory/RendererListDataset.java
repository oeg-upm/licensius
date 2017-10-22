package observatory;


import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import java.util.ArrayList;
import java.util.List;
import vroddon.sw.Dataset;
import vroddon.sw.Vocab;


/**
 * Escribe la lista de dispositivos de manera personalizada
 * @author Victor Rodriguez. (c) Mirubee 2012
 */
public class RendererListDataset extends JLabel implements ListCellRenderer {

//        public static List<DataStream> trained = new ArrayList();

        public RendererListDataset() {
            setOpaque(true);
        }

        public Component getListCellRendererComponent(JList paramlist, Object value, int index, boolean isSelected, boolean cellHasFocus) {
           String cadena = value.toString();
           setText(cadena);
           if (isSelected)
                setBackground(Color.LIGHT_GRAY);
            else
                setBackground(Color.WHITE);
            setForeground(Color.DARK_GRAY);

            Dataset v = (Dataset) value;
            if (v.alive==false)
                setForeground(Color.RED);
            if (v.alive==true)
                setForeground(Color.BLACK);
            return this;
        }
    }
