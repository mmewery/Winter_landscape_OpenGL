package winterlandscape;

import transforms.Vec3D;

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
        snowPanel.add(createSliderRow("Intensity:", 0, 10000, 1000, 1.0f,
                val -> snow.setMaxParticles(val)));
        snowPanel.add(createSliderRow("Gravity:", 0, 100, 5, 100.0f,
                val -> snow.setGravity(new Vec3D(0, -val / 100000.0, 0))));
        mainPanel.add(snowPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Wind
        JPanel windPanel = createSectionPanel("Wind");
        windPanel.add(createSliderRow("Wind X:", -100, 100, 0, 100.0f,
                val -> snow.setWind(new Vec3D(val / 10000.0, snow.getWind().getY(), snow.getWind().getZ()))));
        windPanel.add(createSliderRow("Wind Z:", -100, 100, 0, 100.0f,
                val -> snow.setWind(new Vec3D(snow.getWind().getX(), snow.getWind().getY(), val / 10000.0))));
        mainPanel.add(windPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Lighting
        JPanel lightingPanel = createSectionPanel("Lighting");
        lightingPanel.add(createSliderRow("Intensity:", 0, 20, 10, 10.0f,
                val -> lighting.setIntensity(val / 10.0f)));

        // Light R, G, B sliders
        JSlider rSlider = new JSlider(0, 255, 204);
        JSlider gSlider = new JSlider(0, 255, 217);
        JSlider bSlider = new JSlider(0, 255, 230);

        Runnable updateColor = () -> lighting.setDiffuseColor(
                rSlider.getValue() / 255.0f,
                gSlider.getValue() / 255.0f,
                bSlider.getValue() / 255.0f);

        lightingPanel.add(createColorSliderRow("Red:", rSlider, updateColor));
        lightingPanel.add(createColorSliderRow("Green:", gSlider, updateColor));
        lightingPanel.add(createColorSliderRow("Blue:", bSlider, updateColor));

        // Light position
        lightingPanel.add(createSliderRow("Position X:", -50, 50, 10, 1.0f,
                val -> lighting.setLightPosition(val, lighting.getLightPosition()[1], lighting.getLightPosition()[2])));
        lightingPanel.add(createSliderRow("Position Y:", -50, 50, 20, 1.0f,
                val -> lighting.setLightPosition(lighting.getLightPosition()[0], val, lighting.getLightPosition()[2])));
        lightingPanel.add(createSliderRow("Position Z:", -50, 50, 10, 1.0f,
                val -> lighting.setLightPosition(lighting.getLightPosition()[0], lighting.getLightPosition()[1], val)));

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

    private JPanel createSliderRow(String labelText, int min, int max, int initialValue, float scale,
                                   java.util.function.IntConsumer callback) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));

        JLabel nameLabel = new JLabel(labelText);
        nameLabel.setPreferredSize(new Dimension(80, 25));

        JSlider slider = new JSlider(min, max, initialValue);
        slider.setPreferredSize(new Dimension(150, 25));

        String initialStr = (scale == 1.0f) ? String.valueOf(initialValue) : String.format("%.2f", initialValue / scale);
        JLabel valueLabel = new JLabel("  " + initialStr);
        valueLabel.setPreferredSize(new Dimension(50, 25));

        slider.addChangeListener(e -> {
            if (scale == 1.0f) {
                valueLabel.setText("  " + slider.getValue());
            } else {
                valueLabel.setText(String.format("  %.2f", slider.getValue() / scale));
            }
            callback.accept(slider.getValue());
        });

        row.add(nameLabel);
        row.add(slider);
        row.add(valueLabel);
        return row;
    }

    private JPanel createColorSliderRow(String labelText, JSlider slider, Runnable onChanged) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));

        JLabel nameLabel = new JLabel(labelText);
        nameLabel.setPreferredSize(new Dimension(80, 25));

        slider.setPreferredSize(new Dimension(150, 25));

        JLabel valueLabel = new JLabel("  " + slider.getValue());
        valueLabel.setPreferredSize(new Dimension(50, 25));

        slider.addChangeListener(e -> {
            valueLabel.setText("  " + slider.getValue());
            onChanged.run();
        });

        row.add(nameLabel);
        row.add(slider);
        row.add(valueLabel);
        return row;
    }
}
