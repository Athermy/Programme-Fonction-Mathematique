/*
MAKLOUFI Mayassa
CHEN Christophe
CASTEL Arthur
*/

package tla;

import java.awt.Color;
import java.awt.Graphics2D;

public class Plot {

    final static double STEPS = 1000;
    double range = 10;
    private ExpressionEvaluator evaluator;

    public Plot() {
        this.evaluator = new ExpressionEvaluator();
    }

    public void setFunction(String function) throws Exception {
        evaluator.parse(function);
    }


    public void setRange(double range) {
        this.range = range;
    }

    void paint(Graphics2D g, double w, double h) throws Exception {
    
        double step = range / STEPS;
    
        double centerX = w / 2;
        double centerY = h / 2;
    
        double halfMinSize = Math.min(w, h) / 2;
    
        // Définir une limite personnalisée pour l'axe x
        double minX = -10;
        double maxX = 100;
    
        // Affiche le repère
        g.setColor(Color.GRAY);
        g.drawLine((int) centerX, 0, (int) centerX, (int) h);
        g.drawLine(0, (int) centerY, (int) w, (int) centerY);
    
        // Affiche les points représentant la fonction
        g.setColor(Color.BLACK);
        for (double x = minX; x <= maxX; x += step) {
    
            double y = evaluator.evaluate(x);
    
            if (Double.isFinite(y)) {
                g.fillRect(
                    (int) (centerX + x * halfMinSize / range),
                    (int) (centerY - y * halfMinSize / range),
                    2,
                    2
                );
            }
        }
    }
}