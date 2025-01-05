/*
MAKLOUFI Mayassa
CHEN Christophe
CASTEL Arthur
*/

package tla;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

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
        // Action Listener pour détecter les clics de souris
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                prevX = e.getX();
                prevY = e.getY();
            }
        });
        // Action Listener pour détecter les glissements de souris
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - prevX;
                int dy = e.getY() - prevY;
                offsetX = Math.max(-MAX_OFFSET, Math.min(MAX_OFFSET, offsetX - dx * plot.range / getWidth()));
                offsetY = Math.max(-MAX_OFFSET, Math.min(MAX_OFFSET, offsetY + dy * plot.range / getHeight()));
                prevX = e.getX();
                prevY = e.getY();
                repaint();
            }
        });
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
