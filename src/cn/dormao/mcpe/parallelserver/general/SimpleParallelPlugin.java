package cn.dormao.mcpe.parallelserver.general;

import cn.dormao.mcpe.parallelserver.protocol.AbstractParallelPacket;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleParallelPlugin extends JavaPlugin implements Listener,Runnable {

    private EventLoopGroup parallelWorker;
    private SimpleParallel parallel;
    private int port = 20050;

    @Override
    public void onLoad() {
        AbstractParallelPacket.registerPackets();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        try {
            this.launchParallel();
            getLogger().info("Open Parallel Server at 20050");
            getServer().getScheduler().scheduleSyncRepeatingTask(this, this,0,1);
            getServer().getPluginManager().registerEvents(new SimpleParallelListener(this.parallel), this);
            getServer().getPluginManager().registerEvents(new TestedListener(), this);
        }catch (Exception e){
            e.printStackTrace();
            getLogger().info("Cannot launch Parallel Daemon");
        }
    }

    @Override
    public void onDisable() {
        try {
            parallelWorker.shutdownGracefully().await();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if (parallel != null){
            parallel.onTick(0);
        }
    }

    private void launchParallel() throws Exception{
        parallelWorker = new NioEventLoopGroup();
        parallel = new SimpleParallel(getServer());
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(parallelWorker)
                .channel(NioServerSocketChannel.class)
                .childHandler(parallel)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(65535));
        serverBootstrap.bind(port).sync();
    }
}
