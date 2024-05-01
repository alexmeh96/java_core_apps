package netty.app1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.bootstrap.ServerBootstrap;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.nio.charset.StandardCharsets;

public class NettyReactiveStreamsServer {

    static class NettySubscriber implements Subscriber<ByteBuf> {

        private Subscription subscription;

        @Override
        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            this.subscription.request(1); // Request initial data
        }

        @Override
        public void onNext(ByteBuf byteBuf) {
            String data = byteBuf.toString(StandardCharsets.UTF_8);
            System.out.println("Received: " + data);
            subscription.request(1); // Request more data
        }

        @Override
        public void onError(Throwable throwable) {
            throwable.printStackTrace();
        }

        @Override
        public void onComplete() {
            System.out.println("Data stream completed");
        }
    }

    static class NettyPublisher implements Publisher<ByteBuf> {

        private Subscriber<? super ByteBuf> subscriber;

        @Override
        public void subscribe(Subscriber<? super ByteBuf> subscriber) {
            this.subscriber = subscriber;
        }

        public void emitData(ByteBuf byteBuf) {
            if (subscriber != null) {
                subscriber.onNext(byteBuf);
            }
        }

        public void complete() {
            if (subscriber != null) {
                subscriber.onComplete();
            }
        }
    }

    static class NettyServerHandler extends ChannelInboundHandlerAdapter {

        private final NettyPublisher publisher;

        NettyServerHandler(NettyPublisher publisher) {
            this.publisher = publisher;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            publisher.emitData(Unpooled.copiedBuffer("Data from server", StandardCharsets.UTF_8));
            publisher.complete();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        NettyPublisher publisher = new NettyPublisher();
        NettySubscriber subscriber = new NettySubscriber();

        publisher.subscribe(subscriber);

        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioServerSocketChannel>() {
                    @Override
                    protected void initChannel(NioServerSocketChannel ch) {
                        ch.pipeline().addLast(new NettyServerHandler(publisher));
                    }
                })
                .bind(8080)
                .sync()
                .channel()
                .closeFuture()
                .sync();
    }
}
