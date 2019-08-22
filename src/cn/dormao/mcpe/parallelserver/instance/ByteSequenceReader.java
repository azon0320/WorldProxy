package cn.dormao.mcpe.parallelserver.instance;

import cn.dormao.mcpe.parallelserver.ParallelUtil;

import java.nio.charset.StandardCharsets;

public class ByteSequenceReader extends ByteSequence {
    public ByteSequenceReader(byte[] raw){
        buffer = raw;
    }

    public int readASCII(){
        int i = buffer[0] & 0xFF;
        sub(1);
        return i;
    }

    protected int readByteLength(){
        int len = ParallelUtil.byteArrayToShort(new byte[]{buffer[0], buffer[1]});
        sub(2);
        return len;
    }

    public byte[] readBytes(){
        int len = readByteLength();
        byte[] out = new byte[len];
        for (int i=0;i < len;i++){
            out[i] = buffer[i];
        }
        sub(len);
        return out;
    }

    public int readInt(){
        int len = ParallelUtil.byteArrayToInt(new byte[]{buffer[0], buffer[1]});
        sub(2);
        return len;
    }

    @Override
    public byte[] readRaw(int len) {
        byte[] b = new byte[len];
        System.arraycopy(buffer,0,b,0,len);
        sub(len);
        return b;
    }

    public String readUTF8(){
        return new String(readBytes(), StandardCharsets.UTF_8);
    }
}
