package cn.dormao.mcpe.parallelserver.instance;

import cn.dormao.mcpe.parallelserver.ParallelSequence;
import cn.dormao.mcpe.parallelserver.ParallelUtil;

import java.nio.charset.StandardCharsets;

public class ByteSequenceWriter extends ByteSequence {

    public ByteSequenceWriter(){
        buffer = new byte[0];
    }

    public ParallelSequence writeASCII(int dat){
        append(new byte[]{(byte) (dat % 256)});
        return this;
    }

    protected ParallelSequence writeBytesLength(int len){
        append(ParallelUtil.shortToByteArray(len));
        return this;
    }

    public ParallelSequence writeBytes(byte[] raw){
        writeBytesLength(raw.length);
        append(raw);
        return this;
    }

    public ParallelSequence writeInt(int i){
        append(ParallelUtil.intToByteArray(i));
        return this;
    }

    public ParallelSequence writeUTF8(String utf){
        return writeBytes(utf.getBytes(StandardCharsets.UTF_8));
    }
}
