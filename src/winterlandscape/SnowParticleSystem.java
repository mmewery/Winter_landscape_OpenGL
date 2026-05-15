package winterlandscape;

import lwjglutils.OGLTexture2D;
import transforms.Mat4;
import transforms.Vec3D;
import winterlandscape.solids.SnowParticle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.opengl.GL14.*;

public class SnowParticleSystem {

    private final List<SnowParticle> particles = new ArrayList<>();
    private final Random random = new Random();

    private int maxParticles = 10000;
    private Vec3D gravity = new Vec3D(0, -0.00005, 0);
    private Vec3D wind = new Vec3D(0, 0, 0);
    private int maxLifetime = 30000;
    private final float spawnAreaSize = 30f;

    private OGLTexture2D snowTexture;

    public void init() {
        try {
            snowTexture = new OGLTexture2D("textures/img.png");
        } catch (IOException e) {
            System.err.println("Error loading snow texture: " + e.getMessage());
        }
    }

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

        if (particles.size() > maxParticles) {
            particles.subList(maxParticles, particles.size()).clear();
        }

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
                if (inXRange && pz <= WALL_HALF && pz >= WALL_HALF - 0.3) {
                    particle.position = particle.position.withZ(WALL_HALF + 0.05);
                    particle.velocity = new Vec3D(0, particle.velocity.getY(), 0);
                    particle.wallHit = true;
                }
                if (inXRange && pz >= -WALL_HALF && pz <= -WALL_HALF + 0.3) {
                    particle.position = particle.position.withZ(-WALL_HALF - 0.05);
                    particle.velocity = new Vec3D(0, particle.velocity.getY(), 0);
                    particle.wallHit = true;
                }
                if (inZRange && px <= WALL_HALF && px >= WALL_HALF - 0.3) {
                    particle.position = particle.position.withX(WALL_HALF + 0.05);
                    particle.velocity = new Vec3D(0, particle.velocity.getY(), 0);
                    particle.wallHit = true;
                }
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
        double[] mv = new double[16];
        glGetDoublev(GL_MODELVIEW_MATRIX, mv);

        Mat4 modelViewMatrix = new Mat4(mv);

        Vec3D right = new Vec3D(modelViewMatrix.getColumn(0));
        Vec3D up = new Vec3D(modelViewMatrix.getColumn(1));

        glDisable(GL_LIGHTING);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_TEXTURE_2D);
        glDepthMask(false);

        if (snowTexture != null) {
            snowTexture.bind();
        }

        glColor4d(1, 1, 1, 0.9);

        float size = 0.15f;
        Vec3D rightS = right.mul(size);
        Vec3D upS = up.mul(size);

        glBegin(GL_QUADS);
        for(SnowParticle particle:particles){
            Vec3D pos = particle.position;

            Vec3D v1 = pos.sub(rightS).sub(upS);
            glTexCoord2f(0, 0);
            glVertex3d(v1.getX(), v1.getY(), v1.getZ());

            Vec3D v2 = pos.add(rightS).sub(upS);
            glTexCoord2f(1, 0);
            glVertex3d(v2.getX(), v2.getY(), v2.getZ());

            Vec3D v3 = pos.add(rightS).add(upS);
            glTexCoord2f(1, 1);
            glVertex3d(v3.getX(), v3.getY(), v3.getZ());

            Vec3D v4 = pos.sub(rightS).add(upS);
            glTexCoord2f(0, 1);
            glVertex3d(v4.getX(), v4.getY(), v4.getZ());
        }
        glEnd();

        glDepthMask(true);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glEnable(GL_LIGHTING);
    }
}
