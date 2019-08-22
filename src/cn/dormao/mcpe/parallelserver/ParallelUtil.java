package cn.dormao.mcpe.parallelserver;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.*;

public class ParallelUtil {

    private ParallelUtil(){}

    public static String chunkHash(int x, int z){
        return x + "," + z;
    }

    public static int[] achunkHash(String hash){
        String[] nodes = hash.split(",");
        int[] res = new int[2];
        res[0] = nodes.length > 0 ? Integer.valueOf(nodes[0]) : 0;
        res[1] = nodes.length > 1 ? Integer.valueOf(nodes[1]) : 0;
        return res;
    }

    public static String vec3Hash(Vector3 v){
        return v.getX() + "," + v.getY() + "," + v.getZ();
    }

    public static Vector3 vec3FromLocation(Location loc, boolean isBlock){
        return new Vector3(
                isBlock ? loc.getBlockX() : (float) loc.getX(),
                isBlock ? loc.getBlockY() : (float) loc.getY(),
                isBlock ? loc.getBlockZ() : (float) loc.getZ()
        );
    }

    public static Vector3 avec3Hash(String str){
        String[] nod = str.split(",");
        return new Vector3(
                Float.valueOf(nod.length > 0 ? nod[0] : "0.0"),
                Float.valueOf(nod.length > 1 ? nod[1] : "0.0"),
                Float.valueOf(nod.length > 2 ? nod[2] : "0,0")
        );
    }

    public static int charAscii(char c){
        return (int) c;
    }

    public static char asciiChr(byte b){
        return (char) (b % 255);
    }

    public static String asciiChrAll(byte[] payload){
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i < payload.length; i++){
            sb.append(asciiChr(payload[i]));
        }
        return sb.toString();
    }

    public static byte[] intArrayToByteArray(int[] src){
        byte[] out = new byte[src.length];
        for (int i = 0;i < out.length;i++){
            out[i] = (byte) (src[i] % 255);
        }
        return out;
    }

    public static int[] byteArrayToIntArray(byte[] src){
        int[] out = new int[src.length];
        for (int i = 0;i < out.length;i++){
            out[i] = src[i];
        }
        return out;
    }

    public static int[] subIntArray(int[] src, int offset){
        int[] target = new int[0];
        return intArrayCopy(src, offset,target,0,src.length - offset);
    }

    public static byte[] subByteArray(byte[] src, int offset){
       byte[] target = new byte[0];
       return byteArrayCopy(src,offset,target,0,src.length - offset);
    }

    //目标数组 指定位置后的数据全部废弃
    public static int[] intArrayCopy(int[] src, int srcIndex, int[] target, int targetIndex,int len){
        int length = targetIndex + len;
        int[] out = new int[length];
        int sIndex = srcIndex, tIndex = 0;
        for (int i = 0;i < length;i++){
            if (i >= targetIndex){
                out[i] = src[sIndex++];
            }else {
                out[i] = target[tIndex++];
            }
        }
        return out;
    }

    public static byte[] byteArrayAppend(byte[] src, byte[] add){
        return byteArrayCopy(add,0, src, src.length, add.length);
    }

    public static byte[] byteArrayCopy(byte[] src, int srcIndex, byte[] target, int targetIndex, int len){
        int length = targetIndex + len;
        byte[] out = new byte[length];
        int sIndex = srcIndex, tIndex = 0;
        for (int i = 0;i < length;i++){
            if (i >= targetIndex){
                out[i] = src[sIndex++];
            }else {
                out[i] = target[tIndex++];
            }
        }
        return out;
    }

    public static Map<String, Chunk> bukkitGetWorldChunks(Chunk[] c){
        Map<String, Chunk> map = new HashMap<>();
        for (Chunk ch : c){
            map.put(chunkHash(ch.getX(), ch.getZ()), ch);
        }
        return map;
    }

    public static Chunk bukkitGetWorldChunk(int x, int z, Chunk[] loaded){
        Chunk out = null;
        for (Chunk chunk : loaded){
            if (x == chunk.getX() && z == chunk.getZ()){
                out = chunk;break;
            }
        }
        return out;
    }

    @SuppressWarnings("all")
    public static int[] bukkitGetBlockValue(Block block){
        int id = block.getType().getId();
        int meta = block.getData();
        return new int[]{id,meta};
    }
    @SuppressWarnings("all")
    public static int[] bukkitGetBlockValue(BlockState state){
        int id = state.getType().getId();
        int meta = state.getData().getData();
        return new int[]{id,meta};
    }

    public static byte[] stringToUTF8bytes(String s){
        return s.getBytes(StandardCharsets.UTF_8);
    }

    public static String utf8BytesToString(byte[] b){
        return new String(b, StandardCharsets.UTF_8);
    }

    public static byte[] shortToByteArray(int len){
        return new byte[]{(byte) (len / 256), (byte) (len % 256)};
    }

    public static int byteArrayToShort(byte[] raw){
        return (raw[0] & 0xFF) * 256 + (raw[1] & 0xFF);
    }

    public static byte[] intToByteArray(int num){
        return new byte[]{
                (byte) ((num / pow(256,3)) % 256),
                (byte) ((num / pow(256,2)) % 256),
                (byte) ((num / pow(256,1)) % 256),
                (byte) (num % 256),
        };
    }

    public static int byteArrayToInt(byte[] raw){
        return (int) ((raw[0] & 0xFF) * pow(256,3) +
                (raw[1] & 0xFF) * pow(256,2) +
                (raw[2] & 0xFF) * pow(256,1) +
                (raw[3] & 0xFF));
    }

    public static Byte[] packageByte(byte[] b){
        Byte[] bytes = new Byte[b.length];
        for(int i = 0;i < bytes.length;i++){
            bytes[i] = b[i];
        }
        return bytes;
    }

    public static byte[] depackageByte(Byte[] b){
        byte[] bytes = new byte[b.length];
        for(int i = 0;i < bytes.length;i++){
            bytes[i] = b[i];
        }
        return bytes;
    }

    public static boolean byteAllEmpty(byte[] d){
        for (byte b : d){
            if ((b & 0xFF) != 0) return false;
        }
        return true;
    }
}
