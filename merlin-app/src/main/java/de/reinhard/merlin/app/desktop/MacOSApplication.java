package de.reinhard.merlin.app.desktop;

import com.apple.eawt.*;
import de.reinhard.merlin.app.javafx.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacOSApplication extends Application {
    private static Logger log = LoggerFactory.getLogger(MacOSApplication.class);

    @SuppressWarnings("deprecation")
    public MacOSApplication() {
        setQuitStrategy(QuitStrategy.CLOSE_ALL_WINDOWS);
        setAboutHandler(new AboutHandler() {
            public void handleAbout(AppEvent.AboutEvent arg0) {
                //MerlinApplication.openAboutDialog();
            }
        });
        setQuitHandler(new QuitHandler() {
            @Override
            public void handleQuitRequestWith(AppEvent.QuitEvent arg0, QuitResponse arg1) {
                //MerlinPFApplication.quitApplication();
            }
        });
    }

    public static void main(String[] args) {
        if (RunningMode.isMacOS()) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            Context.setStartedAsMacOSApp();
            new MacOSApplication();
            // -Xdock:name=ProjectForge
        } else {
            log.warn("Oups, why dosn't Context.isMacOS() return true?");
        }
        //MerlinApplication.openApplication();
    }

}
