package sim;

import java.awt.Graphics;

public abstract class Actor implements Drawable {

    private static final int Emax = 200;
    private static final int Emin = 0;

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
        double easing = 0.2;
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
        if (newX < 0) {
            newX = 0;
        }
        if (newX >= world.getWidth()) {
            newX = world.getWidth() - 1;
        }
        if (newY < 0) {
            newY = 0;
        }
        if (newY >= world.getHeight()) {
            newY = world.getHeight() - 1;
        }
        x = newX;
        y = newY;
    }

    protected void wander() {
        int dx = world.getRandom().nextInt(3) - 1;
        int dy = world.getRandom().nextInt(3) - 1;
        moveTo(x + dx, y + dy);
    }

    public abstract void update();

    @Override
    public abstract void draw(Graphics g);
}