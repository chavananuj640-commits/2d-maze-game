import javax.swing.*;
import java.awt.*;

public class MainFrame {

    public static void main(String[] args) {

        JFrame frame = new JFrame("🦁 Lion Maze Game");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 🌄 BACKGROUND PANEL
        JPanel menuPanel = new JPanel() {

            Image bg = new ImageIcon("page.png").getImage();

            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };

        menuPanel.setLayout(null); // 🔥 IMPORTANT for positioning

        // 🔘 BUTTONS
        Font btnFont = new Font("Arial", Font.BOLD, 20);

        JButton easy = new JButton("🟢 Easy");
        JButton medium = new JButton("🟡 Medium");
        JButton hard = new JButton("🔴 Hard");

        JButton[] buttons = {easy, medium, hard};

        int y = 300;

        for (JButton btn : buttons) {
            btn.setBounds(600, y, 300, 60);
            btn.setFont(btnFont);
            btn.setFocusPainted(false);
            btn.setBackground(new Color(0,0,0,150));
            btn.setForeground(Color.WHITE);
            menuPanel.add(btn);
            y += 100;
        }

        frame.add(menuPanel);
        frame.setVisible(true);

        // 🎮 BUTTON ACTIONS

        easy.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.add(new GamePanel(80, "Easy"));
            frame.revalidate();
            frame.repaint();
        });

        medium.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.add(new GamePanel(40, "Medium"));
            frame.revalidate();
            frame.repaint();
        });

        hard.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.add(new GamePanel(25, "Hard"));
            frame.revalidate();
            frame.repaint();
        });
    }
}