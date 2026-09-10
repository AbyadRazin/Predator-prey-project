package sim;
import java.util.List;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Prey extends Actor {

    private static final int ENERGY_GAIN_PER_TICK = 2;
    private static final int BREED_INTERVAL = 10;
    private static final int OFFSPRING_ENERGY = 50;
    private static final int MAX_PREY_POPULATION = 60;

    private static BufferedImage sprite;

    static {
        try {
            sprite = ImageIO.read(new File("assets/prey_fish.png"));
        } catch (IOException e) {
            System.out.println("Could not load prey sprite: " + e.getMessage());
            sprite = null;
        }
    }

    private int ticksUntilBreed = BREED_INTERVAL;

    public Prey(World world, int x, int y, int startingEnergy) {
        super(world, x, y, startingEnergy);
    }

    @Override
    public void update() {
        changeEnergy(ENERGY_GAIN_PER_TICK);
        wander();

        ticksUntilBreed--;
        if (ticksUntilBreed <= 0) {
            breed();
            ticksUntilBreed = BREED_INTERVAL;
        }
    }

    private void breed() {
        if (countPrey() >= MAX_PREY_POPULATION) {
            return;
        }
        int offspringX = getX() + world.getRandom().nextInt(3) - 1;
        int offspringY = getY() + world.getRandom().nextInt(3) - 1;
        try {
            world.spawnActor(new Prey(world, offspringX, offspringY, OFFSPRING_ENERGY));
        } catch (InvalidPositionException e) {
            System.out.println("Skipped breeding: " + e.getMessage());
        }
    }

    private int countPrey() {
        List<Actor> actors = world.getActors();
        int count = 0;
        for (int i = 0; i < actors.size(); i++) {
            if (actors.get(i) instanceof Prey) {
                count++;
            }
        }
        return count;
    }


    @Override
    public void draw(Graphics g) {
        int size = World.ACTOR_SIZE;
        int centerOffset = (World.ACTOR_SIZE - World.CELL_SIZE) / 2;
        int px = (int) (getDrawX() * World.CELL_SIZE) - centerOffset;
        int py = (int) (getDrawY() * World.CELL_SIZE) - centerOffset;

        if (sprite != null) {
            g.drawImage(sprite, px, py, size, size, null);
        } else {
            g.setColor(Color.BLUE);
            g.fillOval(px, py, size, size);
        }
    }
}