package cn.dormao.mcpe.parallelserver.protocol;

public class ChunkResponseMetasPacket extends ChunkResponseBlocksPacket {
    @Override
    public byte getPacketId() {
        return PK_CHUNK_RESPONSE_METAS;
    }
}
