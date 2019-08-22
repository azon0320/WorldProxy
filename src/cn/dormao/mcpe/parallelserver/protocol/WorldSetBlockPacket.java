package cn.dormao.mcpe.parallelserver.protocol;

import cn.dormao.mcpe.parallelserver.ParallelUtil;
import cn.dormao.mcpe.parallelserver.Vector3;
import cn.dormao.mcpe.parallelserver.instance.ByteSequenceReader;

public class WorldSetBlockPacket extends AbstractParallelPacket {

    @Override
    public byte getPacketId() {
        return PK_WORLD_SET_BLOCK;
    }

    public Vector3 pos;
    public int id,meta;
    public boolean fallingblock = false;

    @Override
    protected byte[] onEncode() {
        return openWriter()
                .writeUTF8(ParallelUtil.vec3Hash(pos))
                .writeASCII(id).writeASCII(meta)
                .writeASCII(fallingblock ? 1 : 0)
                .getBuffer();
    }

    @Override
    public void doDecode(byte[] raw) {
        ByteSequenceReader in = openReader(raw);
        pos = ParallelUtil.avec3Hash(in.readUTF8());
        id = in.readASCII();meta = in.readASCII();
        fallingblock = in.readASCII() == 1;
    }
}
