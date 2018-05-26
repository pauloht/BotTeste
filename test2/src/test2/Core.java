/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test2;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Paulo.Tenorio
 */
// todo : transformar essa bagaça em singleton
public class Core{
    /**
     * @param args the command line arguments
     */
    private static final String rootFolderName = "botconfig";
    private static final String configFileName = "config.txt";
    private static final String screenshotsName = "ss";
    private static final String caminhoFiltros = rootFolderName+File.separator+"filters";
    private static final String caminhoScreenshot = rootFolderName+File.separator+screenshotsName;
    private static final int entradaSize = 15;
    
    private ArrayList<Dados> dados = new ArrayList<>();
    
    private int numeroAction = 0;
    private static Robot coreRobot = null;
    
    public void openConfigFile() throws IOException{
        File f = new File(rootFolderName+File.separator+configFileName);
        if (f.exists()==false){
            resetFile();
        }
    }
    
    public long[] processarImagem(BufferedImage img,BufferedImage filter){
        if (img.getHeight()!=filter.getHeight() || img.getWidth()!=filter.getWidth()){
            throw new IllegalArgumentException("Imagens de tamanhos difentes!");
        }
        int h = img.getHeight();
        int w = img.getWidth();
        long[] rgb = new long[3];
        int red;
        int blue;
        int green;
        for (int i=0;i<h;i++){
            for (int j=0;j<w;j++){
                Color c = new Color(img.getRGB(j, i));
                red = c.getRed();
                blue = c.getBlue();
                green = c.getGreen();
                rgb[0] += red;
                rgb[1] += green;
                rgb[2] += blue;
            }
        }
        return(rgb);
    }
    
    public void resetFile() throws IOException{
        System.out.println("reseting.....");
        File f = new File(rootFolderName+File.separator+configFileName);
        if (f.exists()==false){
            f.getParentFile().mkdirs();
            f.createNewFile();
        }
        File ssF = new File(caminhoScreenshot);
        File filter = new File(caminhoFiltros);
        if (filter.exists()==false){
            filter.mkdirs();
        }
        if (ssF.exists()){
            String[]entries = ssF.list();
            for(String s: entries){
                File currentFile = new File(ssF.getPath(),s);
                String prefixo = s.substring(0, 2);
                String sufixo = s.substring(s.length()-3,s.length());
                if (prefixo.equals("ss") && sufixo.equals("png")){
                    currentFile.delete();
                }
            }
        }else{
            ssF.mkdirs();
        }
        BufferedOutputStream writer = null;
        try {
            writer = new BufferedOutputStream(new FileOutputStream(f));
            byte[] bInt = new byte[4];
            ByteBuffer bb = ByteBuffer.wrap(bInt);
            bb.putInt(0);
            writer.write(bb.array());
            writer.write(new String("\n").getBytes());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            if (writer!=null){
                try {
                    writer.close();
                } catch (IOException ex) {
                    Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public void carregarDadosDeDisco(){
        System.out.println("carregando dados de disco...");
        File f = new File(rootFolderName+File.separator+configFileName);
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(f,"r");
            byte[] bInt = new byte[4];
            ByteBuffer bbInt = ByteBuffer.wrap(bInt);
            raf.read(bInt);
            int tam = bbInt.getInt();
            bbInt.clear();
            System.out.println("tam: " + tam);
            raf.skipBytes(1);
            for (int i=0;i<tam;i++){
                raf.read(bInt);
                raf.skipBytes(1);
                int tipo = bbInt.getInt();
                bbInt.clear();
                Dados novoDado = new Dados(tipo,raf);
                dados.add(novoDado);
                System.out.println("dado carregado : " + novoDado);
            }
            raf.close();
        } catch (IOException ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void takeAction(){
        throw new UnsupportedOperationException("todo");
    }
    
    public Dados adicionarAction(){
        System.out.println("adicionando action....");
        File f = new File(rootFolderName+File.separator+configFileName);
        RandomAccessFile raf = null;
        Point p = MouseInfo.getPointerInfo().getLocation();
        try {
            raf = new RandomAccessFile(f,"rwd");
            byte[] bInt = new byte[4];
            ByteBuffer bb = ByteBuffer.wrap(bInt);
            raf.read(bInt);
            int tam = bb.getInt();
            System.out.println("tam antigo : " + tam);
            bb.clear();
            bb.putInt((tam+1));
            raf.seek(0);
            raf.write(bInt);
            raf.seek(f.length());
            String ssNameAppend = "_n"+Integer.toString(tam+1);
            takeScreenShot("ss"+ssNameAppend+".png");
            bb.clear();
            Dados d = new Dados(p.x, p.y);
            d.write(raf);
            raf.close();
            System.out.println("dado adicionado : " + d);
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
        return(null);
    }
    
    public Core() throws IOException{
        try {
            coreRobot = new Robot();
        } catch (AWTException ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        }
        openConfigFile();
        carregarDadosDeDisco();
    }
    
    public long takeScreenShot(String name){
        try {
            System.out.println("screenshot tirado");
            BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            ImageIO.write(image, "png", new File(rootFolderName+File.separator+screenshotsName+File.separator+name));
        } catch (Exception ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        }
        return(-1);
    } 

    void updateValores() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
