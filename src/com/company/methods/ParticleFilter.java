package com.company.methods;

import general.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: johanchateau
 * Date: 03/10/13
 * Time: 17:23
 * To change this template use File | Settings | File Templates.
 */

public class ParticleFilter {

    public static Cloud filter(Cloud cloud, Point posProba, InertialPoint inertialPoint, double particleCount, double cloudRange, double cloudDisplacement) {

        // Weight calculus
        List<Particle> weightList = weightCloud(cloud.getParticles(), posProba, particleCount, cloudRange);
        //String message0 = String.format("WeightList :%s",weightList.size());
        //Logging.printLine(message0);
        // Resample
        List<Particle> reSampleList = reSample(weightList);
        //String message1 = String.format("reSampleList :%s",reSampleList.size());
        //Logging.printLine(message1);
        // Move the cloud
        List<Particle> moveCloudList = moveCloud(reSampleList, cloud.getInerPoint(), inertialPoint);
        //String message2 = String.format("MoveCloudList :%s",moveCloudList.size());
        //Logging.printLine(message2);
        // Create new cloud of particles
        List<Particle> newRandomCloudList = newRandomCloud(moveCloudList, cloudDisplacement);
        //String message3 = String.format("NewRandomCloudList :%s",newRandomCloudList.size());
        //Logging.printLine(message3);
        // Calculation of barycentre
        Point estiPosPart = position(newRandomCloudList);

        //Update
        return new Cloud(estiPosPart, newRandomCloudList, inertialPoint.getPoint());
    }

    private static List<Particle> weightCloud(List<Particle> particles, Point posProba, double particleCount, double cloudRange) {

        TreeSet<Particle> sortedList = new TreeSet<Particle>();
        List<Particle> finalList = new ArrayList<Particle>();

        try {
            // We sort particles by weight
            for (Particle particle : particles) {
                particle.weight = ParticleFilter.weight(posProba, particle, cloudRange);
                sortedList.add(particle);
            }
            // We take only the PARTICLE_MAX first particles
            for (int i = 0; i < particleCount; i++) {
                finalList.add(sortedList.pollLast());
            }

        } catch (NullPointerException e) {
            String message = e.getMessage();
            message = e.getLocalizedMessage();
            //System.out.println(message);
        }
        return finalList;
    }

    private static List<Particle> reSample(List<Particle> particles) {

        List<Particle> newList = new ArrayList<>();

        for (Particle particle : particles) {

            if (particle != null) {
                if (particle.weight > 0.75)
                    newList.addAll(createParticles(particle.getPoint(), 4));
                else if ((particle.weight <= 0.75) && (particle.weight > 0.5))
                    newList.addAll(createParticles(particle.getPoint(), 3));
                else if ((particle.weight <= 0.5) && (particle.weight > 0.25))
                    newList.addAll(createParticles(particle.getPoint(), 2));
                else
                    newList.addAll(createParticles(particle.getPoint(), 1));
            }
        }

        return newList;
    }

    private static List<Particle> moveCloud(List<Particle> particles, Point prevInerPoint, InertialPoint inertialPoint) {

        double xDisplacement = inertialPoint.getX() - prevInerPoint.getX();
        double yDisplacement = inertialPoint.getY() - prevInerPoint.getY();

        for (Particle particle : particles) {
            particle.x += xDisplacement;
            particle.y += yDisplacement;
        }

        return particles;

    }

    private static List<Particle> newRandomCloud(List<Particle> particles, double cloudDisplacement) {

        List<Particle> newParticles = new ArrayList<Particle>();
        for (Particle particle : particles) {

            double dx = cloudDisplacement * Math.sqrt(-Math.log(1 - Math.random()));
            double dy = cloudDisplacement * Math.sqrt(-Math.log(1 - Math.random()));

            Random generator = new Random();

            int randomIndex = 0;
            while (randomIndex == 0) {

                randomIndex = generator.nextInt(3) - 1;

            }
            double x = particle.x + randomIndex * dx;

            randomIndex = 0;
            while (randomIndex == 0) {

                randomIndex = generator.nextInt(3) - 1;

            }

            double y = particle.y + randomIndex * dy;
            newParticles.add(new Particle(x, y));
        }

        return newParticles;
    }

    //Barycentre particles
    private static Point position(List<Particle> particles) {

        double x = 0;
        double y = 0;
        double sum = 0;

        for (Particle result : particles) {

            x += result.weight * result.x;
            y += result.weight * result.y;
            sum += result.weight;
        }
        return new Point(x / sum, y / sum);
    }

    public static List<Particle> createParticles(Point pos, int nbPart) {

        List<Particle> list = new ArrayList<Particle>();

        for (int i = 0; i < nbPart; i++) {

            Particle particle = new Particle(pos.getX(), pos.getY());
            list.add(particle);
        }

        return list;
    }

    // Distance between testPos/estiPos
    private static double distance(Point pos1, Particle pos2) {
        double distX = pos1.getX() - pos2.x;
        double distY = pos1.getY() - pos2.y;

        return Math.hypot(distX, distY);
    }

    //  Weight

    private static double weight(Point estiPos, Particle partPos, double cloudRange) {

        double a = Math.pow((distance(estiPos, partPos)), 2);
        double b = -a * cloudRange;

        return Math.exp(b);
    }


}
