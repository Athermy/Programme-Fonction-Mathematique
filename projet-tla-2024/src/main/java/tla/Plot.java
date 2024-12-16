/*
NOM Prénom(s)
NOM Prénom(s)
NOM Prénom(s)
*/

package tla;

import java.awt.*;

/**
 * Plot : calcul du tracé de différentes fonctions en dur, sur un intervalle
 */
public class Plot {

    /**
     * Nombre de point calculés dans l'intervalle
     */
    final static double STEPS = 1000;

    /**
     * le calcul des valeurs de la fonction se fait sur l'intervalle [-range...range]
     */
    double range = 2;

    /**
     * différentes fonctions en dur<br/>
     * A SUPPRIMER DANS VOTRE PROJET
     */
    final static String[] FUNCTION_DESCRIPTIONS = {
        "pow(x,3)/2 + 2*pow(x,2) - 1.5",
        "abs(1/x) + 0.1*x",
        "sin(10.05*x) / (1.5*exp(-0.04*x))",
        "pow(sin(2.8 * x), 2.5) + pow(cos(5.2 * x), 2.1)",
        "sin(x)*sin(10 * x)"
    };

    /**
     * index de la fonction sélectionnée par l'utilisateur<br/>
     * A SUPPRIMER DANS VOTRE PROJET
     */
    int functionIndex = 0;

    /**
     * point d'entrée de sélection d'une fonction par l'IHM<br/>
     * A SUPPRIMER DANS VOTRE PROJET
     */
    void setFunction(int functionIndex) {
        this.functionIndex = functionIndex;
    }

    /**
     * point d'entrée d'affectation du range par l'IHM
     */
    void setRange(double range) {
        this.range = range;
    }

    /**
     * Méthode appelée par PlotPanel pour effectuer le tracé du graphique,
     * selon le range et la fonction selectionnée
     * @param g objet permettant de dessiner sur un JPanel
     * @param w largeur du JPanel
     * @param h hauteur du JPanel
     */
    void paint(Graphics2D g, double w, double h) {

        double step = range / STEPS;

        double centerX = w / 2;
        double centerY = h / 2;

        double halfMinSize = Math.max(w, h) / 2;

        // affiche le repère
        g.setColor(Color.GRAY);
        g.drawLine((int)centerX, 0, (int)centerX, (int)h);
        g.drawLine(0, (int)centerY, (int)w, (int)centerY);

        // affiche différents points représentant la fonction sélectionnée
        g.setColor(Color.BLACK);
        for (double x = -range; x<= range; x += step) {

            double y=0;

            /*
            calcule de la valeur y=f(x) suivant la fonction sélectionnée par l'utilisateur
            A REMPLACER DANS VOTRE PROJET
            */
            switch (functionIndex) {
                case 0:
                    y = Math.pow(x, 3)/2 + 2*Math.pow(x, 2) - 1.5;
                    break;
                case 1:
                    y = Math.abs(1/x) + 0.1*x;
                    break;
                case 2:
                    y = Math.sin(10.05*x) / (1.5*Math.exp(-0.04*x));
                    break;
                case 3:
                    y = Math.pow(Math.sin(2.8 * x), 2.5) + Math.pow(Math.cos(5.2 * x), 2.1);
                    break;
                case 4:
                    y = Math.sin(x) * Math.sin(10 * x);
                    break;
            }

            /*
            Affichage du point de coordonnées (x,y), coordonnées ajustées à la dimension
            de la zone d'affichage du tracé
            */
            if (Double.isFinite(y)) {
                g.drawRect(
                    (int) (centerX + x * halfMinSize / range),
                    (int) (centerY + -y * halfMinSize / range),
                    1,
                    1
                );
            }
        }
    }
}
