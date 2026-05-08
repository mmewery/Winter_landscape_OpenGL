package winterlandscape;

import transforms.Vec3D;
import winterlandscape.solids.SnowParticle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;

public class SnowParticleSystem {

    private final List<SnowParticle> particles = new ArrayList<>();
    private final Random random = new Random();

    private int maxParticles = 10000;
    private Vec3D gravity = new Vec3D(0, -0.00005, 0);
    private Vec3D wind = new Vec3D(0, 0, 0);
    private int maxLifetime = 30000;

    private final float spawnAreaSize = 30f;

    private void spawnParticle() {
        float x =  random.nextFloat(-spawnAreaSize, spawnAreaSize);
        float z = random.nextFloat(-spawnAreaSize, spawnAreaSize);
        float y = 30.0f;
        float velocityY = random.nextFloat(-0.5f, -0.1f);

        particles.add(new SnowParticle(new Vec3D(x, y, z), new Vec3D(0, velocityY, 0), 5));
    }

    public void setMaxParticles(int count) {
        this.maxParticles = count;
    }


    public void setWind(Vec3D wind) {
        this.wind = wind;
    }

    public Vec3D getWind() {
        return wind;
    }

    public void setGravity(Vec3D gravity) {
        this.gravity = gravity;
    }

    public int getParticleCount() {
        return particles.size();
    }

    public int getMaxParticles() {
        return maxParticles;
    }

    public void setMaxLifetime(int lifetime) {
        this.maxLifetime = lifetime;
    }


    private static final float WALL_HALF = 2.5f;
    private static final float WALL_TOP = 5.0f;
    private static final float ROOF_BASE_Y = 5.0f;
    private static final float ROOF_PEAK_Y = 7.5f;
    private static final float ROOF_HALF_WIDTH = 2.5f;
    private static final float ROOF_HALF_DEPTH = 2.5f;

    public void update() {

        if(particles.size() < maxParticles) {
            spawnParticle();
        }
        for(SnowParticle particle:particles){
            if (particle.grounded) {
                particle.lifetime += 5;
                if (particle.lifetime >= maxLifetime) {
                    deleteParticle(particle);
                }
                continue;
            }

            particle.velocity = particle.velocity.add(gravity);
            if (particle.wallHit) {
                particle.position = particle.position.add(particle.velocity);
            } else {
                particle.position = particle.position.add(particle.velocity).add(wind);
            }
            particle.lifetime += 5;

            double px = particle.position.getX();
            double py = particle.position.getY();
            double pz = particle.position.getZ();

            boolean inXRange = px >= -WALL_HALF && px <= WALL_HALF;
            boolean inZRange = pz >= -ROOF_HALF_DEPTH && pz <= ROOF_HALF_DEPTH;

            // roof collision
            if (inXRange && inZRange && py > ROOF_BASE_Y) {

                double roofY = ROOF_BASE_Y + (ROOF_PEAK_Y - ROOF_BASE_Y) * (1.0 - Math.abs(px) / ROOF_HALF_WIDTH);
                if (py <= roofY) {
                    particle.position = particle.position.withY(roofY + 0.05);
                    particle.velocity = new Vec3D(0, 0, 0);
                    particle.grounded = true;
                    continue;
                }
            }

            // wall collision
            if (py > 0 && py <= WALL_TOP) {
                // Front
                if (inXRange && pz <= WALL_HALF && pz >= WALL_HALF - 0.3) {
                    particle.position = particle.position.withZ(WALL_HALF + 0.05);
                    particle.velocity = new Vec3D(0, particle.velocity.getY(), 0);
                    particle.wallHit = true;
                }
                // Back
                if (inXRange && pz >= -WALL_HALF && pz <= -WALL_HALF + 0.3) {
                    particle.position = particle.position.withZ(-WALL_HALF - 0.05);
                    particle.velocity = new Vec3D(0, particle.velocity.getY(), 0);
                    particle.wallHit = true;
                }
                // Right
                if (inZRange && px <= WALL_HALF && px >= WALL_HALF - 0.3) {
                    particle.position = particle.position.withX(WALL_HALF + 0.05);
                    particle.velocity = new Vec3D(0, particle.velocity.getY(), 0);
                    particle.wallHit = true;
                }
                // left
                if (inZRange && px >= -WALL_HALF && px <= -WALL_HALF + 0.3) {
                    particle.position = particle.position.withX(-WALL_HALF - 0.05);
                    particle.velocity = new Vec3D(0, particle.velocity.getY(), 0);
                    particle.wallHit = true;
                }
            }

            // Ground
            if (py <= 0.1) {
                particle.position = particle.position.withY(0.1);
                particle.velocity = new Vec3D(0, 0, 0);
                particle.grounded = true;
            }

            if(px >= spawnAreaSize-10 || px<= -spawnAreaSize+10 || pz >= spawnAreaSize-10 || pz<= -spawnAreaSize+10 ) {
                deleteParticle(particle);
            }

            if (particle.lifetime >= maxLifetime) {
                deleteParticle(particle);
            }
        }
    }

    private void deleteParticle(SnowParticle particle) {
        float x =  random.nextFloat(-spawnAreaSize, spawnAreaSize);
        float z = random.nextFloat(-spawnAreaSize, spawnAreaSize);
        float y = 30.0f;
        float velocityY = random.nextFloat(-0.5f, -0.1f);

        particle.position = new Vec3D(x, y, z);
        particle.lifetime = 0;
        particle.velocity = new Vec3D(0, velocityY, 0);
        particle.grounded = false;
        particle.wallHit = false;

    }

    public void render() {

        glDisable(GL_LIGHTING);
        glEnable(GL_POINT_SMOOTH);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor3f(1, 1, 1);
        glPointSize(30);

        float[] attenuation = {0f, 0.1f, 0.01f};
        glPointParameterfv(GL_POINT_DISTANCE_ATTENUATION, attenuation);
        glBegin(GL_POINTS);
        for(SnowParticle particle:particles){
            glVertex3d(particle.position.getX(), particle.position.getY(), particle.position.getZ());
        }
        glEnd();
        glEnable(GL_LIGHTING);


    }

}
