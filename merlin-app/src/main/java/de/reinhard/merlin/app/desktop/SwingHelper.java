package de.reinhard.merlin.app.desktop;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class SwingHelper {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SwingHelper.class);

    private static String APP_TITLE = "Merlin" + " - 1.0";

    public static String getAppTitle() {
        return APP_TITLE;
    }

    public static void centerWindow(final JFrame frame, final Component component) {
        centerWindow(frame.getLocation(), frame.getSize(), frame, component);
    }

    public static void centerWindow(final Point parentLocation, final Dimension parentDimension, final JFrame frame, final Component component) {
        final Dimension size = component.getSize();
        component.setLocation(parentLocation.x + parentDimension.width / 2 - size.width / 2, parentLocation.y
                + parentDimension.height
                / 2
                - size.height
                / 2);
    }

    public static JFrame getParentFrame(final JPanel panel) {
        Container parent = panel;
        do {
            parent = parent.getParent();
            if (parent instanceof JFrame) {
                return (JFrame) parent;
            }
        } while (parent != null);
        return null;
    }

    public static ImageIcon createImageIcon(final String imageFilename) {
        final String filename = imageFilename;
        try {
            final InputStream in = SwingHelper.class.getResourceAsStream(filename);
            final BufferedImage image = ImageIO.read(in);
            return new ImageIcon(image);
        } catch (final Exception ex) {
            log.error("Error while loading file '" + filename + "' from classpath: " + ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * @param imageFilename Relative to [class path resources]/[Layout.IMAGE_DIR]. Please use Layout image string constants.
     */
    public static JLabel createImageLabel(final String imageFilename) {
        return createImageLabel(imageFilename, null);
    }

    /**
     * @param imageFilename Relative to [class path resources]/[Layout.IMAGE_DIR]. Please use Layout image string constants.
     * @param tooltip       Tool tip to show on mouse over.
     */
    public static JLabel createImageLabel(final String imageFilename, final String tooltip) {
        final ImageIcon imageIcon = createImageIcon(imageFilename);
        final JLabel imageLabel = new JLabel(imageIcon);
        if (tooltip != null) {
            imageLabel.setToolTipText(tooltip);
        }
        return imageLabel;
    }

    /**
     * Calls {@link #createImageLabel(String, String)} and the given tool tip.
     *
     * @param tooltip
     */
    public static JLabel createInfoLabel(final String tooltip) {
        return createImageLabel(tooltip);
    }

    /**
     * Creates a panel containing the given component followed by an info icon with the given tool tip.
     *
     * @param component
     * @param tooltip   Tool tip to show on mouse over on component and following info icon.
     */
    public static JPanel createPanelWithInfoIcon(final JComponent component, final String tooltip) {
        final JPanel panel = new JPanel();
        panel.add(component);
        panel.add(createInfoLabel(tooltip));
        component.setToolTipText(tooltip);
        return panel;
    }
}
