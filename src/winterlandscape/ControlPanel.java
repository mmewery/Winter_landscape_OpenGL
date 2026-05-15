package winterlandscape;

import transforms.Col;
import transforms.Vec3D;
import winterlandscape.solids.SceneLighting;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ControlPanel extends JFrame {

    public ControlPanel(SnowParticleSystem snow, SceneLighting lighting) {
        setTitle("Winter Landscape Controls");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 400);
        setLocation(100, 100);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Winter Landscape Controls",
                TitledBorder.LEFT,
                TitledBorder.TOP
        ));

        // Snow
        JPanel snowPanel = createSectionPanel("Snow");
        //intensity
        JSlider intensitySlider = new JSlider(10, 5000, 1000);
        intensitySlider.setSnapToTicks(true);
        intensitySlider.setMajorTickSpacing(10);
        JLabel intensityLabel = new JLabel("  1000");
        intensitySlider.addChangeListener(e -> {
            int val = intensitySlider.getValue();
            intensityLabel.setText("  " + val);
            snow.setMaxParticles(val);
        });
        snowPanel.add(createSliderRow("Intensity:", intensitySlider, intensityLabel));

        //gravity
        JSlider gravitySlider = new JSlider(0, 100, 5);
        JLabel gravityLabel = new JLabel("  0.05");
        gravitySlider.addChangeListener(e -> {
            gravityLabel.setText(String.format("  %.2f", gravitySlider.getValue() / 100.0));
            snow.setGravity(new Vec3D(0, -gravitySlider.getValue() / 100000.0, 0));
        });
        snowPanel.add(createSliderRow("Gravity:", gravitySlider, gravityLabel));

        JSlider lifetimeSlider = new JSlider(1000, 60000, 30000);
        JLabel lifetimeLabel = new JLabel("  30000");
        lifetimeSlider.addChangeListener(e -> {
            lifetimeLabel.setText("  " + lifetimeSlider.getValue());
            snow.setMaxLifetime(lifetimeSlider.getValue());
        });
        snowPanel.add(createSliderRow("Lifetime:", lifetimeSlider, lifetimeLabel));

        mainPanel.add(snowPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Wind
        JPanel windPanel = createSectionPanel("Wind");

        JSlider windXSlider = new JSlider(-100, 100, 0);
        JLabel windXLabel = new JLabel("  0.00");
        windXSlider.addChangeListener(e -> {
            windXLabel.setText(String.format("  %.2f", windXSlider.getValue() / 100.0f));
            snow.setWind(new Vec3D(windXSlider.getValue() / 100.0, snow.getWind().getY(), snow.getWind().getZ()));
        });
        windPanel.add(createSliderRow("Wind X:", windXSlider, windXLabel));

        JSlider windZSlider = new JSlider(-100, 100, 0);
        JLabel windZLabel = new JLabel("  0.00");
        windZSlider.addChangeListener(e -> {
            windZLabel.setText(String.format("  %.2f", windZSlider.getValue() / 100.0f));
            snow.setWind(new Vec3D(snow.getWind().getX(), snow.getWind().getY(), windZSlider.getValue() / 100.0));
        });
        windPanel.add(createSliderRow("Wind Z:", windZSlider, windZLabel));

        mainPanel.add(windPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Lighting
        JPanel lightingPanel = createSectionPanel("Lighting");

        JSlider lightIntSlider = new JSlider(0, 20, 10);
        JLabel lightIntLabel = new JLabel("  1.00");
        lightIntSlider.addChangeListener(e -> {
            lightIntLabel.setText(String.format("  %.2f", lightIntSlider.getValue() / 10.0f));
            lighting.setIntensity(lightIntSlider.getValue() / 10.0f);
        });
        lightingPanel.add(createSliderRow("Intensity:", lightIntSlider, lightIntLabel));

        // Light R, G, B sliders
        JSlider rSlider = new JSlider(0, 255, 204);
        JSlider gSlider = new JSlider(0, 255, 217);
        JSlider bSlider = new JSlider(0, 255, 230);
        JLabel rLabel = new JLabel("  204");
        JLabel gLabel = new JLabel("  217");
        JLabel bLabel = new JLabel("  230");

        Runnable updateColor = () -> lighting.setDiffuseColor(new Col(
                rSlider.getValue() / 255.0,
                gSlider.getValue() / 255.0,
                bSlider.getValue() / 255.0));

        rSlider.addChangeListener(e -> {
            rLabel.setText("  " + rSlider.getValue());
            updateColor.run();
        });
        gSlider.addChangeListener(e -> {
            gLabel.setText("  " + gSlider.getValue());
            updateColor.run();
        });
        bSlider.addChangeListener(e -> {
            bLabel.setText("  " + bSlider.getValue());
            updateColor.run();
        });

        lightingPanel.add(createSliderRow("Red:", rSlider, rLabel));
        lightingPanel.add(createSliderRow("Green:", gSlider, gLabel));
        lightingPanel.add(createSliderRow("Blue:", bSlider, bLabel));

        // Light position
        JSlider posXSlider = new JSlider(-50, 50, 10);
        JLabel posXLabel = new JLabel("  10");
        posXSlider.addChangeListener(e -> {
            posXLabel.setText("  " + posXSlider.getValue());
            Vec3D pos = lighting.getLightPosition();
            lighting.setLightPosition(new Vec3D(posXSlider.getValue(), pos.getY(), pos.getZ()));
        });
        lightingPanel.add(createSliderRow("Position X:", posXSlider, posXLabel));

        JSlider posYSlider = new JSlider(-50, 50, 20);
        JLabel posYLabel = new JLabel("  20");
        posYSlider.addChangeListener(e -> {
            posYLabel.setText("  " + posYSlider.getValue());
            Vec3D pos = lighting.getLightPosition();
            lighting.setLightPosition(new Vec3D(pos.getX(), posYSlider.getValue(), pos.getZ()));
        });
        lightingPanel.add(createSliderRow("Position Y:", posYSlider, posYLabel));

        JSlider posZSlider = new JSlider(-50, 50, 10);
        JLabel posZLabel = new JLabel("  10");
        posZSlider.addChangeListener(e -> {
            posZLabel.setText("  " + posZSlider.getValue());
            Vec3D pos = lighting.getLightPosition();
            lighting.setLightPosition(new Vec3D(pos.getX(), pos.getY(), posZSlider.getValue()));
        });
        lightingPanel.add(createSliderRow("Position Z:", posZSlider, posZLabel));

        mainPanel.add(lightingPanel);

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    private JPanel createSliderRow(String labelText, JSlider slider, JLabel valueLabel) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));

        JLabel nameLabel = new JLabel(labelText);
        nameLabel.setPreferredSize(new Dimension(80, 25));
        slider.setPreferredSize(new Dimension(150, 25));
        valueLabel.setPreferredSize(new Dimension(50, 25));

        row.add(nameLabel);
        row.add(slider);
        row.add(valueLabel);
        return row;
    }
}
