package tla;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
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

    public void init() {
        plot = new Plot();

        // Main window
        JFrame frame = new JFrame("Projet TLA 2024");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Plot area
        PlotPanel widgetTrace = new PlotPanel(plot);
        widgetTrace.setPreferredSize(new Dimension(PREF_WIDTH, PREF_HEIGHT));
        frame.add(widgetTrace, BorderLayout.CENTER);

        // Control panel
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

        // Action listeners
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
            if (timer != null && timer.isRunning()) {
                timer.restart();
            } else {
                timer = new Timer(100, e -> {
                    plot.setRange((double) (100 - slider.getValue()) / RANGE_ADJUST);
                    widgetTrace.repaint();
                });
                timer.setRepeats(false);
                timer.start();
            }
        });

        // Add mouse wheel listener for zooming
        widgetTrace.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int notches = e.getWheelRotation();
                int newValue = slider.getValue() - notches;
                newValue = Math.max(slider.getMinimum(), Math.min(slider.getMaximum(), newValue));
                slider.setValue(newValue);
            }
        });

        // Show main window
        frame.pack();
        frame.setVisible(true);
    }
}
