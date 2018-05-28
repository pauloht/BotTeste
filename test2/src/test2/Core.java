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
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
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
    private static final String nomeFiltroBase = "ss";
    private static final String caminhoFiltros = rootFolderName+File.separator+"filters";
    private static final String caminhoScreenshot = rootFolderName+File.separator+screenshotsName;
    private static final String caminhoPlanos = rootFolderName+File.separator+"planos";
    private static final int entradaSize = 15;
    
    private ArrayList<Dados> localdados = new ArrayList<>();
    
    private int numeroAction = 0;
    private static Robot coreRobot = null;
    Interface gui = null;
    private int contadorGui = 0;
    
    public void updateInterface(String msg){
        System.out.println("msg : " + msg);
        if (gui!=null){
            contadorGui++;
            gui.setMessage(Integer.toString(contadorGui) + "-" + msg);
        }
    }
    
    public void openConfigFile() throws IOException{
        File f = new File(rootFolderName+File.separator+configFileName);
        if (f.exists()==false){
            resetFile();
        }
    }
    
    /*
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
                Color cFilter = new Color(filter.getRGB(j, i));
                if (cFilter.equals(Color.WHITE)){
                    red = c.getRed();
                    blue = c.getBlue();
                    green = c.getGreen();
                    rgb[0] += red;
                    rgb[1] += green;
                    rgb[2] += blue;
                }
            }
        }
        return(rgb);
    }
    // */
    
    private void setGreyScale(BufferedImage img){
    for (int i=0;i<img.getHeight();i++){
        for (int j=0;j<img.getWidth();j++){
                Color c = new Color(img.getRGB(j, i));
                int red = c.getRed();
                int green = c.getGreen();
                int blue = c.getBlue();
                int novaCor = (red + green + blue)/3;
                red = novaCor;
                green = novaCor;
                blue = novaCor;
                int rgb = 0xff000000 | (red << 16) | (green << 8) | blue;
                img.setRGB(j, i, rgb);
            }
        }
    }
    
    public byte[] getImageHash(BufferedImage img) throws IOException, NoSuchAlgorithmException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", baos);
        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
        digest.update(baos.toByteArray());
        byte[] hash = digest.digest();
        return(hash);
    }
    
    public long getImageHash2(BufferedImage img) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i=0;i<img.getHeight();i++){
            for (int j=0;j<img.getWidth();j++){
                Color c = new Color(img.getRGB(j, i));
                int red = c.getRed();
                int green = c.getGreen();
                int blue = c.getBlue();
                int novaCor = (red + green + blue)/3;
                red = novaCor;
                green = novaCor;
                blue = novaCor;
                int rgb = 0xff000000 | (red << 16) | (green << 8) | blue;
                img.setRGB(j, i, rgb);
            }
        }
        ImageIO.write(img, "png", baos);
        byte[] bytes = baos.toByteArray();
        Checksum checksum = new CRC32();
        checksum.update(bytes, 0, bytes.length);
        return(checksum.getValue());
    }
        
    public void resetFile() throws IOException{
        updateInterface("reseting.....");
        localdados = new ArrayList<>();
        File f = new File(rootFolderName+File.separator+configFileName);
        if (f.exists()==false){
            f.getParentFile().mkdirs();
            f.createNewFile();
        }
        File filter = new File(caminhoFiltros);
        if (filter.exists()==false){
            filter.mkdirs();
        }
        File plansFolder = new File(caminhoPlanos);
        if (plansFolder.exists()==false){
            plansFolder.mkdirs();
        }
        File ssF = new File(caminhoScreenshot);
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
    
    private ArrayList getDadosDeFile(File f){
        if (f.exists()){
            ArrayList<Dados> retorno = new ArrayList<>();
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
                    Dados novoDado = new Dados(raf);
                    retorno.add(novoDado);
                }
                raf.close();
            } catch (IOException ex) {
                Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
            }
            return(retorno);
        }
        return(null);
    }
    
    public void carregamentoAoIniciar(){
        System.out.println("carregando dados de disco...");
        File f = new File(rootFolderName+File.separator+configFileName);
        this.localdados = getDadosDeFile(f);
    }
    
    public void takeAction(){
        throw new UnsupportedOperationException("todo");
    }
    
    public Dados adicionarAction(Point mousePoint,Rectangle ret){
        updateInterface("adicionando action");
        File f = new File(rootFolderName+File.separator+configFileName);
        RandomAccessFile raf = null;
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
            BufferedImage image = new Robot().createScreenCapture(ret);
            setGreyScale(image);
            ImageIO.write(image, "png", new File(caminhoScreenshot+File.separator+"ss"+Integer.toString(tam+1)+".png"));
            byte[] hash = getImageHash(image);
            bb.clear();
            Dados d = new Dados(mousePoint.x, mousePoint.y,ret,hash);
            d.write(raf);
            localdados.add(d);
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
    
    public void executarPlano(File f) throws FileNotFoundException, IOException, AWTException, InterruptedException, NoSuchAlgorithmException{
        BufferedReader reader = null;
        try{
        if (f.exists()){
            updateInterface("Iniciando plano");
            ArrayList<Dados> dados = null;
            reader = new BufferedReader(new FileReader(f));
            String lineRead = null;
            String[] tokens = null;
            Integer tempoEntreTransicao = null;
            Integer numeroDeRepeticoes = null;
            Integer estadoBuffer = null;
            boolean inicioPlanos = false;
            boolean repeat = false;
            int repeatCounter = 0;
            coreRobot.setAutoDelay(100);
            int args1 = 0;
            int xTemp,yTemp;
            double delta;
            Double aux2;
            String acao2 = null;
            File fDados = null;
            boolean forking = false;
            String filePath = null;
            while (true){
                if (repeat==false){
                    repeatCounter = 0;
                    lineRead = reader.readLine();
                    if (lineRead.equals("fim")){
                        break;
                    }
                    tokens = lineRead.split(" ");
                    acao2 = tokens[tokens.length-1];
                }
                repeat = false;
                if (inicioPlanos){
                    int novoEstado = 1000;
                    byte[] hash = null;
                    Dados dado = null;
                    int mouse1 = InputEvent.BUTTON1_DOWN_MASK;
                    if (forking){
                        novoEstado = Integer.parseInt(tokens[tokens.length-2]);
                        dado = dados.get(novoEstado-1);
                        System.out.println("executando forking para " + novoEstado);
                    }else{
                        novoEstado = Integer.parseInt(tokens[0]);
                        dado = dados.get(novoEstado-1);
                        BufferedImage image = new Robot().createScreenCapture(dado.ret);
                        setGreyScale(image);
                        hash = getImageHash(image);
                        System.out.println("executando " + novoEstado);
                    }
                    if (forking==true || Arrays.equals(hash,dado.hash)){
                        String acao = tokens[1];
                        if (forking){
                            forking = false;
                            repeat = true;
                            acao = "click";
                            System.out.println("executando forking");
                        }
                        switch (acao){
                            case "dragx" :
                                args1 = Integer.parseInt(tokens[2]);
                                updateInterface("draging");
                                delta = (args1+0.0)/(10-1.0);
                                coreRobot.mouseMove(dado.x, dado.y);
                                coreRobot.mousePress(mouse1);
                                for (int w=1;w<9;w++){
                                    aux2 = dado.x+delta*w;
                                    coreRobot.mouseMove(aux2.intValue(), dado.y);
                                    Thread.sleep(100);
                                }
                                coreRobot.mouseMove(dado.x+args1, dado.y);
                                coreRobot.mouseRelease(mouse1);
                                break;
                            case "click" :
                                coreRobot.mouseMove(dado.x, dado.y);
                                coreRobot.mousePress(mouse1);
                                coreRobot.mouseRelease(mouse1);
                                break;
                            default :
                                throw new UnsupportedOperationException("Nao feito operacao para "+acao);
                        }
                        //coreRobot.mouseMove(dado.x, dado.y);
                        //coreRobot.mousePress(tecla);
                        //coreRobot.mouseRelease(tecla);
                        //coreRobot.mousePress(tecla);
                        //coreRobot.mouseRelease(tecla);
                    }else{
                        //System.out.println("hash1 : " + Dados.getHashString(hash) + "\nhash2 : " + Dados.getHashString(dado.hash));
                        System.out.println("falha");
                        if (repeatCounter >= numeroDeRepeticoes){
                            //ImageIO.write(image, "png", new File(caminhoScreenshot+File.separator+"temp.png"));
                            System.out.println("nao pode fazer transicao!");
                            throw new UnsupportedOperationException("todo aqui deveria tirar screenshot e colocar numa pasta erros");
                        }else{
                            System.out.println("repetindo : " + repeatCounter);
                            repeat = true;
                            switch (acao2){
                                case "tryAgain" :
                                    //donothing
                                    System.out.println("tentando denovo");
                                    break;
                                case "execute" :
                                    System.out.println("forking");
                                    forking = true;
                                    break;
                                default : 
                                    throw new UnsupportedOperationException("Sem caso para : "+acao2);
                            }
                            repeatCounter++;
                        }
                    }
                    Thread.sleep(tempoEntreTransicao);
                }else{
                    switch(tokens[0]){
                        case "repeat:":
                            numeroDeRepeticoes = Integer.parseInt(tokens[1]);
                            break;
                        case "waitTime:":
                            tempoEntreTransicao = Integer.parseInt(tokens[1]);
                            break;
                        case "inicio":
                            inicioPlanos = true;
                            break;
                        case "import":
                            filePath = caminhoPlanos+File.separator+tokens[1];
                            fDados = new File(filePath);
                            if (fDados.exists()){
                                dados = getDadosDeFile(fDados);
                            }else{
                                throw new FileNotFoundException("File deveria existir");
                            }
                            break;
                        case "exec":
                            filePath = caminhoPlanos+File.separator+tokens[1];
                            fDados = new File(filePath);
                            if (fDados.exists()){
                                executarPlano(fDados);
                            }else{
                                throw new FileNotFoundException("File deveria existir");
                            }
                            break;
                        default :
                            throw new IllegalArgumentException("erro ao ler token : " + tokens[0]);
                    }
                }
            }
            System.out.println("fim plano");
            updateInterface("fim plano");
        }else{
            System.out.println("arquivo nao existe lol");
        }
        }catch(Exception e){
            if (reader!=null){
                reader.close();
            }
            throw e;
        }finally{
            if (reader!=null){
                reader.close();
            }
        }
    }
    
    public Core(Interface gui) throws IOException{
        try {
            coreRobot = new Robot();
            this.gui = gui;
        } catch (AWTException ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        }
        openConfigFile();
        carregamentoAoIniciar();
    }
    
    public long takeScreenShot(String name,Rectangle ret){
        try {
            System.out.println("screenshot tirado");
            BufferedImage image = new Robot().createScreenCapture(ret);
            ImageIO.write(image, "png", new File(rootFolderName+File.separator+screenshotsName+File.separator+name));
        } catch (Exception ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        }
        return(-1);
    } 
    
    public void test(String nome){
        File plano = new File(caminhoPlanos+File.separator+nome);
        try {
            Thread.sleep(2000);
            executarPlano(plano);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AWTException | InterruptedException | NoSuchAlgorithmException ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            
        }
    }
    
    public void test2(){
        File f1 = new File(caminhoScreenshot+File.separator+"ss3.png");
        File f2 = new File(caminhoScreenshot+File.separator+"temp.png");
        try {
            BufferedImage img1 = ImageIO.read(f1);
            BufferedImage img2 = ImageIO.read(f2);
            byte[] h1 = getImageHash(img1);
            byte[] h2 = getImageHash(img2);
            long h11 = getImageHash2(img1);
            long h22 = getImageHash2(img2);
            System.out.println("hash 1 : " + Dados.getHashString(h1));
            System.out.println("hash 2 : " + Dados.getHashString(h2));
            System.out.println("hash 11 : " + h11);
            System.out.println("hash 22 : " + h22);
            int iguais = 0;
            int diferentes = 0;
            for (int i=0;i<img1.getHeight();i++){
                for (int j=0;j<img1.getWidth();j++){
                    int cor1 = img1.getRGB(j, i);
                    int cor2 = img2.getRGB(j, i);
                    if (cor1==cor2){
                        iguais++;
                    }else{
                        diferentes++;
                        System.out.println("cor1 : " + cor1 + ",cor2 : " + cor2);
                    }
                }
            }
            System.out.println("iguais : " + iguais + ", diferentes : " + diferentes);
        } catch (IOException ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void updateValores() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
