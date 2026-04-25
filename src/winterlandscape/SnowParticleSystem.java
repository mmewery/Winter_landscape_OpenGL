package winterlandscape;

import transforms.Vec3D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

/**
 * Manages a dynamic list of snow particles simulating falling snow.
 *
 * Responsibilities:
 * - Spawn/recycle particles (dynamic ArrayList)
 * - Apply physics each frame: gravity + wind vector
 * - Detect collisions with terrain (Y=0) and house roof
 * - Render particles as GL_POINTS
 */
public class SnowParticleSystem {

    private final List<SnowParticle> particles = new ArrayList<>();
    private final Random random = new Random();

    // Configurable parameters
    private int maxParticles = 1000;
    private Vec3D gravity = new Vec3D(0, -0.5, 0);
    private Vec3D wind = new Vec3D(0, 0, 0);

    // Spawn area bounds
    private float spawnAreaSize = 50f;
    private float spawnHeight = 30f;

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
        // TODO: Spawn new particles up to maxParticles
        // TODO: For each active particle:
        //   - Apply gravity and wind to velocity
        //   - Update position
        //   - Check collision with terrain (Y <= 0) → deactivate or recycle
        //   - Check collision with house roof → deactivate or recycle
        //   - Update lifetime
    }

    public void render() {
        // TODO: Disable lighting for particles
        // TODO: Set point size
        // TODO: glBegin(GL_POINTS)
        //   TODO: For each active particle, set color (white) and glVertex3d
        // TODO: glEnd()
        // TODO: Re-enable lighting
    }

    // TODO: private void spawnParticle() — initialize position above scene, random velocity
    // TODO: private void recycleParticle(SnowParticle p) — reset to spawn position

    public int getActiveCount() {
        return (int) particles.stream().filter(p -> p.active).count();
    }
}
