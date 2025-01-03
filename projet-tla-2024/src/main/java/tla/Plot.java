package tla;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class Plot {

    final static double STEPS = 500; // Reduced steps for faster generation
    double range = 5; // Initial zoom level
    private ExpressionEvaluator evaluator;
    private List<Double> precomputedX;
    private List<Double> precomputedY;

    public Plot() {
        this.evaluator = new ExpressionEvaluator();
        precomputedX = new ArrayList<>();
        precomputedY = new ArrayList<>();
    }

    public void setFunction(String function) throws Exception {
        evaluator.parse(function);
        precomputeValues();
    }

    public void setRange(double range) {
        this.range = range;
        precomputeValues();
    }

    private void precomputeValues() {
        precomputedX.clear();
        precomputedY.clear();
        double step = range / STEPS;
        double minX = -range;
        double maxX = range;
        for (double x = minX; x <= maxX; x += step) {
            precomputedX.add(x);
            try {
                precomputedY.add(evaluator.evaluate(x));
            } catch (Exception e) {
                precomputedY.add(Double.NaN);
            }
        }
    }

    void paint(Graphics2D g, double w, double h, double offsetX, double offsetY) {
        double centerX = w / 2;
        double centerY = h / 2;
        double halfMinSize = Math.min(w, h) / 2;

        // Draw axes
        g.setColor(Color.GRAY);
        g.drawLine((int) (centerX - offsetX * halfMinSize / range), 0, (int) (centerX - offsetX * halfMinSize / range), (int) h);
        g.drawLine(0, (int) (centerY + offsetY * halfMinSize / range), (int) w, (int) (centerY + offsetY * halfMinSize / range));

        // Draw axis labels
        g.setColor(Color.BLACK);
        for (int i = (int) -range; i <= range; i++) {
            int x = (int) (centerX + (i - offsetX) * halfMinSize / range);
            int y = (int) (centerY - (i - offsetY) * halfMinSize / range);
            g.drawString(Integer.toString(i), x, (int) (centerY + offsetY * halfMinSize / range));
            g.drawString(Integer.toString(i), (int) (centerX - offsetX * halfMinSize / range), y);
        }

        // Draw function points
        g.setColor(Color.BLACK);
        for (int i = 0; i < precomputedX.size(); i++) {
            double x = precomputedX.get(i);
            double y = precomputedY.get(i);
            if (Double.isFinite(y)) {
                int pixelX = (int) (centerX + (x - offsetX) * halfMinSize / range);
                int pixelY = (int) (centerY - (y - offsetY) * halfMinSize / range);
                g.fillRect(pixelX, pixelY, 2, 2);
            }
        }
    }
}
