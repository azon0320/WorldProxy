package cn.dormao.mcpe.parallelserver.protocol;

import cn.dormao.mcpe.parallelserver.ParallelUtil;

public class ChunkRequestPacket extends AbstractParallelPacket {
    @Override
    public byte getPacketId() {
        return PK_CHUNK_REQUEST;
    }

    public int chunkx,chunkz;

    @Override
    protected byte[] onEncode() {
        return openWriter().writeUTF8(ParallelUtil.chunkHash(chunkx,chunkz)).getBuffer();
    }

    @Override
    public void doDecode(byte[] raw) {
        int[] nodes = ParallelUtil.achunkHash(openReader(raw).readUTF8());
        chunkx = nodes[0];chunkz = nodes[1];
    }
}
