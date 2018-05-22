/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test2;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 *
 * @author Paulo.Tenorio
 */
public class Test2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws AWTException, InterruptedException, IOException {
        // TODO code application logic here
        Robot r = new Robot();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)screenSize.getWidth();
        int height = (int)screenSize.getHeight();
        int count = 0;
        System.out.println("w : " + width + ", h : " + height);
        Random gen = new Random();
        r.setAutoDelay(100);
        for (int i=0;i<10;i++){
            System.out.println("count : " + count);
            r.mouseMove(gen.nextInt(width),gen.nextInt(height));
            BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            ImageIO.write(image, "png", new File("/screenshot"+Integer.toString(i)+".png"));
            count++;
        }
        System.out.println("fim");
        
    }
    
}
