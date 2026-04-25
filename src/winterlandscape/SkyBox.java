package winterlandscape;

import lwjglutils.OGLTexture2D;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

/**
 * Skybox for the winter scene.
 *
 * Renders a textured cube around the camera to create the illusion of
 * a distant winter sky/mountains environment.
 * Uses snow_*.jpg cubemap textures from res/textures/.
 */
public class SkyBox {

    private OGLTexture2D[] faces; // 6 faces: +x, -x, +y, -y, +z, -z

    public void init() {
        // TODO: Load 6 skybox face textures:
        //   snow_positive_x.jpg, snow_negative_x.jpg,
        //   snow_positive_y.jpg, snow_negative_y.jpg,
        //   snow_positive_z.jpg, snow_negative_z.jpg
    }

    public void render(double eyeX, double eyeY, double eyeZ) {
        // TODO: Disable lighting, depth write
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
