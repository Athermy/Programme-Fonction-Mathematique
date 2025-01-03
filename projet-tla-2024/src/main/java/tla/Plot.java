/*
MAKLOUFI Mayassa
CHEN Christophe
CASTEL Arthur
*/

package tla;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class Plot {

    final static double STEPS = 500; // Nombre de pas pour le calcul des points de la fonction 
    double range = 5; // Niveau de zoom initial 
    private ExpressionEvaluator evaluator;
    private List<Double> precomputedX;
    private List<Double> precomputedY;
    private Noeud ast;

    public Plot() {
        this.evaluator = new ExpressionEvaluator();
        precomputedX = new ArrayList<>();
        precomputedY = new ArrayList<>();
    }

    // Définit la fonction à tracer (ex. "x^2")
    public void setFunction(String function) throws Exception {
        evaluator.parse(function);
        precomputeValues();
    }

    // Définit l'AST à utiliser pour le tracé du graphique 
    public void setAst(Noeud ast) {
        this.ast = ast;
        precomputeValues();
    }

    // Définit la plage de valeurs à tracer (ex. -5 à 5)
    public void setRange(double range) {
        this.range = range;
        precomputeValues();
    }

    // Pré-calculer les valeurs de la fonction
    // Cette méthode calcule les valeurs de y pour une série de valeurs de x dans la plage spécifiée.
    // Les valeurs de x et y sont stockées dans les listes precomputedX et precomputedY respectivement.
    // Cela permet de tracer le graphique plus efficacement en évitant de recalculer les valeurs à chaque fois que le graphique est redessiné.
    private void precomputeValues() {
        precomputedX.clear();
        precomputedY.clear();
        double step = range / STEPS; // Détermine la taille de chaque pas
        double minX = -range; // Valeur minimale de x
        double maxX = range; // Valeur maximale de x
        for (double x = minX; x <= maxX; x += step) {
            precomputedX.add(x); // Ajouter la valeur de x à la liste
            try {
                double y = evaluator.evaluate(ast, x); // Évaluer la fonction pour x
                precomputedY.add(y); // Ajouter la valeur de y à la liste
                System.out.println("Computed y for x = " + x + ": " + y); // Ligne de débogage
            } catch (Exception e) {
                precomputedY.add(Double.NaN); // Ajouter NaN en cas d'erreur
                System.err.println("Error computing y for x = " + x + ": " + e.getMessage()); // Ligne de débogage
            }
        }
    }

    // Dessine le graphique 
    void paint(Graphics2D g, double w, double h, double offsetX, double offsetY) {
        double centerX = w / 2; // Centre horizontal du panneau
        double centerY = h / 2; // Centre vertical du panneau
        double halfMinSize = Math.min(w, h) / 2; // Moitié de la plus petite dimension du panneau

        // Dessiner les axes du graphique (x et y)
        g.setColor(Color.GRAY);
        g.drawLine((int) (centerX - offsetX * halfMinSize / range), 0, (int) (centerX - offsetX * halfMinSize / range), (int) h);
        g.drawLine(0, (int) (centerY + offsetY * halfMinSize / range), (int) w, (int) (centerY + offsetY * halfMinSize / range));

        // Dessiner les valeurs des axes (ex. -5, -4, ..., 4, 5)
        g.setColor(Color.BLACK);
        for (int i = (int) -range; i <= range; i++) {
            int x = (int) (centerX + (i - offsetX) * halfMinSize / range); // Position x de l'étiquette
            int y = (int) (centerY - (i - offsetY) * halfMinSize / range); // Position y de l'étiquette
            g.drawString(Integer.toString(i), x, (int) (centerY + offsetY * halfMinSize / range)); // Étiquette sur l'axe x
            g.drawString(Integer.toString(i), (int) (centerX - offsetX * halfMinSize / range), y); // Étiquette sur l'axe y
        }

        // Dessiner les points de la fonction 
        g.setColor(Color.BLACK);
        for (int i = 0; i < precomputedX.size(); i++) {
            double x = precomputedX.get(i);
            double y = precomputedY.get(i);
            if (Double.isFinite(y)) {
                int pixelX = (int) (centerX + (x - offsetX) * halfMinSize / range); // Convertir x en coordonnées de pixel
                int pixelY = (int) (centerY - (y - offsetY) * halfMinSize / range); // Convertir y en coordonnées de pixel
                g.fillRect(pixelX, pixelY, 2, 2); // Dessiner un petit rectangle pour représenter le point
            }
        }
    }
}
