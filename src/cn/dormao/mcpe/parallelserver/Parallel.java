package cn.dormao.mcpe.parallelserver;

import org.bukkit.Server;
import org.bukkit.World;

import java.util.Collection;

public interface Parallel extends ParallelTickable{

    int allocateChannelId();

    Collection<ParallelWorld> getParallelWorlds();

    ParallelWorld getParallelWorld(String worldname);

    boolean existBukkitWorldInstance(String worldname);

    boolean loadParallelWorld(String worldname);

    boolean existBukkitWorld(String worldname);

    Collection<ParallelChannel> getChannelsByWorld(String worldname);

    Collection<ParallelChannel> getChannelsByWorld(World bukkitWorld);

    Collection<ParallelChannel> getAllChannels();

    void gc();

    Server bukkit();
}
