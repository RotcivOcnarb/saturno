/*
 * Decompiled with CFR 0.139.
 */
package pack;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class SplashPanel extends JPanel implements Runnable {
	private static final long serialVersionUID = 7181078119753445940L;
	BufferedImage img;
    float alpha = 0.0f;
    boolean exit = false;

    public SplashPanel() {
        new Thread(this).start();
        this.setLayout(new GridLayout(1, 1));
        try {
            this.img = (BufferedImage)this.getScaledImage(ImageIO.read(this.getClass().getResourceAsStream("/saturn_icon.png")), 150, 150);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exit() {
        this.exit = true;
    }

    public Image getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, 2);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.setColor(Color.RED);
        g2.drawString("Carregando", 0, 200);
        g2.dispose();
        return resizedImg;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setComposite(AlphaComposite.getInstance(1, 0.0f));
        g2.setColor(new Color(0, 0, 0, 0));
        g2.fillRect(0, 0, 200, 200);
        g2.setFont(new Font("Dialog", 1, 14));
        g2.setComposite(AlphaComposite.getInstance(3, this.alpha));
        g2.drawRenderedImage(this.img, AffineTransform.getTranslateInstance(0.0, 0.0));
        g2.setColor(Color.white);
        g2.drawString("Carregando...", 30, 125);
    }

    @Override
    public void run() {
        do {
            if (!this.exit) {
                this.alpha += 0.01f;
                if (this.alpha > 1.0f) {
                    this.alpha = 1.0f;
                }
            } else {
                this.alpha -= 0.01f;
                if (this.alpha < 0.0f) {
                    this.alpha = 0.0f;
                    this.getParent().setVisible(false);
                }
            }
            this.repaint();
            try {
                Thread.sleep(16L);
                continue;
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }
        } while (true);
    }
}

