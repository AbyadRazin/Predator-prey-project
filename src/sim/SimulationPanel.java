package sim;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimulationPanel extends JPanel {

    private static final int LOGIC_INTERVAL_MS = 400;
    private static final int RENDER_INTERVAL_MS = 16;
    private static final int SAND_HEIGHT = 110;
    private static final int BUBBLE_COUNT = 15;

    private static BufferedImage sandSprite;
    private static BufferedImage dirtSprite;
    private static BufferedImage seaweed1Sprite;
    private static BufferedImage seaweed2Sprite;
    private static BufferedImage seaweed3Sprite;
    private static BufferedImage seaweed4Sprite;
    private static BufferedImage grassSprite;
    private static BufferedImage rockSprite;

    static {
        sandSprite = loadImage("assets/sand.png");
        dirtSprite = loadImage("assets/dirt.png");
        seaweed1Sprite = loadImage("assets/seaweed1.png");
        seaweed2Sprite = loadImage("assets/seaweed2.png");
        seaweed3Sprite = loadImage("assets/seaweed3.png");
        seaweed4Sprite = loadImage("assets/seaweed4.png");
        grassSprite = loadImage("assets/grass.png");
        rockSprite = loadImage("assets/rock.png");
    }

    private static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println("Could not load background sprite: " + e.getMessage());
            return null;
        }
    }

    private final World world;
    private final List<Bubble> bubbles = new ArrayList<>();
    private boolean paused = false;
    private String winnerMessage = null;

    public SimulationPanel(World world) {
        this.world = world;

        int panelWidth = world.getWidth() * World.CELL_SIZE;
        int panelHeight = world.getHeight() * World.CELL_SIZE;
        setPreferredSize(new Dimension(panelWidth, panelHeight));
        // JPanel isn't focusable by default, and a focused JButton eats the
        // spacebar before our own key binding ever sees it, so make this
        // panel able to hold keyboard focus itself.
        setFocusable(true);

        for (int i = 0; i < BUBBLE_COUNT; i++) {
            bubbles.add(new Bubble(panelWidth, panelHeight, world.getRandom()));
        }

        setupPauseKey();

        Timer logicTimer = new Timer(LOGIC_INTERVAL_MS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!paused) {
                    world.step();
                    checkWinCondition();
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
                for (int i = 0; i < bubbles.size(); i++) {
                    bubbles.get(i).update();
                }
                repaint();
            }
        });
        renderTimer.start();
    }

    private void checkWinCondition() {
        List<Actor> actors = world.getActors();
        int predatorCount = 0;
        int preyCount = 0;
        for (int i = 0; i < actors.size(); i++) {
            Actor a = actors.get(i);
            if (a instanceof Predator) {
                predatorCount++;
            } else if (a instanceof Prey) {
                preyCount++;
            }
        }

        if (preyCount == 0 && predatorCount > 0) {
            winnerMessage = "Predators Win!";
            paused = true;
        } else if (predatorCount == 0 && preyCount > 0) {
            winnerMessage = "Prey Wins!";
            paused = true;
        } else if (predatorCount == 0 && preyCount == 0) {
            winnerMessage = "Everyone Died!";
            paused = true;
        }
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

    private void drawAquariumBackground(Graphics g) {
        int width = getWidth();
        int height = getHeight();

        Graphics2D g2d = (Graphics2D) g;
        GradientPaint waterGradient = new GradientPaint(
                0, 0, new Color(176, 224, 255),
                0, height, new Color(20, 90, 150));
        g2d.setPaint(waterGradient);
        g2d.fillRect(0, 0, width, height);

        int sandY = height - SAND_HEIGHT;
        int tileSize = 64;

        // Darker dirt fills the whole sand band first, giving it depth...
        if (dirtSprite != null) {
            for (int x = 0; x < width; x += tileSize) {
                for (int y = sandY; y < height; y += tileSize) {
                    g.drawImage(dirtSprite, x, y, tileSize, tileSize, null);
                }
            }
        }
        // ...then the lighter wavy sand sits on top of it, only at the surface.
        if (sandSprite != null) {
            for (int x = 0; x < width; x += tileSize) {
                g.drawImage(sandSprite, x, sandY, tileSize, tileSize, null);
            }
        }

        int decorationSize = 64;
        int decorationY = sandY - decorationSize + 16;
        BufferedImage[] decorations = {seaweed1Sprite, rockSprite, seaweed3Sprite, grassSprite, seaweed4Sprite, seaweed2Sprite};
        for (int x = 20, index = 0; x < width; x += 110, index++) {
            BufferedImage decoration = decorations[index % decorations.length];
            if (decoration != null) {
                g.drawImage(decoration, x, decorationY, decorationSize, decorationSize, null);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawAquariumBackground(g);

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

        for (int i = 0; i < bubbles.size(); i++) {
            bubbles.get(i).draw(g);
        }

        g.setColor(Color.BLACK);
        g.drawString("Predators: " + predatorCount, 10, 15);
        g.drawString("Prey: " + preyCount, 10, 30);

        if (winnerMessage != null) {
            Font originalFont = g.getFont();
            g.setFont(originalFont.deriveFont(Font.BOLD, 48f));
            FontMetrics metrics = g.getFontMetrics();
            int textWidth = metrics.stringWidth(winnerMessage);
            g.drawString(winnerMessage, (getWidth() - textWidth) / 2, getHeight() / 2);
            g.setFont(originalFont);
        } else if (paused) {
            g.drawString("PAUSED", 10, 45);
        }
    }
}
