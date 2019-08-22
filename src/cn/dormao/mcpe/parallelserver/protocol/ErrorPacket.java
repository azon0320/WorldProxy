package cn.dormao.mcpe.parallelserver.protocol;

import cn.dormao.mcpe.parallelserver.instance.ByteSequenceReader;

public class ErrorPacket extends AbstractParallelPacket {
    @Override
    public byte getPacketId() {
        return PK_ERROR;
    }

    public int errcode;

    public String errmsg;

    @Override
    public boolean requireSendIndependent() {
        return true;
    }

    @Override
    protected byte[] onEncode() {
        return openWriter().writeASCII(errcode).writeUTF8(errmsg).getBuffer();
    }

    @Override
    public void doDecode(byte[] raw) {
        ByteSequenceReader in = openReader(raw);
        errcode = in.readASCII();
        errmsg = in.readUTF8();
    }
}
