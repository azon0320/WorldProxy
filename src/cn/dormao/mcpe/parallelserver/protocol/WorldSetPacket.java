package cn.dormao.mcpe.parallelserver.protocol;

public class WorldSetPacket extends AbstractParallelPacket {
    @Override
    public byte getPacketId() {
        return PK_WORLD_SET;
    }

    public String worldname;

    @Override
    protected byte[] onEncode() {
        return openWriter().writeUTF8(worldname).getBuffer();
    }

    @Override
    public void doDecode(byte[] raw) {
        worldname = openReader(raw).readUTF8();
    }
}
