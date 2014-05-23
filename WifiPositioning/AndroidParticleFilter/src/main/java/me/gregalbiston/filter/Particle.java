package me.gregalbiston.filter;

import general.Point;

/**
 * Created with IntelliJ IDEA. User: pierre Date: 25/09/13 Time: 14:47 To change
 * this template use File | Settings | File Templates.
 */
public class Particle implements Comparable<Particle> {

    double x;
    double y;
    double weight = 1.0;

    public Particle(double x, double y, double weight) {
        this.x = x;
        this.y = y;
        this.weight = weight;
    }

    public Particle(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point getPoint() {
        return new Point(x, y);
    }

    @Override
    public int compareTo(Particle particle) throws NullPointerException, ClassCastException {
        int result = 1;

        if (particle != null) {

            if (this.weight < particle.weight) {
                result = -1;
            } else if (this.weight > particle.weight) {
                result = 1;
            } else {
                if (this.equals(particle)) {
                    result = 0;
                }
            }
        } else {
            throw new NullPointerException("Null not supported");
        }

        return result;
    }
}
