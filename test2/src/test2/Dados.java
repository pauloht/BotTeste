/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test2;

import java.awt.Rectangle;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 *
 * @author FREE
 */
public class Dados {
    byte[] hash = null;
    Rectangle ret;
    int x,y;
    
    public Dados(int x,int y,Rectangle r,byte[] h){
        this.x = x;
        this.y = y;
        this.hash = h;
        this.ret = r;
    }
    
    public Dados(RandomAccessFile reader) throws IOException{
        this.hash = new byte[16];
        byte[] bInt = new byte[4];
        ByteBuffer bbInt = ByteBuffer.wrap(bInt);
        int rx,ry,rw,rh;
        reader.read(bInt);
        rx = bbInt.getInt();
        bbInt.clear();
        reader.read(bInt);
        ry = bbInt.getInt();
        bbInt.clear();
        reader.read(bInt);
        rw = bbInt.getInt();
        bbInt.clear();
        reader.read(bInt);
        rh = bbInt.getInt();
        bbInt.clear();
        this.ret = new Rectangle(rx, ry, rw, rh);
        reader.skipBytes(1);//'\n'
        reader.read(bInt);
        this.x = bbInt.getInt();
        bbInt.clear();
        reader.read(bInt);
        this.y = bbInt.getInt();
        reader.skipBytes(1);//'\n'
        reader.read(this.hash);
        reader.skipBytes(1);
        bbInt.clear();
    }
    
    public void write(RandomAccessFile raf) throws IOException{
        byte[] bInt = new byte[4];
        ByteBuffer bbInt = ByteBuffer.wrap(bInt);
        bbInt.putInt(ret.x);
        System.out.println("retx : " + ret.x);
        raf.write(bInt);
        bbInt.clear();
        bbInt.putInt(ret.y);
        System.out.println("rety : " + ret.y);
        raf.write(bInt);
        bbInt.clear();
        bbInt.putInt(ret.width);
        System.out.println("retwidth : " + ret.width);
        raf.write(bInt);
        bbInt.clear();
        bbInt.putInt(ret.height);
        System.out.println("retheight : " + ret.height);
        raf.write(bInt);
        bbInt.clear();
        raf.writeBytes("\n");
        bbInt.putInt(x);
        System.out.println("x : " + x);
        raf.write(bInt);
        bbInt.clear();
        bbInt.putInt(y);
        System.out.println("y : " + y);
        raf.write(bInt);
        raf.writeBytes("\n");
        raf.write(this.hash);
        raf.writeBytes("\n");
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (byte b : hash){
            s.append((char)b);
        }
        return "Dados{" + "ret=" + ret + ", x=" + x + ", y=" + y + ",array : "+s+'}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }
    
    public static String getHashString(byte[] hash){
        StringBuilder s = new StringBuilder();
        for (byte b : hash){
            s.append((char)b);
        }
        return(s.toString());
    }
    
}
