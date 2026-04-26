package winterlandscape;

import static org.lwjgl.opengl.GL11.*;

/**
 * Manages lighting setup for the winter scene.
 *
 * Provides methods to configure and modify light parameters in real-time:
 * - Light position
 * - Ambient, diffuse, specular components
 * - Light color and intensity
 *
 * Uses OpenGL fixed-pipeline lighting (GL_LIGHT0).
 */
public class SceneLighting {

    // Light parameters (modifiable via GUI)
    private float[] lightPosition = {10f, 20f, 10f, 1f};
    private float[] ambientColor  = {0.2f, 0.2f, 0.3f, 1f};
    private float[] diffuseColor  = {0.8f, 0.85f, 0.9f, 1f};
    private float[] specularColor = {1f, 1f, 1f, 1f};
    private float intensity = 1.0f;

    public void init() {
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glEnable(GL_COLOR_MATERIAL);
        glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);
        glEnable(GL_NORMALIZE);
    }

    public void apply() {
        // TODO: glLightfv(GL_LIGHT0, GL_POSITION, ...)
        // TODO: glLightfv(GL_LIGHT0, GL_AMBIENT, ...)
        // TODO: glLightfv(GL_LIGHT0, GL_DIFFUSE, ...)
        // TODO: glLightfv(GL_LIGHT0, GL_SPECULAR, ...)
    }

    // Setters for GUI control
    public void setLightPosition(float x, float y, float z) {
        lightPosition[0] = x; lightPosition[1] = y; lightPosition[2] = z;
    }

    public float[] getLightPosition() { return lightPosition; }

    public void setIntensity(float intensity) { this.intensity = intensity; }
    public float getIntensity() { return intensity; }

    public void setDiffuseColor(float r, float g, float b) {
        diffuseColor[0] = r; diffuseColor[1] = g; diffuseColor[2] = b;
    }

    public void setAmbientColor(float r, float g, float b) {
        ambientColor[0] = r; ambientColor[1] = g; ambientColor[2] = b;
    }
}
