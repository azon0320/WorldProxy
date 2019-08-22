package cn.dormao.mcpe.parallelserver.general;

import cn.dormao.mcpe.parallelserver.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPlaceEvent;

import static cn.dormao.mcpe.parallelserver.ParallelUtil.*;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SimpleParallelWorld implements ParallelWorld {

    protected Map<String, ParallelChunk> chunks;

    protected String worldname;

    protected Parallel reference;

    public SimpleParallelWorld(String worldname, Parallel ref){
        this.worldname = worldname;
        this.reference = ref;
        chunks = new HashMap<>();
    }

    public World getWorld(){
        World world = null;
        if (getWorldName() != null){
            world = parallel().bukkit().getWorld(getWorldName());
        }
        return world;
    }

    @Override
    public Block bukkitGetBlock(int x, int y, int z) {
        return getWorld() != null ? getWorld().getBlockAt(x, y, z) : null;
    }

    @SuppressWarnings("all")
    @Override
    public void bukkitSetBlock(int x, int y, int z, int id, int meta) {
        Block block = bukkitGetBlock(x,y,z);
        if (block != null) {
            block.setTypeIdAndData(id,(byte)meta,true);
            //block.getWorld().spawnParticle(Particle.BLOCK_CRACK,x,y,z,1,block.getState().getData());
        }
    }

    @Override
    public Chunk bukkitGetChunk(int chunkx, int chunkz, boolean load) {
        World w = getWorld();
        Chunk out = null;
        if (w != null){
            out = bukkitGetWorldChunk(chunkx, chunkz, w.getLoadedChunks());
            if (out == null && load){
                w.loadChunk(chunkx, chunkz);
                out = bukkitGetWorldChunk(chunkx, chunkz, w.getLoadedChunks());
            }
        }
        return out;
    }

    @Override
    public Chunk bukkitGetChunk(int chunkx, int chunkz) {
        return bukkitGetChunk(chunkx, chunkz, true);
    }

    @Override
    public Chunk bukkitLoadChunk(int chunkx, int chunkz) {
        World w = getWorld();
        Chunk c = null;
        if (w != null){
            w.loadChunk(chunkx, chunkz);
            c = bukkitGetChunk(chunkx, chunkz, false);
        }
        return c;
    }

    @Override
    public void bukkitUnloadChunk(int chunkx, int chunkz) {
        World w = getWorld();
        if (w != null) w.unloadChunk(chunkx, chunkz);
    }

    @Override
    public boolean bukkitChunkIsUsing(int chunkx, int chunkz) {
        World w = getWorld();
        boolean f = true;
        if (w != null){
            f = w.isChunkInUse(chunkx, chunkz);
        }
        return f;
    }

    @Override
    public ParallelChunk getChunk(int x, int z, boolean create) {
        ParallelChunk chunk = chunks.get(chunkHash(x,z));
        if (chunk == null && create){
            loadChunk(x, z);
            chunk = chunks.get(chunkHash(x,z));
        }
        return chunk;
    }

    @Override
    public ParallelChunk getChunk(int x, int z) {
        return getChunk(x, z, true);
    }

    @Override
    public void setChunk(int x, int z, ParallelChunk chunk) {
        unloadChunk(x, z);
        chunks.put(chunkHash(x, z), chunk);
    }

    @Override
    public boolean chunkExists(int x, int z) {
        return chunks.containsKey(chunkHash(x,z));
    }


    @Override
    public Chunk loadChunk(int chunkx, int chunkz) {
        String wname = getWorldName();
        Chunk c = null;
        if (wname != null){
            World world = parallel().bukkit().getWorld(getWorldName());
            if (world != null){
                if (bukkitGetChunk(chunkx,chunkz,false) == null){
                    world.loadChunk(chunkx, chunkz);
                }else {
                    c = bukkitGetWorldChunk(chunkx, chunkz, world.getLoadedChunks());
                    loadChunkCallback(c);
                }
            }
        }
        return c;
    }

    @Override
    public void loadChunkCallback(Chunk bukkitChunk) {
        int chunkx = bukkitChunk.getX(), chunkz = bukkitChunk.getZ();
        ParallelChunk vchunk = new XZYParallelChunk(chunkx, chunkz);
        vchunk.fromBukkitChunk(bukkitChunk);
        setChunk(chunkx,chunkz,vchunk);
        for (ParallelChannel ch : parallel().getChannelsByWorld(getWorldName())){
            ch.chunkRequestCallback(vchunk);
        }
    }

    @Override
    public Vector3 getSpawn() {
        String wname = getWorldName();
        if (wname != null){
            World world = parallel().bukkit().getWorld(getWorldName());
            if (world != null){
                Location bukkitloc = world.getSpawnLocation();
                return new Vector3(
                        (float) bukkitloc.getX(),
                        (float)  bukkitloc.getY(),
                        (float) bukkitloc.getZ()
                );
            }
        }
        return new Vector3(0,125,0);
    }

    @Override
    public void unloadChunk(int chunkx, int chunkz) {
        chunks.remove(chunkHash(chunkx, chunkz));
    }

    @Override
    public boolean onTick(int currentTick) {
        return true;
    }

    @Override
    public void gc() {
        int count = 0,vcount = 0;
        World w = getWorld();
        if (w != null) {
            for (ParallelChunk chunk : new ArrayList<>(chunks.values())){
                if (System.currentTimeMillis() - chunk.lastActive() >= 1000 * 60 * 2) {
                    unloadChunk(chunk.getChunkX(), chunk.getChunkZ());
                    vcount++;
                    if (!w.isChunkInUse(chunk.getChunkX(), chunk.getChunkZ())) {
                        w.unloadChunk(chunk.getChunkX(), chunk.getChunkZ());
                        count++;
                    }
                }
            }
            /* 不确定是否系统自动清理Chunk
            for(Chunk bukkitchunk : w.getLoadedChunks()){
                if (w.isChunkInUse(bukkitchunk.getX(), bukkitchunk.getZ())){
                    bukkitUnloadChunk(bukkitchunk.getX(), bukkitchunk.getZ());
                    count++;
                }
            }
             */
        }
        if (count != 0 || vcount != 0) {
            parallel().bukkit().getLogger().info(String.format(
                    Locale.ENGLISH,
                    "[%s] Unload chunk %d , vchunk %d",
                    getWorldName(), count, vcount
            ));
        }
    }

    @Override
    @Deprecated
    public void close() {}

    @Override
    public Parallel parallel() {
        return reference;
    }

    public String getWorldName() {
        return worldname;
    }
}
