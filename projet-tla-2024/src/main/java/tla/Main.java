/*
MAKLOUFI Mayassa
CHEN Christophe
CASTEL Arthur
*/

package tla;

import java.awt.*;
import javax.swing.*;

public class Main {

    Plot plot;

    final static int PREF_HEIGHT = 300;
    final static int PREF_WIDTH = 400;
    final static double RANGE_ADJUST = 10;

    public static void main(String[] args) {
        Main main = new Main();
        SwingUtilities.invokeLater(main::init);
    }

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

        // Gestionnaires des différentes actions sur l'IHM
        btnOk.addActionListener(event -> {
            String userInput = inputField.getText();
            try {
                plot.setFunction(userInput);
                widgetTrace.repaint();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Erreur : " + e.getMessage(), "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
            }
        });

        slider.addChangeListener(event -> {
            plot.setRange((double) slider.getValue() / RANGE_ADJUST);
            widgetTrace.repaint();
        });

        // Rend visible la fenêtre principale
        frame.pack();
        frame.setVisible(true);
    }
}