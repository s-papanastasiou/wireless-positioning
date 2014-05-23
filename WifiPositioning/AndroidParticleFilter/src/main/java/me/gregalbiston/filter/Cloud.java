package me.gregalbiston.filter;

import general.Point;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: johanchateau
 * Date: 04/10/13
 * Time: 12:17
 * To change this template use File | Settings | File Templates.
 */
public class Cloud {

    private Point estiPos;
    private List<Particle> particles;
    private final Point inerPoint;

    public Cloud(Point estiPos, List<Particle> particles, Point inerPoint) {
        this.estiPos = estiPos;
        this.particles = particles;
        this.inerPoint = inerPoint;
    }

    public Cloud(Point estiPos, List<Particle> particles) {
        this.estiPos = estiPos;
        this.particles = particles;
        this.inerPoint = estiPos;
    }

    public void setEstiPos(Point estiPos) {
        this.estiPos = estiPos;
    }

    public void setParticles(List<Particle> particles) {
        this.particles = particles;
    }

    public String getParticleCount() {
        return String.format("ParticleNb : %s", particles.size());
    }

    public Point getEstiPos() {
        return estiPos;
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public Point getInerPoint() {
        return inerPoint;
    }
}
