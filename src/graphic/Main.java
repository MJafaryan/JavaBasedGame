package graphic;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    public Main() {
        setUndecorated(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(new Board());

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        if (gd.isFullScreenSupported()) {
            gd.setFullScreenWindow(this);
        } else {
            setSize(800, 600);
            setVisible(true);
        }
    }

    public static void main(String[] args) {

        new Main();
    }
}