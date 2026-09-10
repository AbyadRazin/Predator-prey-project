package sim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class World {
    public static final int CELL_SIZE = 16;
    private final int width;
    private final int height;
    private final List<Actor> actors;
    private final Random random;

    public World(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("World dimensions must be positive");
        }
        this.width = width;
        this.height = height;
        this.actors = new ArrayList<>();
        this.random = new Random();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Random getRandom() {
        return random;
    }

    public List<Actor> getActors() {
        return Collections.unmodifiableList(actors);
    }

    public void spawnActor(Actor a) throws InvalidPositionException {
        if (a.getX() < 0 || a.getX() >= width || a.getY() < 0 || a.getY() >= height) {
            throw new InvalidPositionException("Cannot spawn actor outside the grid at (" + a.getX() + ", " + a.getY() + ")");
        }
        actors.add(a);
    }

    public void step() {
        List<Actor> snapshot = new ArrayList<>(actors);
        for (int i = 0; i < snapshot.size(); i++) {
            Actor a = snapshot.get(i);
            if (a.isAlive()) {
                a.update();
            }
        }

        List<Actor> survivors = new ArrayList<>();
        for (int i = 0; i < actors.size(); i++) {
            Actor a = actors.get(i);
            if (a.isAlive()) {
                survivors.add(a);
            }
        }
        actors.clear();
        actors.addAll(survivors);
    }

    public <T extends Actor> Optional<T> findNearest(int fromX, int fromY, Class<T> type, int visionRadius) {
        T nearest = null;
        int nearestDistance = Integer.MAX_VALUE;
        for (int i = 0; i < actors.size(); i++) {
            Actor a = actors.get(i);
            if (type.isInstance(a) && a.isAlive()) {
                int dx = a.getX() - fromX;
                int dy = a.getY() - fromY;
                int distance = dx * dx + dy * dy;
                if (distance <= visionRadius * visionRadius && distance < nearestDistance) {
                    nearestDistance = distance;
                    nearest = type.cast(a);
                }
            }
        }
        return Optional.ofNullable(nearest);
    }
}