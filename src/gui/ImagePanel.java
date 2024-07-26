package gui;

import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel {
    private ImageIcon image;

    public ImagePanel(ImageIcon image) {
        super();

        //memorizza l'immagine
        this.image = image;

        //inizializza il pannello
        this.setBorder(null);
        this.setFocusable(false);
        this.setPreferredSize(new Dimension(135, 250));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(image.getImage(), 0, 0, null); //mostra l'immagine come sfondo del pannello
    }
}
