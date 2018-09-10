package de.reinhard.merlin.app.desktop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;

public class MainFrame extends JFrame implements ActionListener {
    private Logger log = LoggerFactory.getLogger(MainFrame.class);
    private Main main;

    public MainFrame(Main main) {
        this.main = main;
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                main.stop();
            }
        });
        setTitle("Melin");
        setSize(200, 100);
        //setResizable(false);
        //setLocation(50, 50);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.CENTER;
        JButton button = new JButton("Start");
        button.setActionCommand("start");
        button.addActionListener(this);
        getContentPane().add(button, c);
        setVisible(true);
    }

    private void openBrowser() {
        try {
            Desktop.getDesktop().browse(URI.create(main.getServerUrl()));
        } catch (IOException ex) {
            log.error("Can't open browser: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("start".equals(e.getActionCommand())) {
            openBrowser();
        }
    }
}
