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

    }
    public void init(){

        try {
            texture = new OGLTexture2D("textures/wood.jpg");

        } catch (IOException e) {
            System.err.println("Error loading textures: "+e.getMessage());
        }

    }

    private void drawWalls(){
        glPushMatrix();
        glEnable(GL_TEXTURE_2D);
        glTranslatef(0,2.5f, 0);
        glColor3f(0.85f, 0.75f, 0.6f);
        texture.bind();
        glutSolidCube(5);
        glPopMatrix();
        glDisable(GL_TEXTURE_2D);
    }
    // TODO: private void drawWalls() — scaled glutSolidCube or quads
    // TODO: private void drawRoof() — triangular prism or scaled cube
    // TODO: private void drawDoor() — colored quad
    // TODO: private void drawWindow(float offsetX) — colored quad with pane divisions
    // TODO: private void drawChimney() — small scaled cube on roof (optional)
}
