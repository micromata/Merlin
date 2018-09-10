package de.reinhard.merlin.app.desktop;

import de.reinhard.merlin.app.javafx.Context;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AboutDialog
        extends JDialog {
    private final JButton okButton;

    public AboutDialog(final JFrame frame) {
        super(frame);
        setResizable(false);
        setModal(true);
        setTitle(SwingHelper.getAppTitle());
        GridBagConstraintsHelper con = new GridBagConstraintsHelper();
        getContentPane().setLayout(new GridBagLayout());

        final JLabel imageLabel = SwingHelper.createImageLabel("header-400.png");
        final JPanel buttonBar = new JPanel();
        final FlowLayout layout = new FlowLayout(FlowLayout.CENTER);
        buttonBar.setLayout(layout);
        Context context = Context.instance();
        okButton = new JButton(context.getString("button.ok"));
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        buttonBar.add(okButton);

        int gridy = 0;
        if (imageLabel != null) {
            getContentPane().add(imageLabel, con.set(0, gridy++).setInsets(0, 0, 10, 0).get());
        }
        final JLabel appLabel = new JLabel("<html><span style=\"font-weight: bold; font-size: 16pt;\">"
                + "Merlin"
                + "</span><br>Version "
                + "1.0"
                + "</html>");
        getContentPane().add(appLabel, con.setLabel(0, gridy++).get());
        final JLabel aboutLabel = new JLabel("<html>" + context.getMessage("about", 2018) + "</html>");
        getContentPane().add(aboutLabel, con.setLabel(0, gridy++, GridBagConstraints.NORTH).setWeight(1.0, 1.0, GridBagConstraints.BOTH).get());
        getContentPane().add(buttonBar, con.set(0, gridy++, GridBagConstraints.CENTER).setFill(GridBagConstraints.HORIZONTAL).get());
        pack();
        SwingHelper.centerWindow(frame, this);
        setVisible(true);
    }
}
