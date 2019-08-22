package cn.dormao.mcpe.parallelserver.general;

import cn.dormao.mcpe.parallelserver.instance.ByteSequenceWriter;
import org.bukkit.Chunk;
import static cn.dormao.mcpe.parallelserver.ParallelUtil.*;

//TODO Fastbin快速区块算法暂时没有更好的优化方法，未来可能会删除
public class XZYFastbinParallelChunk extends XZYParallelChunk {
    public XZYFastbinParallelChunk(int chunkx, int chunkz) {
        super(chunkx, chunkz);
    }

    @Override
    public void fromBukkitChunk(Chunk c) {
        int blockMod = 0;
        ByteSequenceWriter
                blockout = new ByteSequenceWriter(),
                metaout = new ByteSequenceWriter();
        for (int x=0;x<getMaxX();x++){
            for (int z=0;z<getMaxZ();z++){
                for (int y=0;y<getMaxHeight();y++){
                    int[] blockDat = bukkitGetBlockValue(c.getBlock(x,y,z));
                    int index = getBlockIndex(x,y,z);
                    if (blockDat[0] != 0) {
                        blockMod++;
                        blockout.writeInt(index).writeASCII(blockDat[0]);
                        metaout.writeASCII(blockDat[1]);
                    }
                }
            }
        }
        this.blocks = byteArrayAppend(shortToByteArray(blockMod), blockout.getBuffer());
        this.metas = metaout.getBuffer();
    }
}
