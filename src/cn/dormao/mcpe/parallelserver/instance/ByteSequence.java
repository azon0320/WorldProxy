package cn.dormao.mcpe.parallelserver.instance;

import cn.dormao.mcpe.parallelserver.ParallelSequence;
import cn.dormao.mcpe.parallelserver.ParallelUtil;

public abstract class ByteSequence implements ParallelSequence {

    protected byte[] buffer;

    public ParallelSequence writeASCII(int dat){return this; }

    protected ParallelSequence writeBytesLength(int len){return this; }

    public ParallelSequence writeBytes(byte[] raw){ return this; }

    public ParallelSequence writeUTF8(String utf){ return this; }

    @Override
    public ParallelSequence writeInt(int i) { return this; }

    public int readASCII(){return 0;}

    protected int readByteLength(){return 0;}

    public byte[] readBytes(){return null;}

    public String readUTF8(){return null;}

    @Override
    public int readInt() { return 0; }

    @Override
    public byte[] readRaw(int len) { return new byte[0]; }

    public ParallelSequence sub(){ return sub(1); }
    public ParallelSequence sub(int len){
        buffer = ParallelUtil.subByteArray(buffer,len);
        return this;
    }
    public ParallelSequence append(byte[] add){
        buffer = ParallelUtil.byteArrayAppend(buffer,add);
        return this;
    }

    @Override
    public ParallelSequence writeRaw(byte[] raw) {
        return append(raw);
    }

    public byte[] getBuffer() {
        return buffer;
    }
}
