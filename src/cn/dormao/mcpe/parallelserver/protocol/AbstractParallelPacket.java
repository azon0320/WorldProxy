package cn.dormao.mcpe.parallelserver.protocol;

import cn.dormao.mcpe.parallelserver.ParallelPacket;
import cn.dormao.mcpe.parallelserver.ParallelUtil;
import cn.dormao.mcpe.parallelserver.instance.ByteSequenceReader;
import cn.dormao.mcpe.parallelserver.instance.ByteSequenceWriter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractParallelPacket implements ParallelPacket {

    /*
    public static void main(String[] args) {
        int[] src = new int[]{1,0,2};
        int[] pid = new int[]{5};
        //int[] out = ParallelUtil.intArrayCopy(src,0,pid,1,src.length);
        byte[] out = ParallelUtil.intArrayToByteArray(new int[]{3,4,7,4,2,8,9,1,4});
        out = ParallelUtil.subByteArray(out, 2);
        for (int i=0;i<out.length;i++){
            System.out.print(out[i]);
        }
    }
     */

    protected static Map<Byte, Class<? extends ParallelPacket>> registeredPackets;

    protected abstract byte[] onEncode();

    @Override
    public boolean requireSendIndependent() {
        return false;
    }

    public byte[] getEncoded(){
        return ParallelUtil.byteArrayAppend(new byte[]{getPacketId()}, onEncode());
    }

    public static ByteSequenceReader openReader(byte[] r){
        return new ByteSequenceReader(r);
    }

    public static ByteSequenceWriter openWriter(){return new ByteSequenceWriter();}

    static{
        registeredPackets = new HashMap<>();
    }

    public static void registerPacket(byte pid, Class<? extends ParallelPacket> claz){
        registeredPackets.put(pid, claz);
    }

    public static void registerPackets(){
        registerPacket(PK_CLOSE, ClosePacket.class);
        registerPacket(PK_BATCHED,BatchedPacket.class);
        registerPacket(PK_ERROR, ErrorPacket.class);
        registerPacket(PK_WORLD_SET, WorldSetPacket.class);
        registerPacket(PK_TEST, TestPacket.class);
        registerPacket(PK_CHUNK_REQUEST,ChunkRequestPacket.class);
        registerPacket(PK_GET_SPAWN, GetSpawnPacket.class);
        registerPacket(PK_SET_SPAWN, SetSpawnPacket.class);
        registerPacket(PK_CHUNK_RESPONSE_BLOCKS, ChunkResponseBlocksPacket.class);
        registerPacket(PK_CHUNK_RESPONSE_METAS,ChunkResponseMetasPacket.class);
        registerPacket(PK_CHUNK_RESPONSE_BIOMES, ChunkResponseBiomesPacket.class);
        registerPacket(PK_CHUNK_RESPONSE_FASTBIN_BLOCKS, ChunkResponseFastbinBlocks.class);
        registerPacket(PK_CHUNK_RESPONSE_FASTBIN_METAS, ChunkResponseFastbinMetas.class);
        registerPacket(PK_WORLD_SET_BLOCK, WorldSetBlockPacket.class);
        registerPacket(PK_WORLD_TIME, WorldTimePacket.class);
    }

    /**
     * @param dat byte array with packet id
     * @return ParallelPacket
     */
    public static ParallelPacket getPacket(byte[] dat){
        Class<? extends ParallelPacket> clazz = registeredPackets.get(dat[0]);
        ParallelPacket pk = null;
        if (clazz != null){
            try {
                pk = clazz.getDeclaredConstructor().newInstance();
                pk.doDecode(ParallelUtil.subByteArray(dat,1));
            }catch (Exception e){
                e.printStackTrace();
                //ign
                pk = null;
            }
        }
        return pk;
    }

}
