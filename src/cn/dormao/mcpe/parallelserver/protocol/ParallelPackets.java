package cn.dormao.mcpe.parallelserver.protocol;

import cn.dormao.mcpe.parallelserver.ParallelPacket;

public class ParallelPackets {
    private ParallelPackets(){}
    public static ParallelPacket getPacket(byte[] dat){
        return AbstractParallelPacket.getPacket(dat);
    }
}
