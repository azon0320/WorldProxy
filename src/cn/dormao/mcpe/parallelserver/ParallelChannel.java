package cn.dormao.mcpe.parallelserver;

import org.bukkit.World;

public interface ParallelChannel extends ParallelTickable {

    String getWorldName();

    boolean isAvailable();

    boolean isChannelAvailable();

    void processPacket(byte[] payload);

    void sendPacket(ParallelPacket pk);

    void sendChunk(int chunkx, int chunkz);

    void chunkRequestCallback(ParallelChunk vchunk);

    void worldLoadCallback();

    Parallel parallel();

    int getChannelId();

    void close();
}
