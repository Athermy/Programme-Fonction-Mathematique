package tla;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class PlotPanel extends JPanel {

    private Plot plot;

    public PlotPanel(Plot plot) {
        this.plot = plot;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            plot.paint((Graphics2D) g, this.getWidth(), this.getHeight());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
