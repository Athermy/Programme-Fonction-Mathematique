/*
MAKLOUFI Mayassa
CHEN Christophe
CASTEL Arthur
*/

package tla;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class PlotPanel extends JPanel {

    private Plot plot;
    private double offsetX = 0;
    private double offsetY = 0;
    private int prevX;
    private int prevY;
    private final double MAX_OFFSET = 10; // Valeur maximale de décalage pour le zoom

    // Constructeur pour initialiser le panneau de tracé avec un objet Plot donné
    public PlotPanel(Plot plot) {
        this.plot = plot;
    }

    // Redéfinir la méthode paintComponent pour dessiner le graphique Plot
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            plot.paint((Graphics2D) g, this.getWidth(), this.getHeight(), offsetX, offsetY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}