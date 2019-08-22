package cn.dormao.mcpe.parallelserver.general;

import cn.dormao.mcpe.parallelserver.ParallelChunk;
import org.bukkit.Chunk;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

import static cn.dormao.mcpe.parallelserver.ParallelUtil.*;

public class XZYParallelChunk implements ParallelChunk {

    protected byte[] blocks;

    protected byte[] metas;

    protected byte[] biomes;

    protected int chunkX;

    protected int chunkZ;

    protected long lastActive;

    public XZYParallelChunk(int chunkx, int chunkz){
        this.clearAllData();
        this.chunkX = chunkx;this.chunkZ = chunkz;
        chunkActive();
    }

    @Override
    public int getOrder() {
        return ORDER_XZY;
    }

    @Override
    public void clearAllData() {
        blocks = new byte[MAX_X * MAX_Z * this.getMaxHeight()];
        metas = new byte[MAX_X * MAX_Z * this.getMaxHeight()];
        biomes = new byte[MAX_X * MAX_Z];
        for (int i=0;i<biomes.length;i++){
            biomes[i] = (byte) Biome.PLAINS.ordinal();
        }
    }

    @Override
    public int getMaxHeight() {
        return MAX_Y_127;
    }

    @Override
    public int getMaxX() { return MAX_X; }

    @Override
    public int getMaxZ() { return MAX_Z; }

    @Override
    public int getBlockIndex(int x, int y, int z) {
        int indexY = MAX_X * MAX_Z * (y % this.getMaxHeight());
        int indexZ = MAX_Z * (z % MAX_Z);
        return indexY + indexZ + x;
    }

    @Override
    public int getBlockBiomeIndex(int x, int z) {
        int indexZ = MAX_Z * (z % MAX_Z);
        return indexZ + x;
    }

    @Override
    public int getBlockId(int index) {
        return this.blocks[index] & 0xFF;
    }

    @Override
    public int getBlockIdXYZ(int x, int y, int z) {
        return getBlockId(getBlockIndex(x,y,z));
    }

    @Override
    public void setBlockId(int index, int id) {
        chunkActive();
        blocks[index] = (byte) (id % 256);
    }

    @Override
    public int getBlockMeta(int index) {
        return metas[index] & 0xFF;
    }

    @Override
    public int getBlockMetaXYZ(int x, int y, int z) {
        return getBlockMeta(getBlockIndex(x,y,z));
    }

    @Override
    public void setBlockMeta(int index, int meta) {
        chunkActive();
        metas[index] = (byte) (meta % 256);
    }

    @Override
    public int[] getFullBlock(int index) {
        return new int[]{getBlockId(index), getBlockMeta(index)};
    }
    @Override
    public int[] getFullBlockXYZ(int x, int y, int z) {
        return getFullBlock(getBlockIndex(x,y,z));
    }

    @Override
    public void setFullBlock(int index, int id, int meta) {
        chunkActive();
        setBlockId(index,id);setBlockMeta(index,meta);
    }

    @Override
    public void setFullBlockXYZ(int x, int y, int z, int id, int meta) {
        chunkActive();
        setFullBlock(getBlockIndex(x,y,z),id,meta);
    }

    @Override
    public int getBlockBiomeAt(int x, int z) {
        return biomes[getBlockBiomeIndex(x,z)] & 0xFF;
    }

    @Override
    public void setBlockBiomeAt(int x, int z, int biomeId) {
        int index = getBlockBiomeIndex(x,z);
        this.biomes[index] = (byte) (biomeId % 256);
    }

    @Override
    public byte[] getBlocks() {
        chunkActive();
        return blocks;
    }

    @Override
    public void setBlocks(byte[] blocks) {
        chunkActive();
        this.blocks = blocks;
    }

    @Override
    public byte[] getMetas() {
        chunkActive();
        return metas;
    }

    @Override
    public void setMetas(byte[] metas) {
        chunkActive();
        this.metas = metas;
    }

    @Override
    public byte[] getBiomes() {
        return biomes;
    }

    @Override
    public void setBiomes(byte[] biomes) {
        this.biomes = biomes;
    }

    @Override
    public int getChunkX() {
        return chunkX;
    }

    @Override
    public int getChunkZ() {
        return chunkZ;
    }

    public void setChunkX(int chunkX) {
        this.chunkX = chunkX;
    }

    public void setChunkZ(int chunkZ) {
        this.chunkZ = chunkZ;
    }

    @Override
    public void setChunkXZ(int cx, int cz) {
        chunkActive();
        setChunkX(cx);setChunkZ(cz);
    }

    @Override
    public long lastActive() {
        return this.lastActive;
    }

    @Override
    public void chunkActive() {
        this.lastActive = System.currentTimeMillis();
    }

    @Override
    public void fromBukkitChunk(Chunk c) {
        boolean f = true;
        for (int x = 0;x < getMaxX();x++){
            for (int y=0;y < getMaxHeight();y++){
                for (int z=0;z<getMaxZ();z++){
                    Block block = c.getBlock(x,y,z);
                    int[] dat = bukkitGetBlockValue(block);
                    if (dat[0] == 0) f = false;
                    this.setFullBlockXYZ(x,y,z,dat[0],dat[1]);
                    this.setBlockBiomeAt(x,z,block.getBiome().ordinal());
                }
            }
        }
        if (f) System.out.println(chunkHash(c.getX(), c.getZ()) + " blocks all empty..");
    }
}