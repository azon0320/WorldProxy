package cn.dormao.mcpe.parallelserver.protocol;

import cn.dormao.mcpe.parallelserver.instance.ByteSequenceReader;

public class TestPacket extends AbstractParallelPacket {
    @Override
    public byte getPacketId() {
        return PK_TEST;
    }

    public int asciiValue = 2;
    public byte[] byteValue = new byte[]{3};
    public String utf8Value = "hello";
    public int intValue = 3458232;

    @Override
    protected byte[] onEncode() {
        return openWriter()
                .writeASCII(asciiValue)
                .writeBytes(byteValue)
                .writeUTF8(utf8Value).writeInt(intValue).getBuffer();
    }

    @Override
    public void doDecode(byte[] raw) {
        ByteSequenceReader in = openReader(raw);
        asciiValue = in.readASCII();
        byteValue = in.readBytes();
        utf8Value = in.readUTF8();
        intValue = in.readInt();
    }
}
