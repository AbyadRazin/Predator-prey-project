package sim;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class SimulationPanel extends JPanel {

    private static final int LOGIC_INTERVAL_MS = 400;
    private static final int RENDER_INTERVAL_MS = 30;

    private final World world;
    private boolean paused = false;

    public SimulationPanel(World world) {
        this.world = world;

        int panelWidth = world.getWidth() * World.CELL_SIZE;
        int panelHeight = world.getHeight() * World.CELL_SIZE;
        setPreferredSize(new Dimension(panelWidth, panelHeight));
        setBackground(Color.WHITE);

        setupPauseKey();

        Timer logicTimer = new Timer(LOGIC_INTERVAL_MS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!paused) {
                    world.step();
                }
            }
        });
        logicTimer.start();

        Timer renderTimer = new Timer(RENDER_INTERVAL_MS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Actor> actors = world.getActors();
                for (int i = 0; i < actors.size(); i++) {
                    actors.get(i).easeDrawPosition();
                }
                repaint();
            }
        });
        renderTimer.start();
    }

    private void setupPauseKey() {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "togglePause");
        getActionMap().put("togglePause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paused = !paused;
            }
        });
    }

    public World getWorld() {
        return world;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        List<Actor> actors = world.getActors();
        int predatorCount = 0;
        int preyCount = 0;

        for (int i = 0; i < actors.size(); i++) {
            Actor a = actors.get(i);
            a.draw(g);
            if (a instanceof Predator) {
                predatorCount++;
            } else if (a instanceof Prey) {
                preyCount++;
            }
        }

        g.setColor(Color.BLACK);
        g.drawString("Predators: " + predatorCount, 10, 15);
        g.drawString("Prey: " + preyCount, 10, 30);

        if (paused) {
            g.drawString("PAUSED", 10, 45);
        }
    }
}
