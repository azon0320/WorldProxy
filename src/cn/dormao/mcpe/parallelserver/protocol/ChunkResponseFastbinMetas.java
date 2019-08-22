package cn.dormao.mcpe.parallelserver.protocol;

public class ChunkResponseFastbinMetas extends ChunkResponseBlocksPacket {
    @Override
    public byte getPacketId() {
        return PK_CHUNK_RESPONSE_FASTBIN_METAS;
    }
}
