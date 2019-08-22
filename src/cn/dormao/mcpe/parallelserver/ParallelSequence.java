package cn.dormao.mcpe.parallelserver;

public interface ParallelSequence {
    ParallelSequence writeASCII(int asc);
    ParallelSequence writeBytes(byte[] dat);
    ParallelSequence writeUTF8(String utf);
    ParallelSequence writeInt(int i);
    ParallelSequence writeRaw(byte[] raw);
    ParallelSequence sub(int len);
    ParallelSequence sub();
    ParallelSequence append(byte[] dat);
    byte[] getBuffer();
    int readASCII();
    byte[] readBytes();
    String readUTF8();
    byte[] readRaw(int len);
    int readInt();
}
