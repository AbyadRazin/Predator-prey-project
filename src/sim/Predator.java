package sim;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import java.util.Optional;

public class Predator extends Actor {

    private static final int VISION_RADIUS = 6;
    private static final int ENERGY_LOSS_PER_TICK = 3;
    private static final int ENERGY_PER_MEAL = 40;
    private static final int BREED_INTERVAL = 60;
    private static final int OFFSPRING_ENERGY = 60;
    private static final int MAX_PREDATOR_POPULATION = 10;

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

        Optional<Prey> nearestPrey = world.findNearest(getX(), getY(), Prey.class, VISION_RADIUS);

        if (nearestPrey.isPresent()) {
            Prey prey = nearestPrey.get();
            if (Math.abs(getX() - prey.getX()) <= 1 && Math.abs(getY() - prey.getY()) <= 1) {
                prey.changeEnergy(-prey.getEnergy());
                changeEnergy(ENERGY_PER_MEAL);
            } else {
                int dx = prey.getX() - getX();
                int dy = prey.getY() - getY();
                moveTo(getX() + Integer.signum(dx), getY() + Integer.signum(dy));
            }
        } else {
            wander();
        }

        ticksUntilBreed--;
        if (ticksUntilBreed <= 0) {
            breed();
            ticksUntilBreed = BREED_INTERVAL;
        }
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
        int size = World.CELL_SIZE;
        int px = getX() * size;
        int py = getY() * size;

        g.setColor(Color.RED);
        g.fillOval(px, py, size, size);

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