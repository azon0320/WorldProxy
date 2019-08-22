package cn.dormao.mcpe.parallelserver.protocol;

import cn.dormao.mcpe.parallelserver.ParallelUtil;
import cn.dormao.mcpe.parallelserver.instance.ByteSequenceReader;

public class ChunkResponseBlocksPacket extends AbstractParallelPacket {
    @Override
    public byte getPacketId() {
        return PK_CHUNK_RESPONSE_BLOCKS;
    }

    public int chunkx,chunkz;
    public byte[] payload;

    @Override
    public boolean requireSendIndependent() {
        return true;
    }

    @Override
    protected byte[] onEncode() {
        return openWriter()
                .writeUTF8(ParallelUtil.chunkHash(chunkx,chunkz))
                .writeBytes(payload).getBuffer();
    }

    @Override
    public void doDecode(byte[] raw) {
        ByteSequenceReader in = openReader(raw);
        int[] nodes = ParallelUtil.achunkHash(in.readUTF8());
        chunkx = nodes[0];chunkz = nodes[1];
        payload = in.readBytes();
    }
}
