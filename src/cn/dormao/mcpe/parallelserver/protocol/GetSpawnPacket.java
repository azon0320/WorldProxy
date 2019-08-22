package cn.dormao.mcpe.parallelserver.protocol;

public class GetSpawnPacket extends AbstractParallelPacket {
    @Override
    public byte getPacketId() {
        return PK_GET_SPAWN;
    }

    @Override
    protected byte[] onEncode() {
        return new byte[0];
    }

    @Override
    public void doDecode(byte[] raw) {

    }
}
