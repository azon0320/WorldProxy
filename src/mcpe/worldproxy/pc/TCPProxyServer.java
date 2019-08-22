package mcpe.worldproxy.pc;

import cn.dormao.mcpe.parallelserver.instance.ByteSequence;
import cn.dormao.mcpe.parallelserver.instance.ByteSequenceReader;
import cn.dormao.mcpe.parallelserver.instance.ByteSequenceWriter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ReferenceCountUtil;

import cn.dormao.mcpe.parallelserver.*;
import static java.lang.System.*;

public class TCPProxyServer extends ChannelInitializer<SocketChannel>{


    public static void main(String[] args) throws Exception {
        new TCPProxyServer(20050).init();
    }

    private EventLoopGroup groupNetwork = new NioEventLoopGroup();
    private int port;

    public TCPProxyServer(int p){
        this.port = p;
    }

    public void init() throws Exception{
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(groupNetwork);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childHandler(this);
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        serverBootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(65535));
        serverBootstrap.bind(port).sync();
    }

    public void close() throws Exception{
        groupNetwork.shutdownGracefully().await();
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>(){
            @Override
            protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
                channelRead(channelHandlerContext, byteBuf);
            }

            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                super.channelActive(ctx);
            }

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                ByteBuf byteBuf = (ByteBuf) msg;
                byte[] bytearray = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(bytearray);
                try {

                /*
                if (bytearray.length == 0) System.out.println("Empty Length !");
                byte[] contentArray = new byte[bytearray.length - 1];
                for (int i = 0;i< contentArray.length;i++){
                    contentArray[i] = bytearray[i+1];
                }


                String s;
                s = new String(contentArray, StandardCharsets.UTF_8);
                System.out.println(bytearray[0] + s);
                 */

                    //handlePacket(bytearray);
                    handlePacket(bytearray);

                    /*
                    ByteSequenceWriter bw = new ByteSequenceWriter();
                    bw.writeASCII(0);
                    byte[] out = bw.getBuffer();
                    ByteBuf buf = ctx.alloc().buffer(out.length);
                    buf.writeBytes(out);
                    ctx.writeAndFlush(buf);*/
                }catch (Exception e){
                    e.printStackTrace();
                    ByteBuf buferr = ctx.alloc().buffer(1).writeBytes(new byte[]{0});
                    ctx.writeAndFlush(buferr);
                }finally {
                    ReferenceCountUtil.release(msg);
                }
            }

            public void handlePacket(byte[] bytearray){
                int pid = bytearray[0] & 0xFF;
                String s = "[%pid]%payload",payload="";
                switch (pid){
                    case 12:
                        handleBatch(bytearray);
                        break;
                    case 13:
                        handleTest(bytearray);
                        break;
                    case 2:
                        bytearray = ParallelUtil.subByteArray(bytearray,1);
                        payload = new ByteSequenceReader(bytearray).readUTF8();
                        break;
                    case 8:
                        bytearray = ParallelUtil.subByteArray(bytearray,1);
                        ByteSequenceReader in = new ByteSequenceReader(bytearray);
                        payload = in.readUTF8();
                        payload += ParallelUtil.avec3Hash(in.readUTF8()).toString();
                        break;
                }
                if (pid != 12 && pid != 13) System.out.println(s.replace("%pid", String.valueOf(pid)).replace("%payload", payload));
            }



            public void handleBatch(byte[] raw){
                raw = ParallelUtil.subByteArray(raw, 1);
                ByteSequenceReader in = new ByteSequenceReader(raw);
                int count = in.readASCII();
                for (int i=0;i<count;i++){
                    handlePacket(in.readBytes());
                }
            }

            public void handleTest(byte[] raw) {
                raw = ParallelUtil.subByteArray(raw, 1);
                out.println("-----[BEGIN TEST PACKET(Length:" + raw.length + ")]-----");
                ByteSequenceReader reader = new ByteSequenceReader(raw);
                out.println("ASCII : " + reader.readASCII());
                byte[] bs = reader.readBytes();
                for (int i = 0; i < bs.length; i++) {
                    byte b = bs[i];
                    out.print("[" + (b & 0xFF) + "]");
                }
                out.println();
                out.println("UTF8 : " + reader.readUTF8());
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                //super.exceptionCaught(ctx, cause);
                ctx.close();
            }
        });
    }
}
