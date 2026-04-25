package winterlandscape;

import lwjglutils.OGLTexture2D;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

/**
 * Flat snowy terrain plane at Y=0.
 *
 * Large textured quad representing the snow-covered ground.
 * Uses tiled snow texture and proper surface normals for lighting.
 */
public class TerrainPlane {

    private static final float SIZE = 100f;
    private static final float Y = 0f;
    private static final float TILE_COUNT = 10f;

    private OGLTexture2D snowTexture;

    public void init() {
        // TODO: Load snow texture from resources
        // try { snowTexture = new OGLTexture2D("textures/snow_positive_y.jpg"); }
        // catch (IOException e) { System.err.println("TerrainPlane: texture load failed
        // — " + e.getMessage()); }
    }

    public void render() {
        // TODO: Enable texture, bind snow texture
        // TODO: glBegin(GL_QUADS)
        // TODO: Set normal (0, 1, 0) for correct lighting
        // TODO: Draw 4 vertices at (-SIZE, Y, -SIZE) to (SIZE, Y, SIZE)
        // TODO: Use tiled texture coordinates (0,0) to (TILE_COUNT, TILE_COUNT)
        // TODO: glEnd()
        // TODO: Disable texture
    }

    public void dispose() {
        // TODO: Clean up texture resources if needed
    }
}
