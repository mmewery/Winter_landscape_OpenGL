package winterlandscape;

import lwjglutils.OGLTexture2D;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;


public class SkyBox {

    private OGLTexture2D[] faces; //+x, -x, +y, -y, +z, -z

    public void init() {
        try {
            faces[0] = new OGLTexture2D("textures/snow_positive_x.jpg");
            faces[1] = new OGLTexture2D("textures/snow_negative_x.jpg");
            faces[2] = new OGLTexture2D("textures/snow_positive_y.jpg");
            faces[3] = new OGLTexture2D("textures/snow_negative_y.jpg");
            faces[4] = new OGLTexture2D("textures/snow_positive_z.jpg");
            faces[5] = new OGLTexture2D("textures/snow_negative_z.jpg");

        } catch (IOException e) {
            System.err.println("Error loading textures: "+e.getMessage());
        }
    }

    public void render(double eyeX, double eyeY, double eyeZ) {
        glDisable(GL_LIGHTING);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);

        glPushMatrix();
        glTranslated(eyeX, eyeY, eyeZ);

        double size = 200;

        faces[0].bind();
        glBegin(GL_QUADS);

        glVertex3d(size, -size, -size);
        glVertex3d(size, -size, size);
        glVertex3d(size, size, size);
        glVertex3d(size, size, -size);

        glEnd();
        // TODO: glPushMatrix
        //   TODO: Translate to camera position
        //   TODO: Draw 6 textured quads forming a cube
        // TODO: glPopMatrix
        // TODO: Re-enable depth write, lighting
    }

    public void dispose() {
        // TODO: Clean up texture resources
    }
}
