package graphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        // بستن پنجره فعلی (پنجره اصلی)
        Window mainWindow = SwingUtilities.getWindowAncestor(this);
        if (mainWindow != null) {
            mainWindow.dispose();
        }

        // ساخت پنجره جدید برای لاگین
        JFrame loginFrame = new JFrame();
        loginFrame.setUndecorated(true); // حذف نوار بالا
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // تمام صفحه
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        if (gd.isFullScreenSupported()) {
            gd.setFullScreenWindow(loginFrame);
        } else {
            loginFrame.setSize(800, 600);
            loginFrame.setVisible(true);
        }

        // بارگذاری پس‌زمینه
        Image loginBackground = new ImageIcon("login.png").getImage();

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(loginBackground, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(null);

        JTextField userField = new JTextField();
        userField.setBounds(470, 490, 480, 50);
        userField.setOpaque(false);
        userField.setBackground(new Color(0,0,0,0));
        userField.setBorder(BorderFactory.createEmptyBorder());
        userField.setFont(new Font("Serif", Font.PLAIN, 25));
        panel.add(userField);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(470, 612, 480, 50);
        passField.setOpaque(false);
        passField.setBorder(BorderFactory.createEmptyBorder());
        passField.setBackground(new Color(0,0,0,0));
        passField.setFont(new Font("Serif", Font.PLAIN, 25));
        panel.add(passField);

        JButton loginBtn = new JButton();
        loginBtn.setBounds(490, 690, 460, 70);
        loginBtn.setFocusPainted(false);
        loginBtn.setContentAreaFilled(false);
        loginBtn.setBorderPainted(false);
        panel.add(loginBtn);

        JButton registerBtn = new JButton();
        registerBtn.setBounds(650, 790, 140, 50);
        registerBtn.setFocusPainted(false);
        registerBtn.setContentAreaFilled(false);
        registerBtn.setBorderPainted(false);
        panel.add(registerBtn);

        registerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRegisterWindow();
            }
        });

        JButton exitBtn = new JButton();
        exitBtn.setBounds(1490, 0, 50, 50);
        exitBtn.setFocusPainted(false);
        exitBtn.setContentAreaFilled(false);
        exitBtn.setBorderPainted(false);
        exitBtn.setOpaque(false);
        exitBtn.addActionListener(e -> System.exit(0));
        panel.add(exitBtn);


        loginFrame.setContentPane(panel);
        loginFrame.setVisible(true);
    }

    private void showRegisterWindow() {
        Window loginWindow = SwingUtilities.getWindowAncestor(this);
        if (loginWindow != null) {
            loginWindow.dispose();
        }
        JFrame registerFrame = new JFrame();
        registerFrame.setUndecorated(true);
        registerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        if (gd.isFullScreenSupported()) {
            gd.setFullScreenWindow(registerFrame);
        } else {
            registerFrame.setSize(800, 600);
            registerFrame.setVisible(true);
        }

        Image registerBackground = new ImageIcon("register.png").getImage();

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(registerBackground, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(null);

        JTextField userField = new JTextField();
        userField.setBounds(460, 412, 500, 35);
        userField.setOpaque(false);
        userField.setBorder(BorderFactory.createEmptyBorder());
        userField.setFont(new Font("Serif", Font.PLAIN, 25));
        panel.add(userField);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(460, 468, 500, 35);
        passField.setOpaque(false);
        passField.setBorder(BorderFactory.createEmptyBorder());
        passField.setFont(new Font("Serif", Font.PLAIN, 25));
        panel.add(passField);

        JPasswordField confirmPassField = new JPasswordField();
        confirmPassField.setBounds(460, 525, 500, 35);
        confirmPassField.setOpaque(false);
        confirmPassField.setBorder(BorderFactory.createEmptyBorder());
        confirmPassField.setFont(new Font("Serif", Font.PLAIN, 25));
        panel.add(confirmPassField);

        JTextField userField2 = new JTextField();
        userField2.setBounds(460, 580, 500, 35);
        userField2.setOpaque(false);
        userField2.setBorder(BorderFactory.createEmptyBorder());
        userField2.setFont(new Font("Serif", Font.PLAIN, 25));
        panel.add(userField2);

        // افزودن چک باکس‌ها
        JCheckBox iran = new JCheckBox("iran");
        iran.setBounds(470, 700, 100, 30);
        iran.setOpaque(false);
        iran.setFont(new Font("Serif", Font.PLAIN, 25));
        panel.add(iran);

        JCheckBox arabic = new JCheckBox("arab");
        arabic.setBounds(610, 700, 100, 30);
        arabic.setOpaque(false);
        arabic.setFont(new Font("Serif", Font.PLAIN, 25));
        panel.add(arabic);

        JCheckBox mongolian = new JCheckBox("mongol");
        mongolian.setBounds(735, 700, 100, 30);
        mongolian.setFont(new Font("Serif", Font.PLAIN, 25));
        mongolian.setOpaque(false);
        panel.add(mongolian);

        JCheckBox roman = new JCheckBox("roma");
        roman.setBounds(880, 700, 100, 30);
        roman.setFont(new Font("Serif", Font.PLAIN, 25));
        roman.setOpaque(false);
        panel.add(roman);

        // افزودن دکمه ثبت‌نام
        JButton registerButton = new JButton();
        registerButton.setBounds(520, 775, 400, 50);
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setBorder(BorderFactory.createEmptyBorder());
        registerButton.setFont(new Font("Serif", Font.PLAIN, 25));
        panel.add(registerButton);

        JButton exitButton = new JButton();
        exitButton.setBounds(1470, 0, 60, 60);
        exitButton.setFocusPainted(false);
        exitButton.setBorderPainted(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setBorder(BorderFactory.createEmptyBorder());

        JButton infoIran = new JButton();
        infoIran.setBounds(460, 635, 100, 60);
        infoIran.setFocusPainted(false);
        infoIran.setBorderPainted(false);
        infoIran.setContentAreaFilled(false);
        infoIran.setBorder(BorderFactory.createEmptyBorder());
        panel.add(infoIran);
        JButton infoArabic = new JButton();
        infoArabic.setBounds(600, 635, 100, 60);
        infoArabic.setFocusPainted(false);
        infoArabic.setBorderPainted(false);
        infoArabic.setContentAreaFilled(false);
        infoArabic.setBorder(BorderFactory.createEmptyBorder());
        panel.add(infoArabic);
        JButton infoMongolian = new JButton();
        infoMongolian.setBounds(740, 635, 100, 60);
        infoMongolian.setFocusPainted(false);
        infoMongolian.setBorderPainted(false);
        infoMongolian.setContentAreaFilled(false);
        infoMongolian.setBorder(BorderFactory.createEmptyBorder());
        panel.add(infoMongolian);
        JButton infoRoman = new JButton();
        infoRoman.setBounds(870, 635, 100, 60);
        infoRoman.setFocusPainted(false);
        infoRoman.setBorderPainted(false);
        infoRoman.setContentAreaFilled(false);
        infoRoman.setBorder(BorderFactory.createEmptyBorder());
        panel.add(infoRoman);
        panel.add(exitButton);
        exitButton.addActionListener(e -> {
            System.exit(0);
        });


        registerFrame.setContentPane(panel);
        registerFrame.setVisible(true);
    }
}