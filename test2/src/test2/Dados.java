/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 *
 * @author FREE
 */
public class Dados {
    Long r = null;
    Long g = null;
    Long b = null;
    int x,y;
    
    public Dados(int x,int y){
        this.x = x;
        this.y = y;
    }
    
    public Dados(int tipo,RandomAccessFile reader) throws IOException{
        byte[] bInt = new byte[4];
        byte[] bLong = new byte[8];
        ByteBuffer bbInt = ByteBuffer.wrap(bInt);
        ByteBuffer bbLong = ByteBuffer.wrap(bLong);
        System.out.println("tipo : " + tipo);
        if (tipo==1){
            reader.readLine();
            //reader.skipBytes(25);//8*3+1 3longs e \n
        }else if (tipo==2){
            for (int i=0;i<3;i++){
                reader.read(bLong);
                switch(i){
                    case 0 :
                        r = bbLong.getLong();
                        break;
                    case 1 :
                        g = bbLong.getLong();
                        break;
                    case 2 :
                        b = bbLong.getLong();
                        break;
                    default :
                        throw new UnsupportedOperationException("nao esperava-se chegar aqui");
                }
                bbLong.clear();
            }
            reader.skipBytes(1);//'\n'
        }else{
            throw new IllegalArgumentException("esperava-se 1 ou 2, recebeu : " + tipo);
        }
        reader.read(bInt);
        this.x = bbInt.getInt();
        bbInt.clear();
        reader.read(bInt);
        this.y = bbInt.getInt();
        reader.skipBytes(1);//'\n'
        bbInt.clear();
    }
    
    public void write(RandomAccessFile raf) throws IOException{
        byte[] bInt = new byte[4];
        byte[] bLong = new byte[8];
        ByteBuffer bbInt = ByteBuffer.wrap(bInt);
        ByteBuffer bbLong = ByteBuffer.wrap(bLong);
        int tipo = 0;//tipo 0 vazio tipo 1 preenchido sem long tipo 2 preenchido com long
        if (r==null || g==null || b==null){
            tipo = 1;
            bbInt.putInt(tipo);
            raf.write(bInt);
            bbInt.clear();
            raf.writeBytes("\n");
            raf.writeBytes("longlonglonglonglonglong\n");
        }else{
            tipo = 2;
            bbInt.putInt(tipo);
            raf.write(bInt);
            bbInt.clear();
            bbLong.putLong(r);
            raf.write(bLong);
            bbLong.clear();
            bbLong.putLong(g);
            raf.write(bLong);
            bbLong.clear();
            bbLong.putLong(b);
            raf.write(bLong);
            bbLong.clear();
            raf.writeBytes("\n");
        }
        bbInt.putInt(x);
        System.out.println("x : " + x);
        raf.write(bInt);
        bbInt.clear();
        bbInt.putInt(y);
        System.out.println("y : " + y);
        raf.write(bInt);
        raf.writeBytes("\n");
        raf.close();
    }

    @Override
    public String toString() {
        return "Dados{" + "r=" + r + ", g=" + g + ", b=" + b + ", x=" + x + ", y=" + y + '}';
    }
    
    
}
