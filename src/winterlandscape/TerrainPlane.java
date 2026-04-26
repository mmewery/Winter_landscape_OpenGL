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

    private static final float SIZE = 10f;
    private static final float Y = 0f;
    private static final float TILE_COUNT = 10f;

    private OGLTexture2D snowTexture;

    public void init() {
        try {
            snowTexture = new OGLTexture2D("textures/snow_negative_y.jpg");

        } catch (IOException e) {
            System.err.println("Error loading textures: "+e.getMessage());
        }
    }

    public void render() {

        glEnable(GL_TEXTURE_2D);
        snowTexture.bind();

        glBegin(GL_QUADS);

        glNormal3f(0, 1, 0);

        glTexCoord2f(0, 0);            glVertex3f(-SIZE, Y, -SIZE);
        glTexCoord2f(TILE_COUNT, 0);   glVertex3f( SIZE, Y, -SIZE);
        glTexCoord2f(TILE_COUNT, TILE_COUNT); glVertex3f( SIZE, Y,  SIZE);
        glTexCoord2f(0, TILE_COUNT);   glVertex3f(-SIZE, Y,  SIZE);
        glEnd();

        glDisable(GL_TEXTURE_2D);

    }

    public void dispose() {
        // TODO: Clean up texture resources if needed
    }
}
