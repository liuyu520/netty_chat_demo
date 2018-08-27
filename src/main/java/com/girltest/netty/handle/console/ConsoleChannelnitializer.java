package com.girltest.netty.handle.console;

import com.girltest.netty.dto.ChannelHandleDto;
import com.girltest.netty.encode.bytes.BytesMessageDecoder;
import com.girltest.netty.encode.bytes.BytesMessageEncoder;
import com.girltest.netty.handle.bytes.ConsoleServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsoleChannelnitializer extends ChannelInitializer<SocketChannel> {
    private static final Logger log = LoggerFactory.getLogger(ConsoleChannelnitializer.class);
    //    @Getter
//    @Setter
    private ChannelHandleDto channelHandleDto;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        /**
         * Handler就相当于Servlet中的过滤器, 请求和响应都会走Handler
         * HttpServerCodec: http的编解码器，用于Http请求和相应
         */
//        pipeline.addLast("httpServerCodec", new HttpServerCodec());
        pipeline.addLast(new BytesMessageEncoder());
        //io.netty.handler.codec.TooLongFrameException: Adjusted frame length exceeds 1048576: 83886084 - discarded
        pipeline.addLast(new BytesMessageDecoder(1024 * 1024 * 4/* 支持大文件上传 */, 1, 8));//lengthFieldLength 只能为1,2,4,8
        pipeline.addLast("stringHttpServerHandler", new ConsoleServerHandler(channelHandleDto));

    }
}
