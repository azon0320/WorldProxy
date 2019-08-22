package cn.dormao.mcpe.parallelserver.general;

import cn.dormao.mcpe.parallelserver.*;
import cn.dormao.mcpe.parallelserver.protocol.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.bukkit.World;

import static cn.dormao.mcpe.parallelserver.ParallelPacket.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SimpleParallelChannel extends ChannelInboundHandlerAdapter implements ParallelChannel {

    public final String ERR_WORLD_NOT_SET = "world not set";
    public final String ERR_WORLD_NOT_EXIST = "world not exist";
    public final String ERR_WORLD_REPEAT = "world has been set";

    protected String worldname;

    protected Parallel reference;

    protected boolean available;

    protected Channel channel;

    protected ConcurrentLinkedQueue<Byte[]> inqueue;

    protected ConcurrentLinkedQueue<ParallelPacket> outqueue;

    protected int batchCount;

    protected int channelId;

    protected Map<String, Long> chunkRequestQueue;

    public SimpleParallelChannel(){
        batchCount = 2;
        channel = null;
        worldname = null;
        available = true;
        inqueue = new ConcurrentLinkedQueue<>();
        outqueue = new ConcurrentLinkedQueue<>();
        chunkRequestQueue = new HashMap<>();
    }

    public SimpleParallelChannel bindParallel(Parallel p, int id){
        reference = p;channelId = id;return this;
    }

    @Override
    public Parallel parallel() {
        return reference;
    }

    @Override
    public int getChannelId() { return channelId; }

    public String getWorldName() {
        return worldname;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public boolean isChannelAvailable() {
        return isAvailable() && channel != null;
    }

    @Override
    public void processPacket(byte[] payload) {
        if (payload.length > 0){
            ParallelPacket p = AbstractParallelPacket.getPacket(payload);
            if (p != null) {
                switch (p.getPacketId()) {
                    case PK_CLOSE:
                        this.close();
                        return;
                    case PK_BATCHED:
                        BatchedPacket pk12 = (BatchedPacket) p;
                        for (byte[] dat : pk12.payloads) {
                            this.processPacket(dat);
                        }
                        break;
                    case PK_WORLD_SET:
                        if (getWorldName() == null) {
                            worldname = ((WorldSetPacket) p).worldname;
                            if (parallel().existBukkitWorld(worldname)) {
                                if (parallel().getParallelWorld(worldname) != null){
                                    parallel().bukkit().getLogger().info(String.format(Locale.ENGLISH,
                                            "[Channel %d] bind world : %s", getChannelId(), getWorldName()
                                    ));
                                }else {
                                    parallel().loadParallelWorld(worldname);
                                }
                                worldLoadCallback();

                            } else sendError(PK_WORLD_SET, ERR_WORLD_NOT_EXIST, true);
                        } else sendError(PK_WORLD_SET, ERR_WORLD_REPEAT, true);
                        break;
                    case PK_CHUNK_REQUEST:
                        ChunkRequestPacket pk2 = (ChunkRequestPacket) p;
                        if (checkError(true)) {
                            ParallelWorld world = parallel().getParallelWorld(getWorldName());
                            chunkRequestQueue.put(ParallelUtil.chunkHash(pk2.chunkx, pk2.chunkz), System.currentTimeMillis());
                            if (world.chunkExists(pk2.chunkx,pk2.chunkz)){
                                chunkRequestCallback(world.getChunk(pk2.chunkx, pk2.chunkz));
                            }else {
                                world.loadChunk(pk2.chunkx, pk2.chunkz);
                            }
                        }
                        break;
                    case PK_GET_SPAWN:
                        if (this.checkError(true)){
                            SetSpawnPacket pk5 = new SetSpawnPacket();
                            pk5.spawn = parallel().getParallelWorld(getWorldName()).getSpawn();
                            sendPacket(pk5);
                        }
                        break;
                    //TestPacket ignored
                    case PK_WORLD_SET_BLOCK:
                        WorldSetBlockPacket pk14 = (WorldSetBlockPacket) p;
                        if (checkError(true)){
                            ParallelWorld world = parallel().getParallelWorld(getWorldName());
                            int chunkx = pk14.pos.getFloorX() / 16, chunkz = pk14.pos.getFloorZ() / 16;
                            int id = pk14.id,meta = pk14.meta;
                            //System.out.println(String.format("Channel RECV Id [%d,%d]", id, meta));
                            ParallelChunk chunk = world.getChunk(chunkx, chunkz);
                            chunk.setFullBlockXYZ(pk14.pos.getFloorX(), pk14.pos.getFloorY(), pk14.pos.getFloorZ(), id, meta);
                            world.bukkitLoadChunk(chunkx, chunkz);
                            world.bukkitSetBlock(pk14.pos.getFloorX(), pk14.pos.getFloorY(), pk14.pos.getFloorZ(), id, meta);
                        }
                        break;
                    default:
                        System.out.println("PID : " + p.getPacketId());
                        break;
                }
            }else System.out.println("Unknown PID : " + payload[0]);
        }
    }

    protected boolean checkError(boolean sendclose){
        if (isAvailable()){
            String errmsg = null;
            if (getWorldName() == null){
                errmsg = ERR_WORLD_NOT_SET;
            }else if(!parallel().existBukkitWorld(getWorldName())){
                errmsg = ERR_WORLD_NOT_EXIST;
            }
            boolean returnBool = errmsg == null;
            if (!returnBool) sendError(0, errmsg, sendclose);
            return returnBool;
        }else return false;
    }

    protected void sendError(int code, String errmsg, boolean sendclose){
        ErrorPacket pk = new ErrorPacket();
        pk.errcode = code;
        pk.errmsg = errmsg;
        sendPacket(pk);
        if (sendclose) sendPacket(new ClosePacket());
    }

    @Override
    public void sendPacket(ParallelPacket pk) {
        outqueue.offer(pk);
    }

    @Override
    public void chunkRequestCallback(ParallelChunk vchunk) {
        String hash = ParallelUtil.chunkHash(vchunk.getChunkX(), vchunk.getChunkZ());
        if (chunkRequestQueue.containsKey(hash)){
            if (checkError(true)){
                chunkRequestQueue.remove(hash);
                sendChunk(vchunk);
            }
        }
    }

    @Override
    public void worldLoadCallback() {
        parallel().bukkit().getLogger().info(String.format(Locale.ENGLISH,
                "[Channel %d] Async bind world : %s", getChannelId(), getWorldName()
        ));
        ParallelWorld vworld = parallel().getParallelWorld(getWorldName());
        if (vworld != null){
            WorldTimePacket pk = new WorldTimePacket();
            pk.timetick = (int) vworld.getWorld().getTime();
            pk.timerun = true;
            sendPacket(pk);
        }
    }

    @Override
    public void sendChunk(int chunkx, int chunkz) {
        if (worldname != null){
            ParallelWorld vworld = parallel().getParallelWorld(worldname);
            if (vworld != null){
                ParallelChunk vchunk = vworld.getChunk(chunkx, chunkz);
                if (vchunk != null) {
                    sendChunk(vchunk);
                }else sendError(0, "chunk is null", true);
            }else sendError(0, ERR_WORLD_NOT_EXIST, true);
        }else sendError(0,ERR_WORLD_NOT_SET,true);
    }

    public void sendChunk(ParallelChunk chunk){
        if (chunk instanceof XZYFastbinParallelChunk) {
            ChunkResponseFastbinBlocks pk1 = new ChunkResponseFastbinBlocks();
            pk1.chunkx = chunk.getChunkX();pk1.chunkz = chunk.getChunkZ();
            pk1.payload = chunk.getBlocks();
            ChunkResponseFastbinMetas pk2 = new ChunkResponseFastbinMetas();
            pk2.chunkx = chunk.getChunkX();pk2.chunkz = chunk.getChunkZ();
            pk2.payload = chunk.getMetas();
            sendPacket(pk1);sendPacket(pk2);
        }else {
            ChunkResponseBlocksPacket pk1 = new ChunkResponseBlocksPacket();
            pk1.chunkx = chunk.getChunkX();
            pk1.chunkz = chunk.getChunkZ();
            pk1.payload = chunk.getBlocks();
            ChunkResponseMetasPacket pk2 = new ChunkResponseMetasPacket();
            pk2.chunkx = chunk.getChunkX();
            pk2.chunkz = chunk.getChunkZ();
            pk2.payload = chunk.getMetas();
            ChunkResponseBiomesPacket pk3 = new ChunkResponseBiomesPacket();
            pk3.chunkx = chunk.getChunkX();
            pk3.chunkz = chunk.getChunkZ();
            pk3.payload = chunk.getBiomes();
            this.sendPacket(pk1);this.sendPacket(pk2);this.sendPacket(pk3);
        }
    }

    protected void tickSendQueue(){
        final int MAX_DATA_LEN = 65535;
        List<byte[]> cache = new ArrayList<>();
        ParallelPacket pk;
        int datLength = 2;//PID & COUNT
        while ((pk = outqueue.peek()) != null){
            byte[] pay = pk.getEncoded();
            int datLen = pay.length + 2;
            if (datLen + datLength < MAX_DATA_LEN){
                cache.add(pay);
                datLength += datLen;
                outqueue.poll();
            }else break;
        }
        if (cache.size() > 1) {
            BatchedPacket batch = new BatchedPacket();
            batch.payloads = cache.toArray(new byte[0][]);
            batch.getEncoded();
            byte[] batchedDat = batch.getEncoded();
            channel.writeAndFlush(channel.alloc().buffer(batchedDat.length).writeBytes(batchedDat));
        }else if (cache.size() == 1){
            byte[] pay = cache.get(0);
            channel.writeAndFlush(channel.alloc().buffer(pay.length).writeBytes(pay));
        }
    }

    protected void tickSendQueue0(){
        byte[][] cache = new byte[batchCount][0];
        ParallelPacket pk;
        int polledCount=0;
        for (int i=0;i<cache.length;i++){
            pk = outqueue.peek();
            if (pk != null) {
                if (pk.requireSendIndependent()) {
                    if (i == 0) { //数组第一位
                        polledCount++;
                        outqueue.poll();
                        cache[i] = pk.getEncoded();
                        break;
                    } else break;
                } else {
                    polledCount++;
                    outqueue.poll();
                    cache[i] = pk.getEncoded();
                }
            }else break;
        }
        byte[][] pks = new byte[polledCount][];
        System.arraycopy(cache,0,pks,0,polledCount);
        if (pks.length == 1){
            channel.writeAndFlush(channel.alloc().buffer(pks[0].length).writeBytes(pks[0]));
        }else if (pks.length > 1){
            BatchedPacket batch = new BatchedPacket();
            batch.payloads = pks;
            byte[] fin = batch.getEncoded();
            channel.writeAndFlush(channel.alloc().buffer(fin.length).writeBytes(fin));
        }
    }

    @Override
    public boolean onTick(int currentTick) {
        Byte[] obj = inqueue.poll();
        if (obj != null) processPacket(ParallelUtil.depackageByte(obj));
        tickSendQueue();
        return isAvailable();
    }

    @Override
    public void close() {
        available = false;
        reference = null;
        if (channel != null) {
            channel.close();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        channel = ctx.channel();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] dat = new byte[buf.readableBytes()];
        buf.readBytes(dat);
        inqueue.offer(ParallelUtil.packageByte(dat));
        ReferenceCountUtil.safeRelease(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(!(cause instanceof IOException)) cause.printStackTrace();
        ErrorPacket err = new ErrorPacket();
        err.errcode = 0;
        err.errmsg = cause.toString();
        sendPacket(err);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.close();
    }
}
