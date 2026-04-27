package winterlandscape.solids;

import transforms.Vec3D;
import winterlandscape.SnowParticleSystem;

/**
 * Represents a single snow particle in the particle system.
 *
 * Stores position, velocity, and active state.
 * Managed by {@link SnowParticleSystem} via a dynamic ArrayList.
 */
public class SnowParticle {

    public Vec3D position;
    public Vec3D velocity;
    public float size;
    public float lifetime;

    public SnowParticle() {
        this.position = new Vec3D();
        this.velocity = new Vec3D();
        this.size = 1.0f;
        this.lifetime = 0f;
    }

    public SnowParticle(Vec3D position, Vec3D velocity, float size) {
        this.position = position;
        this.velocity = velocity;
        this.size = size;
        this.lifetime = 0f;
    }
}
