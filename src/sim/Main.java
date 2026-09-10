package sim;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {

    private static final int GRID_WIDTH = 35;
    private static final int GRID_HEIGHT = 25;
    private static final int STARTING_ENERGY = 100;

    public static void main(String[] args) {
        int predatorCount = askForCount("How many predators to start with?", 5);
        int preyCount = askForCount("How many prey to start with?", 15);

        World world = new World(GRID_WIDTH, GRID_HEIGHT);

        for (int i = 0; i < predatorCount; i++) {
            spawnRandom(world, true);
        }
        for (int i = 0; i < preyCount; i++) {
            spawnRandom(world, false);
        }

        SimulationPanel simulationPanel = new SimulationPanel(world);

        JButton addPredatorButton = new JButton("Add Predator");
        addPredatorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spawnRandom(world, true);
            }
        });

        JButton addPreyButton = new JButton("Add 5 Prey");
        addPreyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < 5; i++) {
                    spawnRandom(world, false);
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addPredatorButton);
        buttonPanel.add(addPreyButton);

        JFrame frame = new JFrame("Predator-Prey Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(simulationPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static int askForCount(String message, int defaultValue) {
        String input = JOptionPane.showInputDialog(message, defaultValue);
        if (input == null) {
            return defaultValue;
        }
        try {
            int value = Integer.parseInt(input.trim());
            if (value < 0) {
                return defaultValue;
            }
            return value;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static void spawnRandom(World world, boolean predator) {
        int x = world.getRandom().nextInt(world.getWidth());
        int y = world.getRandom().nextInt(world.getHeight());
        try {
            if (predator) {
                world.spawnActor(new Predator(world, x, y, STARTING_ENERGY));
            } else {
                world.spawnActor(new Prey(world, x, y, STARTING_ENERGY));
            }
        } catch (InvalidPositionException e) {
            System.out.println("Skipped spawn: " + e.getMessage());
        }
    }
}
