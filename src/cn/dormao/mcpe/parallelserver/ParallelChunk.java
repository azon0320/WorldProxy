package cn.dormao.mcpe.parallelserver;

import org.bukkit.Chunk;

public interface ParallelChunk {

    int ORDER_XZY = 0;
    int MAX_Y_127 = 127;
    int MAX_X = 16;
    int MAX_Z = 16;

    void clearAllData();

    int getOrder();

    int getBlockIndex(int x, int y, int z);
    int getBlockBiomeIndex(int x, int z);

    int getMaxHeight();
    int getMaxX();
    int getMaxZ();

    int getBlockId(int index);
    int getBlockIdXYZ(int x,int y,int z);

    void setBlockId(int index, int id);

    int getBlockMeta(int index);
    int getBlockMetaXYZ(int x, int y, int z);

    void setBlockMeta(int index, int meta);

    int[] getFullBlock(int index);
    int[] getFullBlockXYZ(int x, int y, int z);

    void setFullBlock(int index, int id, int meta);
    void setFullBlockXYZ(int x,int y, int z, int id, int meta);

    void setBlockBiomeAt(int x, int z, int biomeId);
    int getBlockBiomeAt(int x, int z);

    byte[] getBlocks();
    void setBlocks(byte[] b);

    byte[] getMetas();
    void setMetas(byte[] b);

    byte[] getBiomes();
    void setBiomes(byte[] biomes);

    int getChunkX();

    int getChunkZ();

    void setChunkXZ(int cx, int cz);

    long lastActive();

    void chunkActive();

    void fromBukkitChunk(Chunk c);
}
