package winterlandscape;

import transforms.Vec3D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

public class SnowParticleSystem {

    private final List<SnowParticle> particles = new ArrayList<>();
    private final Random random = new Random();

    private int maxParticles = 100000;
    private Vec3D gravity = new Vec3D(0, -0.0005, 0);
    private Vec3D wind = new Vec3D(0, 0, 0);

    private float spawnAreaSize = 10f;
    private float spawnHeight = 30f;

    private void spawnParticle() {
        float x =  random.nextFloat(-spawnAreaSize, spawnAreaSize);
        float z = random.nextFloat(-spawnAreaSize, spawnAreaSize);
        float y = spawnHeight;
        float velocityY = random.nextFloat(-0.5f, -0.1f);

        particles.add(new SnowParticle(new Vec3D(x, y, z), new Vec3D(0, velocityY, 0), 5));
    }

    public void setMaxParticles(int count) {
        this.maxParticles = count;
    }

    public int getMaxParticles() {
        return maxParticles;
    }

    public void setWind(Vec3D wind) {
        this.wind = wind;
    }

    public Vec3D getWind() {
        return wind;
    }

    public void update(float deltaTime) {

        if(particles.size() < maxParticles) {
            spawnParticle();
        }
        for(SnowParticle particle:particles){
            //particle.velocity = particle.velocity.add(gravity);
            particle.position = particle.position.add(particle.velocity);
            particle.lifetime += deltaTime;

            if(particle.position.getY() <= 0){
                particle.velocity = new Vec3D(0, 0, 0);
            }

            if (particle.lifetime >= 300000) {
                deleteParticle(particle);
            }
        }
    }

    private void deleteParticle(SnowParticle particle) {
        float x =  random.nextFloat(-spawnAreaSize, spawnAreaSize);
        float z = random.nextFloat(-spawnAreaSize, spawnAreaSize);
        float y = spawnHeight;
        float velocityY = random.nextFloat(-0.5f, -0.1f);

        particle.position = new Vec3D(x, y, z);
        particle.lifetime = 0;
        particle.velocity = new Vec3D(0, velocityY, 0);

    }

    public void render() {
        glDisable(GL_LIGHTING);

        glColor3f(1, 1, 1);
        glPointSize(5);
        glBegin(GL_POINTS);
        for(SnowParticle particle:particles){
            glVertex3d(particle.position.getX(), particle.position.getY(), particle.position.getZ());
        }
        glEnd();

        glEnable(GL_LIGHTING);
    }

    public int getActiveCount() {
        return (int) particles.stream().filter(p -> p.active).count();
    }
}
