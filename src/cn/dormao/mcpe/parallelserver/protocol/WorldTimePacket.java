package cn.dormao.mcpe.parallelserver.protocol;

import cn.dormao.mcpe.parallelserver.instance.ByteSequenceReader;

public class WorldTimePacket extends AbstractParallelPacket {
    @Override
    public byte getPacketId() {
        return PK_WORLD_TIME;
    }

    public int timetick;
    public boolean timerun;

    @Override
    protected byte[] onEncode() {
        return openWriter().writeInt(timetick).writeASCII(timerun ? 1 : 0).getBuffer();
    }

    @Override
    public void doDecode(byte[] raw) {
        ByteSequenceReader in = openReader(raw);
        timetick = in.readInt();
        timerun = in.readASCII() != 0;
    }
}
