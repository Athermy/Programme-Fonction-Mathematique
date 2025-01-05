/*
MAKLOUFI Mayassa
CHEN Christophe
CASTEL Arthur
*/

package tla;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Main {

    Plot plot;
    Timer timer;

    final static int PREF_HEIGHT = 300;
    final static int PREF_WIDTH = 400;
    final static double RANGE_ADJUST = 10;

    public static void main(String[] args) {
        Main main = new Main();
        SwingUtilities.invokeLater(main::init);
    }

    // Initialisation de l'interface utilisateur
    public void init() {
        plot = new Plot();

        // Fenêtre principale
        JFrame frame = new JFrame("Projet TLA 2024");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Zone de tracé
        PlotPanel widgetTrace = new PlotPanel(plot);
        widgetTrace.setPreferredSize(new Dimension(PREF_WIDTH, PREF_HEIGHT));
        frame.add(widgetTrace, BorderLayout.CENTER);

        // Panneau de contrôle
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel label = new JLabel("f(x) = ");
        topPanel.add(label);

        JTextField inputField = new JTextField(20);
        topPanel.add(inputField);

        JButton btnOk = new JButton("Ok");
        topPanel.add(btnOk);

        JSlider slider = new JSlider(JSlider.HORIZONTAL, 1, 100, (int)(plot.range * RANGE_ADJUST));
        slider.setPaintLabels(true);
        topPanel.add(slider);

        frame.add(topPanel, BorderLayout.NORTH);

        // Action listeners pour le bouton Ok
        ActionListener okActionListener = event -> {
            String userInput = inputField.getText();
            try {
                // Analyse lexicale et syntaxique de l'entrée utilisateur
                AnalyseSyntaxique analyseSyntaxique = new AnalyseSyntaxique();
                Noeud ast = analyseSyntaxique.analyse(new AnalyseLexicale().analyse(userInput));
                plot.setAst(ast); // Mettre à jour l'AST du graphique
                widgetTrace.repaint(); // Redessiner le graphique
                System.out.println("Analyse terminée avec succès !");
            } catch (Exception e) {
                System.err.println("Erreur : " + e.getMessage());
                JOptionPane.showMessageDialog(frame, "Erreur : " + e.getMessage(), "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
            }
        };
        btnOk.addActionListener(okActionListener);

        // Action listeners pour détecter la touche Entrée
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnOk.doClick(); // Simuler un clic sur le bouton Ok
                }
            }
        });

        // Action listeners de changement pour le slider
        slider.addChangeListener(event -> {
            if (timer != null && timer.isRunning()) {
                timer.restart();
            } else {
                timer = new Timer(100, e -> {
                    plot.setRange((double) (100 - slider.getValue()) / RANGE_ADJUST); // Mettre à jour la plage de valeurs
                    widgetTrace.repaint(); // Redessiner le graphique
                });
                timer.setRepeats(false);
                timer.start();
            }
        });

        // Action listeners de molette de souris pour le zoom
        widgetTrace.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int notches = e.getWheelRotation();
                int newValue = slider.getValue() - notches;
                newValue = Math.max(slider.getMinimum(), Math.min(slider.getMaximum(), newValue));
                slider.setValue(newValue); // Mettre à jour la valeur du slider
            }
        });

        // Afficher la fenêtre principale
        frame.pack();
        frame.setVisible(true);
    }
}
