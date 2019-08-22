package cn.dormao.mcpe.parallelserver.protocol;

import cn.dormao.mcpe.parallelserver.ParallelUtil;
import cn.dormao.mcpe.parallelserver.Vector3;

public class SetSpawnPacket extends AbstractParallelPacket {
    @Override
    public byte getPacketId() {
        return PK_SET_SPAWN;
    }

    public Vector3 spawn;

    @Override
    protected byte[] onEncode() {
        return openWriter().writeUTF8(ParallelUtil.vec3Hash(spawn)).getBuffer();
    }

    @Override
    public void doDecode(byte[] raw) {
        spawn = ParallelUtil.avec3Hash(openReader(raw).readUTF8());
    }
}
