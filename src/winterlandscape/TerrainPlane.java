package winterlandscape;

import lwjglutils.OGLTexture2D;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

public class TerrainPlane {

    private static final float SIZE = 20f;
    private static final float Y = 0f;
    private static final float TILE_COUNT = 10f;

    private OGLTexture2D snowTexture;

    public void init() {
        try {
            snowTexture = new OGLTexture2D("textures/snow.jpg");

        } catch (IOException e) {
            System.err.println("Error loading textures: "+e.getMessage());
        }
    }

    public void render() {

        glEnable(GL_TEXTURE_2D);
        snowTexture.bind();
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glColor3f(1, 1, 1);

        glBegin(GL_QUADS);

        glNormal3f(0, 1, 0);

        glTexCoord2f(0, 0);            glVertex3f(-SIZE, Y, -SIZE);
        glTexCoord2f(TILE_COUNT, 0);   glVertex3f( SIZE, Y, -SIZE);
        glTexCoord2f(TILE_COUNT, TILE_COUNT); glVertex3f( SIZE, Y,  SIZE);
        glTexCoord2f(0, TILE_COUNT);   glVertex3f(-SIZE, Y,  SIZE);
        glEnd();

        glDisable(GL_TEXTURE_2D);

    }

}
