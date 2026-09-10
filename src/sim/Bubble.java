package sim;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Bubble implements Drawable {

    private static final int SIZE = 14;
    private static final double MIN_SPEED = 0.6;
    private static final double MAX_SPEED = 1.8;

    private static final BufferedImage[] sprites = new BufferedImage[3];

    static {
        String[] fileNames = {"assets/bubble_a.png", "assets/bubble_b.png", "assets/bubble_c.png"};
        for (int i = 0; i < fileNames.length; i++) {
            try {
                sprites[i] = ImageIO.read(new File(fileNames[i]));
            } catch (IOException e) {
                System.out.println("Could not load bubble sprite: " + e.getMessage());
                sprites[i] = null;
            }
        }
    }

    private final int panelHeight;
    private final double x;
    private double y;
    private final double speed;
    private final BufferedImage sprite;

    public Bubble(int panelWidth, int panelHeight, Random random) {
        this.panelHeight = panelHeight;
        this.x = random.nextInt(panelWidth);
        this.y = random.nextInt(panelHeight);
        this.speed = MIN_SPEED + random.nextDouble() * (MAX_SPEED - MIN_SPEED);
        this.sprite = sprites[random.nextInt(sprites.length)];
    }

    public void update() {
        y -= speed;
        if (y < -SIZE) {
            y = panelHeight;
        }
    }

    @Override
    public void draw(Graphics g) {
        if (sprite != null) {
            g.drawImage(sprite, (int) x, (int) y, SIZE, SIZE, null);
        }
    }
}
