package winterlandscape.solids;

import transforms.Col;
import transforms.Vec3D;

import static org.lwjgl.opengl.GL11.*;

public class SceneLighting {

    private Vec3D lightPosition = new Vec3D(10, 20, 10);
    private Col ambientColor  = new Col(0.2, 0.2, 0.3);
    private Col diffuseColor  = new Col(0.8, 0.85, 0.9);
    private Col specularColor = new Col(1.0, 1.0, 1.0);
    private float intensity = 1.0f;

    public void init() {
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glEnable(GL_COLOR_MATERIAL);
        glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);
        glEnable(GL_NORMALIZE);
    }

    public void apply() {
        float[] pos = {(float) lightPosition.getX(), (float) lightPosition.getY(), (float) lightPosition.getZ(), 1f};
        glLightfv(GL_LIGHT0, GL_POSITION, pos);

        glLightfv(GL_LIGHT0, GL_AMBIENT, colToFloat(ambientColor));
        glLightfv(GL_LIGHT0, GL_DIFFUSE, colToFloatScaled(diffuseColor, intensity));
        glLightfv(GL_LIGHT0, GL_SPECULAR, colToFloatScaled(specularColor, intensity));

        glLightModelfv(GL_LIGHT_MODEL_AMBIENT, colToFloat(new Col(0.05, 0.05, 0.15)));
    }

    private float[] colToFloat(Col c) {
        return new float[]{(float) c.getR(), (float) c.getG(), (float) c.getB(), (float) c.getA()};
    }

    private float[] colToFloatScaled(Col c, float scale) {
        return new float[]{
                (float) (c.getR() * scale),
                (float) (c.getG() * scale),
                (float) (c.getB() * scale),
                (float) c.getA()
        };
    }

    public void setLightPosition(Vec3D position) {
        this.lightPosition = position;
    }

    public Vec3D getLightPosition() { return lightPosition; }

    public void setIntensity(float intensity) { this.intensity = intensity; }
    public float getIntensity() { return intensity; }

    public void setDiffuseColor(Col color) {
        this.diffuseColor = color;
    }

    public Col getDiffuseColor() { return diffuseColor; }

    public void setAmbientColor(Col color) {
        this.ambientColor = color;
    }

    public Col getAmbientColor() { return ambientColor; }
}