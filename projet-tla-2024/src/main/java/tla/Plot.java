/*
MAKLOUFI Mayassa
CHEN Christophe
CASTEL Arthur
*/

package tla;

import java.awt.Color;
import java.awt.Graphics2D;

public class Plot {

    final static double STEPS = 1000; // Nombre de points calculés dans l'intervalle
    double range = 2; // Intervalle de calcul des valeurs de la fonction
    private ExpressionEvaluator evaluator;
    private Noeud ast;

    public Plot() {
        this.evaluator = new ExpressionEvaluator();
    }

    // Définit la fonction à tracer (ex. "x^2")
    public void setFunction(String function) throws Exception {
        evaluator.parse(function);
    }

    // Définit l'AST à utiliser pour le tracé du graphique 
    public void setAst(Noeud ast) {
        this.ast = ast;
    }

    // Définit la plage de valeurs à tracer (ex. -5 à 5)
    public void setRange(double range) {
        this.range = range;
    }

    // Méthode appelée par PlotPanel pour effectuer le tracé du graphique
    void paint(Graphics2D g, double w, double h, double offsetX, double offsetY) {
        double step = range / STEPS; // Taille de chaque pas
        double centerX = w / 2; // Centre horizontal du panneau
        double centerY = h / 2; // Centre vertical du panneau
        double halfMinSize = Math.min(w, h) / 2; // Moitié de la plus petite dimension du panneau

        // Dessiner les axes du graphique (x et y)
        g.setColor(Color.GRAY);
        g.drawLine((int) (centerX - offsetX * halfMinSize / range), 0, (int) (centerX - offsetX * halfMinSize / range), (int) h);
        g.drawLine(0, (int) (centerY + offsetY * halfMinSize / range), (int) w, (int) (centerY + offsetY * halfMinSize / range));


        // Dessiner les points de la fonction
        g.setColor(Color.BLACK);
        for (double x = -range; x <= range; x += step) {
            try {
                double y = evaluator.evaluate(ast, x); // Évaluer la fonction pour x
                if (Double.isFinite(y)) {
                    int pixelX = (int) (centerX + (x - offsetX) * halfMinSize / range); // Convertir x en coordonnées de pixel
                    int pixelY = (int) (centerY - (y - offsetY) * halfMinSize / range); // Convertir y en coordonnées de pixel
                    g.fillRect(pixelX, pixelY, 2, 2); // Dessiner un petit rectangle pour représenter le point
                }
            } catch (Exception e) {
                System.err.println("Erreur de calcul de y pour x = " + x + ": " + e.getMessage()); // Ligne de débogage
            }
        }
    }
}
