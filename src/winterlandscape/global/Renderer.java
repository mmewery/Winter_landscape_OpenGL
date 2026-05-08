package winterlandscape.global;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import transforms.Vec3D;
import winterlandscape.*;
import winterlandscape.solids.House;
import winterlandscape.solids.SkyBox;
import winterlandscape.solids.TerrainPlane;

import javax.swing.*;
import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.opengl.GL11.*;


public class Renderer extends winterlandscape.global.AbstractRenderer {

    private GLCamera camera;
    private SceneLighting lighting;

    private TerrainPlane terrain;
    private SnowParticleSystem snow;
    private House house;

    private SkyBox skyBox;

    private float dx, dy, ox, oy;
    private float zenit, azimut;

    private float trans, deltaTrans = 0;


    private boolean mouseButton1 = false;

    @Override
    public void init() {
        super.init();
        glEnable(GL_DEPTH_TEST);

        glClearColor(0.05f, 0.05f, 0.15f, 1f);
        glShadeModel(GL_SMOOTH);

        camera = new GLCamera();
        camera.setFirstPerson(true);
        camera.setPosition(new Vec3D(10));

        lighting = new SceneLighting();
        lighting.init();

        skyBox = new SkyBox();
        skyBox.init();

        terrain = new TerrainPlane();
        terrain.init();

        snow = new SnowParticleSystem();

        house = new House();
        house.init();

        SwingUtilities.invokeLater(() -> new ControlPanel(snow, lighting));
    }

    @Override
    public void display() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        trans += deltaTrans;

        glViewport(0, 0, width, height);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GluUtils.gluPerspective(60, (double) width / height, 0.1, 500);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        camera.setMatrix();

        skyBox.render(camera.getPosition().getX(), camera.getPosition().getY(), camera.getPosition().getZ());

        lighting.apply();

        terrain.render();

        house.render();

        snow.update();
        snow.render();

        textRenderer.clear();
        textRenderer.addStr2D(3, 20, "Snow: " + snow.getParticleCount() + " / " + snow.getMaxParticles());
        textRenderer.addStr2D(3, 40, String.format("Wind: (%.4f, %.4f)", snow.getWind().getX(), snow.getWind().getZ()));
        textRenderer.addStr2D(3, 60, String.format("Light: %.1f", lighting.getIntensity()));
        textRenderer.addStr2D(3, height - 3, "WASD=move | Mouse=look | Scroll=zoom");
        textRenderer.draw();
    }


    public Renderer(){
        super();
        glfwMouseButtonCallback = new GLFWMouseButtonCallback() {

            @Override
            public void invoke(long window, int button, int action, int mods) {
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                double x = xBuffer.get(0);
                double y = yBuffer.get(0);

                mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;

                if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS) {
                    ox = (float) x;
                    oy = (float) y;
                }
            }

        };

        glfwCursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double x, double y) {
                if (mouseButton1) {
                    dx = (float) x - ox;
                    dy = (float) y - oy;
                    ox = (float) x;
                    oy = (float) y;
                    zenit -= dy / width * 180;
                    if (zenit > 90)
                        zenit = 90;
                    if (zenit <= -90)
                        zenit = -90;
                    azimut += dx / height * 180;
                    azimut = azimut % 360;
                    camera.setAzimuth(Math.toRadians(azimut));
                    camera.setZenith(Math.toRadians(zenit));
                    dx = 0;
                    dy = 0;
                }
            }
        };

        glfwScrollCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double dx, double dy) {
                camera.forward(dy * 2);
            }
        };

        glfwKeyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                    // We will detect this in our rendering loop
                    glfwSetWindowShouldClose(window, true);
                if (action == GLFW_RELEASE) {
                    trans = 0;
                    deltaTrans = 0;
                }

                if (action == GLFW_PRESS) {
                    switch (key) {
                        case GLFW_KEY_W:
                        case GLFW_KEY_S:
                        case GLFW_KEY_A:
                        case GLFW_KEY_D:
                            deltaTrans = 0.001f;
                            break;
                    }
                }
                switch (key) {
                    case GLFW_KEY_W:
                        camera.forward(trans);
                        if (deltaTrans < 0.001f)
                            deltaTrans = 0.001f;
                        else
                            deltaTrans *= 1.02f;
                        break;

                    case GLFW_KEY_S:
                        camera.backward(trans);
                        if (deltaTrans < 0.001f)
                            deltaTrans = 0.001f;
                        else
                            deltaTrans *= 1.02f;
                        break;

                    case GLFW_KEY_A:
                        camera.left(trans);
                        if (deltaTrans < 0.001f)
                            deltaTrans = 0.001f;
                        else
                            deltaTrans *= 1.02f;
                        break;

                    case GLFW_KEY_D:
                        camera.right(trans);
                        if (deltaTrans < 0.001f)
                            deltaTrans = 0.001f;
                        else
                            deltaTrans *= 1.02f;
                        break;
                }
            }
        };
    }
}
