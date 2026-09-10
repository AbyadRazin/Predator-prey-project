package sim;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import java.util.Optional;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Predator extends Actor {

    private static final int VISION_RADIUS = 6;
    private static final int ENERGY_LOSS_PER_TICK = 3;
    private static final int ENERGY_PER_MEAL = 30;
    private static final int BREED_INTERVAL = 60;
    private static final int OFFSPRING_ENERGY = 60;
    private static final int MAX_PREDATOR_POPULATION = 10;
    // Predator's sprite is bigger than one grid cell now, so its "mouth" needs
    // to reach further than 1 cell too, or prey visually touching it won't get eaten.
    private static final int EATING_RANGE = 2;

    private static BufferedImage sprite;

    static {
        try {
            sprite = ImageIO.read(new File("assets/predator_fish.png"));
        } catch (IOException e) {
            System.out.println("Could not load predator sprite: " + e.getMessage());
            sprite = null;
        }
    }

    private int ticksUntilBreed = BREED_INTERVAL;

    public Predator(World world, int x, int y, int startingEnergy) {
        super(world, x, y, startingEnergy);
    }

    @Override
    public void update() {
        changeEnergy(-ENERGY_LOSS_PER_TICK);
        if (!isAlive()) {
            return;
        }

        int eatenCount = eatAdjacentPrey();

        if (eatenCount == 0) {
            Optional<Prey> nearestPrey = world.findNearest(getX(), getY(), Prey.class, VISION_RADIUS);
            if (nearestPrey.isPresent()) {
                Prey prey = nearestPrey.get();
                int dx = prey.getX() - getX();
                int dy = prey.getY() - getY();
                moveTo(getX() + Integer.signum(dx) * MOVE_SPEED, getY() + Integer.signum(dy) * MOVE_SPEED);
            } else {
                wander();
            }
        }

        ticksUntilBreed--;
        if (ticksUntilBreed <= 0) {
            breed();
            ticksUntilBreed = BREED_INTERVAL;
        }
    }

    private int eatAdjacentPrey() {
        int eatenCount = 0;
        List<Actor> actors = world.getActors();
        for (int i = 0; i < actors.size(); i++) {
            Actor a = actors.get(i);
            if (a instanceof Prey && a.isAlive()) {
                if (Math.abs(getX() - a.getX()) <= EATING_RANGE && Math.abs(getY() - a.getY()) <= EATING_RANGE) {
                    a.changeEnergy(-a.getEnergy());
                    changeEnergy(ENERGY_PER_MEAL);
                    eatenCount++;
                }
            }
        }
        return eatenCount;
    }

    private void breed() {
        if (countPredators() >= MAX_PREDATOR_POPULATION) {
            return;
        }
        int offspringX = getX() + world.getRandom().nextInt(3) - 1;
        int offspringY = getY() + world.getRandom().nextInt(3) - 1;
        try {
            world.spawnActor(new Predator(world, offspringX, offspringY, OFFSPRING_ENERGY));
        } catch (InvalidPositionException e) {
            System.out.println("Skipped breeding: " + e.getMessage());
        }
    }

    private int countPredators() {
        List<Actor> actors = world.getActors();
        int count = 0;
        for (int i = 0; i < actors.size(); i++) {
            if (actors.get(i) instanceof Predator) {
                count++;
            }
        }
        return count;
    }

    @Override

    public void draw(Graphics g) {
        int size = World.PREDATOR_ACTOR_SIZE;
        int centerOffset = (World.PREDATOR_ACTOR_SIZE - World.CELL_SIZE) / 2;
        int px = (int) (getDrawX() * World.CELL_SIZE) - centerOffset;
        int py = (int) (getDrawY() * World.CELL_SIZE) - centerOffset;

        if (sprite != null) {
            g.drawImage(sprite, px, py, size, size, null);
        } else {
            g.setColor(Color.RED);
            g.fillOval(px, py, size, size);
        }

        int barHeight = 4;
        int barX = px;
        int barY = py - barHeight - 2;
        double healthFraction = getEnergy() / (double) getMaxEnergy();
        int filledWidth = (int) (size * healthFraction);

        g.setColor(Color.DARK_GRAY);
        g.fillRect(barX, barY, size, barHeight);

        g.setColor(Color.GREEN);
        g.fillRect(barX, barY, filledWidth, barHeight);
    }
}