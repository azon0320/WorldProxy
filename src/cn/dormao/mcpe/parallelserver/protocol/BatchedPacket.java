package cn.dormao.mcpe.parallelserver.protocol;

import cn.dormao.mcpe.parallelserver.instance.ByteSequenceReader;
import cn.dormao.mcpe.parallelserver.instance.ByteSequenceWriter;

public class BatchedPacket extends AbstractParallelPacket {
    @Override
    public byte getPacketId() {
        return PK_BATCHED;
    }

    public byte[][] payloads;

    @Override
    protected byte[] onEncode() {
        int count = payloads.length;
        ByteSequenceWriter os = openWriter();
        os.writeASCII(count);
        for (byte[] payload : payloads){
            os.writeBytes(payload);
        }
        return os.getBuffer();
    }

    @Override
    public void doDecode(byte[] raw) {
        ByteSequenceReader in = openReader(raw);
        int count = in.readASCII();
        payloads = new byte[count][];
        for (int i=0;i<count;i++){
            payloads[i] = in.readBytes();
        }
    }
}
