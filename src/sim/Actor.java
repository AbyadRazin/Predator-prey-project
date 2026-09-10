package sim;

import java.awt.Graphics;

public abstract class Actor implements Drawable {

    private static final int Emax = 200;
    private static final int Emin = 0;
    protected static final int MOVE_SPEED = 2;

    protected final World world;
    private int x;
    private int y;
    private int energy;
    private double drawX;
    private double drawY;

    public Actor(World world, int x, int y, int startingEnergy) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.energy = startingEnergy;
        this.drawX = x;
        this.drawY = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public double getDrawX() {
        return drawX;
    }

    public double getDrawY() {
        return drawY;
    }

    public void easeDrawPosition() {
        double easing = 0.12;
        drawX += (x - drawX) * easing;
        drawY += (y - drawY) * easing;
    }

    public int getEnergy() {
        return energy;
    }

    public boolean isAlive() {
        return energy > 0;
    }
    public static int getMaxEnergy() {
        return Emax;
    }

    protected void changeEnergy(int E) {
        energy += E;
        if (energy > Emax) {
            energy = Emax;
        }
        if (energy < Emin) {
            energy = Emin;
        }
    }

    protected void moveTo(int newX, int newY) {
        // Keep a 1-block buffer from the true edge so the (much bigger) sprite
        // never gets visually clipped by the edge of the window.
        int minX = 1;
        int maxX = world.getWidth() - 2;
        int minY = 1;
        int maxY = world.getHeight() - 2;

        if (newX < minX) {
            newX = minX;
        }
        if (newX > maxX) {
            newX = maxX;
        }
        if (newY < minY) {
            newY = minY;
        }
        if (newY > maxY) {
            newY = maxY;
        }
        x = newX;
        y = newY;
    }

    protected void wander() {
        int dirX = 0;
        int dirY = 0;
        while (dirX == 0 && dirY == 0) {
            dirX = world.getRandom().nextInt(3) - 1;
            dirY = world.getRandom().nextInt(3) - 1;
        }
        moveTo(x + dirX * MOVE_SPEED, y + dirY * MOVE_SPEED);
    }

    public abstract void update();

    @Override
    public abstract void draw(Graphics g);
}