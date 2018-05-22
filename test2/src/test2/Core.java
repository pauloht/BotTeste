/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test2;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Paulo.Tenorio
 */
public class Core{
    /**
     * @param args the command line arguments
     */
    private static final String rootFolderName = "botconfig";
    private static final String configFileName = "config.txt";
    private static final String screenshotsName = "ss";
    
    public void openConfigFile() throws IOException{
        File f = new File(rootFolderName+File.separator+configFileName);
        FileWriter fw;
        boolean folderExist = true;
        try{
            fw = new FileWriter(f);
        }catch(FileNotFoundException e){
            folderExist = false;
            fw = null;
            System.out.println("criando diretorio");
            boolean sucess = f.getParentFile().mkdir();
            if (sucess){
                System.out.println("diretorio criado com successo");
                sucess = f.createNewFile();
                if (sucess){
                    System.out.println("arquivo criado com sucesso");
                    fw = new FileWriter(f);
                }else{
                    System.out.println("falha ao criar arquivo em branco");
                }
            }else{
                System.out.println("falha ao criar diretorio");
            }
        }
        if (fw==null){
            throw new IllegalStateException("falha ao criar arquivo");
        }
        if (folderExist){
            System.out.println("path existe!");
            boolean sucess = new File(rootFolderName+File.separator+screenshotsName).mkdir();
            if (sucess){
                System.out.println("criando pasta de screenshots");
            }else{
                System.out.println("Pasta de screenshots ja criada");
            }
        }
    }
    
    public Core() throws IOException{
        openConfigFile();
    }
    
    public void takeScreenShot(){
        try {
            BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            ImageIO.write(image, "png", new File(rootFolderName+File.separator+screenshotsName+File.separator+"screenshot.png"));
            System.out.println("screenshot tirado");
        } catch (Exception ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) throws AWTException, InterruptedException, IOException {
        // TODO code application logic here
        Core a = new Core();
    }
    
}
