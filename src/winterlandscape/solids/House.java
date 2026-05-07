package winterlandscape.solids;

import lwjglutils.OGLTexture2D;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static winterlandscape.global.GlutUtils.*;

public class House {

    private OGLTexture2D texture;

    public void render() {
        glEnable(GL_LIGHTING);
        drawWalls();
        drawRoof();
    }

    public void init() {
        try {
            texture = new OGLTexture2D("textures/wood.jpg");
        } catch (IOException e) {
            System.err.println("Error loading textures: " + e.getMessage());
        }
    }

    private void drawWalls() {
        glPushMatrix();
        glEnable(GL_TEXTURE_2D);
        glTranslatef(0, 2.5f, 0);
        glColor3f(0.85f, 0.75f, 0.6f);
        if (texture != null)
            texture.bind();
        glutSolidCube(5);
        glDisable(GL_TEXTURE_2D);
        glPopMatrix();
    }

    private void drawRoof() {
        glPushMatrix();
        glTranslatef(0, 5.0f, 0);
        glEnable(GL_TEXTURE_2D);
        if (texture != null)
            texture.bind();
        glColor3f(0.85f, 0.75f, 0.6f);

        float hw = 2.5f;
        float hd = 2.5f;
        float rh = 2.5f;

        // Normals
        float len = (float) Math.sqrt(rh * rh + hw * hw);
        float lnx = -rh / len, lny = hw / len;
        float rnx = rh / len, rny = hw / len;

        // Left
        glBegin(GL_QUADS);
        glNormal3f(lnx, lny, 0);
        glTexCoord2f(0, 0);
        glVertex3f(-hw, 0, -hd);
        glTexCoord2f(1, 0);
        glVertex3f(-hw, 0, hd);
        glTexCoord2f(1, 1);
        glVertex3f(0, rh, hd);
        glTexCoord2f(0, 1);
        glVertex3f(0, rh, -hd);
        glEnd();

        // Right
        glBegin(GL_QUADS);
        glNormal3f(rnx, rny, 0);
        glTexCoord2f(0, 0);
        glVertex3f(hw, 0, -hd);
        glTexCoord2f(1, 0);
        glVertex3f(hw, 0, hd);
        glTexCoord2f(1, 1);
        glVertex3f(0, rh, hd);
        glTexCoord2f(0, 1);
        glVertex3f(0, rh, -hd);
        glEnd();

        // Front
        glBegin(GL_TRIANGLES);
        glNormal3f(0, 0, 1);
        glTexCoord2f(0, 0);
        glVertex3f(-hw, 0, hd);
        glTexCoord2f(1, 0);
        glVertex3f(hw, 0, hd);
        glTexCoord2f(0.5f, 1);
        glVertex3f(0, rh, hd);
        glEnd();

        // Back
        glBegin(GL_TRIANGLES);
        glNormal3f(0, 0, -1);
        glTexCoord2f(0, 0);
        glVertex3f(hw, 0, -hd);
        glTexCoord2f(1, 0);
        glVertex3f(-hw, 0, -hd);
        glTexCoord2f(0.5f, 1);
        glVertex3f(0, rh, -hd);
        glEnd();

        glDisable(GL_TEXTURE_2D);
        glPopMatrix();
    }
}
