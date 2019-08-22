package cn.dormao.mcpe.parallelserver;


import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

public interface ParallelWorld extends ParallelTickable {

    Chunk loadChunk(int chunkx, int chunkz);

    void loadChunkCallback(Chunk bukkitChunk);

    void unloadChunk(int chunkx, int chunkz);

    ParallelChunk getChunk(int x,int z,boolean create);
    ParallelChunk getChunk(int x,int z);

    void setChunk(int x, int z, ParallelChunk chunk);

    Block bukkitGetBlock(int x, int y, int z);

    void bukkitSetBlock(int x, int y, int z, int id, int meta);

    Chunk bukkitGetChunk(int chunkx, int chunkz, boolean load);
    Chunk bukkitGetChunk(int chunkx, int chunkz);

    Chunk bukkitLoadChunk(int chunkx, int chunkz);

    boolean bukkitChunkIsUsing(int chunkx, int chunkz);

    void bukkitUnloadChunk(int chunkx, int chunkz);

    boolean chunkExists(int x, int z);

    String getWorldName();

    World getWorld();

    Vector3 getSpawn();

    void gc();

    Parallel parallel();

    void close();
}
