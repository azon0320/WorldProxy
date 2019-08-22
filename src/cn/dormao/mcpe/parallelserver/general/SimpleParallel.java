package cn.dormao.mcpe.parallelserver.general;

import cn.dormao.mcpe.parallelserver.Parallel;
import cn.dormao.mcpe.parallelserver.ParallelChannel;
import cn.dormao.mcpe.parallelserver.ParallelWorld;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.util.*;

public class SimpleParallel extends ChannelInitializer<SocketChannel> implements Parallel {

    private static int channelCount = 0;

    protected Server serverBukkit;

    protected Map<Integer, ParallelChannel> parallelChannelMap;

    protected Map<String, ParallelWorld> parallelWorldMap;

    protected int currentTick = 0;

    public SimpleParallel(Server server){
        serverBukkit = server;
        parallelChannelMap = new HashMap<>();
        parallelWorldMap = new HashMap<>();
    }

    @Override
    public Collection<ParallelWorld> getParallelWorlds() {
        return new ArrayList<>(parallelWorldMap.values());
    }

    @Override
    public ParallelWorld getParallelWorld(String worldname) {
        return parallelWorldMap.get(worldname);
    }

    @Override
    public boolean existBukkitWorld(String worldname) {
        File f = new File(bukkit().getWorldContainer(), worldname);
        return f.isDirectory();
    }

    @Override
    public boolean existBukkitWorldInstance(String worldname) {
        return bukkit().getWorld(worldname) != null;
    }

    @Override
    public boolean loadParallelWorld(String worldname) {
        if (existBukkitWorld(worldname)){
            if (!parallelWorldMap.containsKey(worldname)){
                bukkit().createWorld(WorldCreator.name(worldname));
                ParallelWorld vworld = new SimpleParallelWorld(worldname, this);
                bukkit().getLogger().info(String.format(Locale.ENGLISH,"Loading world %s",vworld.getWorldName()));
                parallelWorldMap.put(worldname, vworld);
            }
            return true;
        }else return false;
    }

    @Override
    public void gc() {
        for (ParallelWorld world : parallelWorldMap.values()){
            world.gc();
        }
    }

    @Override
    public boolean onTick(int currentTick) {
        ++this.currentTick;
        for (ParallelChannel channel : new ArrayList<>(parallelChannelMap.values())){
            if (!channel.onTick(this.currentTick)) {
                channel.close();
                bukkit().getLogger().info("Closed Channel " + channel.getChannelId());
                parallelChannelMap.remove(channel.getChannelId());
            }
        }
        for (ParallelWorld world : parallelWorldMap.values()){
            world.onTick(this.currentTick);
        }
        if (this.currentTick % (20 * 60 * 3) == 0){//3分钟GC一次
            this.gc();
        }
        return true;
    }

    public Collection<ParallelChannel> getChannelsByWorld(String worldname){
        List<ParallelChannel> l = new ArrayList<>();
        for (ParallelChannel channel : this.parallelChannelMap.values()){
            if (worldname.equals(channel.getWorldName())) l.add(channel);
        }
        return l;
    }

    public Collection<ParallelChannel> getChannelsByWorld(World bukkitWorld){
        return getChannelsByWorld(bukkitWorld.getName());
    }

    public Collection<ParallelChannel> getAllChannels(){return new ArrayList<>(parallelChannelMap.values());}

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        SimpleParallelChannel handler = new SimpleParallelChannel().bindParallel(this, allocateChannelId());
        parallelChannelMap.put(handler.getChannelId(), handler);
        bukkit().getLogger().info(String.format(Locale.ENGLISH,
                "[%s] Connecting Parallel...",socketChannel.remoteAddress().toString()
                ));
        socketChannel.pipeline().addLast(handler);
    }

    @Override
    public int allocateChannelId() {
        return ++channelCount;
    }

    @Override
    public Server bukkit() {
        return serverBukkit;
    }
}
