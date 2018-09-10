package de.reinhard.merlin.app.desktop;

import java.awt.*;

public class GridBagConstraintsHelper {
    private GridBagConstraints con = new GridBagConstraints();

    public static final Insets STANDARD_INSETS = new Insets(5, 5, 5, 5);

    public GridBagConstraints get() {
        return con;
    }

    /**
     * Must be called first (resets all other settings).
     */
    public GridBagConstraintsHelper setLabel(final int gridx, final int gridy) {
        return set(gridx, gridy, GridBagConstraints.WEST);
    }

    /**
     * Must be called first (resets all other settings).
     */
    public GridBagConstraintsHelper setLabel(final int gridx, final int gridy, final int anchor) {
        return set(gridx, gridy, anchor);
    }

    /**
     * Must be called first (resets all other settings).
     */
    public GridBagConstraintsHelper set(final int gridx, final int gridy) {
        return set(gridx, gridy, GridBagConstraints.WEST);
    }

    /**
     * Must be called first (resets all other settings).
     */
    public GridBagConstraintsHelper set(final int gridx, final int gridy, final int anchor) {
        con.gridx = gridx;
        con.gridy = gridy;
        con.gridwidth = 1;
        con.gridheight = 1;
        con.weightx = 0.0;
        con.weighty = 0.0;
        con.ipadx = con.ipady = 0;
        con.insets = STANDARD_INSETS;
        con.fill = GridBagConstraints.NONE;
        con.anchor = anchor;
        return this;
    }

    public GridBagConstraintsHelper setGrid(final int gridwidth, final int gridheight) {
        con.gridwidth = gridwidth;
        con.gridheight = gridheight;
        return this;
    }

    public GridBagConstraintsHelper setWeight(final double weightx, final double weighty) {
        con.weightx = weightx;
        con.weighty = weighty;
        return this;
    }

    public GridBagConstraintsHelper setWeight(final double weightx, final double weighty, final int fill) {
        con.weightx = weightx;
        con.weighty = weighty;
        con.fill = fill;
        return this;
    }

    public GridBagConstraintsHelper setFill(final int fill) {
        con.fill = fill;
        return this;
    }

    public GridBagConstraintsHelper setInsets(final int top, final int left, final int bottom, final int right) {
        con.insets = new Insets(top, left, bottom, right);
        return this;
    }

    public GridBagConstraintsHelper setPad(final int ipadx, final int ipady) {
        con.ipadx = ipadx;
        con.ipady = ipady;
        return this;
    }
}
