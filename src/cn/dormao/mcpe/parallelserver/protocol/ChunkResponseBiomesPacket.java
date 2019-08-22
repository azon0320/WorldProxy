package cn.dormao.mcpe.parallelserver.protocol;

public class ChunkResponseBiomesPacket extends ChunkResponseBlocksPacket {
    @Override
    public byte getPacketId() {
        return PK_CHUNK_RESPONSE_BIOMES;
    }
}
