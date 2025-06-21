package graphic;

import javax.swing.*;
import java.awt.*;

public class Board extends JPanel {
    private Image backgroundImage;

    public Board() {
        setLayout(null);

        backgroundImage = new ImageIcon("vorod.png").getImage();

        JButton loginButton = new JButton();
        loginButton.setBounds(400, 500, 650, 150);
        loginButton.setFocusPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setBorderPainted(false);
        loginButton.setOpaque(false);
        add(loginButton);
        JButton exitButton = new JButton();
        exitButton.setBounds(1400, 0, 100, 100);
        exitButton.setFocusPainted(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(false);
        exitButton.setOpaque(false);
        exitButton.addActionListener(e -> System.exit(0));
        add(exitButton);

        loginButton.addActionListener(e -> showLoginWindow());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }

    private void showLoginWindow() {
        JFrame loginFrame = new JFrame("ورود");
        loginFrame.setSize(300, 200);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("نام کاربری:"));
        panel.add(new JTextField());
        panel.add(new JLabel("رمز عبور:"));
        panel.add(new JPasswordField());
        panel.add(new JButton("ورود"));

        loginFrame.add(panel);
        loginFrame.setVisible(true);
    }
}