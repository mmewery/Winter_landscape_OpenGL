package winterlandscape;

import static org.lwjgl.opengl.GL11.*;
import static winterlandscape.global.GlutUtils.*;

public class House {

    public void render() {

        glEnable(GL_LIGHTING);
        drawWalls();
        // TODO: Enable lighting, disable textures (or apply textures)
        // TODO: glPushMatrix
        //   TODO: drawWalls()
        //   TODO: drawRoof()
        //   TODO: drawDoor()
        //   TODO: drawWindows()
        //   TODO: drawChimney() (optional)
        // TODO: glPopMatrix
    }

    private void drawWalls(){
        glPushMatrix();
        glTranslatef(0,2.5f, 0);
        glColor3f(0.85f, 0.75f, 0.6f);
        glutSolidCube(5);
        glPopMatrix();
    }
    // TODO: private void drawWalls() — scaled glutSolidCube or quads
    // TODO: private void drawRoof() — triangular prism or scaled cube
    // TODO: private void drawDoor() — colored quad
    // TODO: private void drawWindow(float offsetX) — colored quad with pane divisions
    // TODO: private void drawChimney() — small scaled cube on roof (optional)
}
