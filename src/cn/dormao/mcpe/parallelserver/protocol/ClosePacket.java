package cn.dormao.mcpe.parallelserver.protocol;

public class ClosePacket extends AbstractParallelPacket {
    @Override
    public byte getPacketId() {
        return PK_CLOSE;
    }

    @Override
    protected byte[] onEncode() {
        return new byte[0];
    }

    @Override
    public void doDecode(byte[] raw) {

    }
}
