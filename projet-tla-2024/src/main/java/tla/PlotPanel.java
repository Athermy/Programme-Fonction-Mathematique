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
    private final double MAX_OFFSET = 10; // Maximum offset value

    public PlotPanel(Plot plot) {
        this.plot = plot;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                prevX = e.getX();
                prevY = e.getY();
            }
        });
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
