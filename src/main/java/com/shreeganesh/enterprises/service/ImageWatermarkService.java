/* package com.shreeganesh.enterprises.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

@Service
public class ImageWatermarkService {

    public BufferedImage addTextWatermark(String text, BufferedImage sourceImage) {

        Graphics2D g2d = sourceImage.createGraphics();

        AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.30f);
        g2d.setComposite(alpha);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 50));

        FontMetrics fontMetrics = g2d.getFontMetrics();

        int x = sourceImage.getWidth() - fontMetrics.stringWidth(text) - 20;
        int y = sourceImage.getHeight() - fontMetrics.getHeight() + 60;

        g2d.drawString(text, x, y);
        g2d.dispose();

        return sourceImage;
    }

    public void saveImage(BufferedImage image, String path) throws Exception {
        ImageIO.write(image, "png", new File(path));
    }
}
*/