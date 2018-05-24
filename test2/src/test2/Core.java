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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
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
        try{
            fw = new FileWriter(f);
        }catch(FileNotFoundException e){
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
        fw.write("size = ");
        fw.write(0);
        fw.write("\n");
        fw.close();
        System.out.println("path existe!");
        boolean sucess = new File(rootFolderName+File.separator+screenshotsName).mkdir();
        if (sucess){
            System.out.println("criando pasta de screenshots");
        }else{
            System.out.println("Pasta de screenshots ja criada");
            adicionarAction();
        }
    }
    
    public void takeAction(){
        File f = new File(rootFolderName+File.separator+configFileName);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            String line;
            line = reader.readLine();
            String[] tokens = line.split(" ");
            int tamanho = Integer.parseInt(tokens[2]);
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void adicionarAction(){
        File f = new File(rootFolderName+File.separator+configFileName);
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(f,"rw");
            int contador = 0;
            try{
                while (true){
                    raf.readByte();
                    contador++;
                }
            }catch(EOFException e){
                System.out.println("fim do arquivo");
            }
            System.out.println("tam : " + contador);
        } catch (Exception ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            if (raf!=null){
                try {
                    raf.close();
                } catch (IOException ex) {
                    Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
                }
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
}
