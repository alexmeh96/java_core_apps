package reactive_steams.app2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Flow.*;
import java.util.concurrent.atomic.AtomicInteger;

public class NetworkBackpressureExample {
    public static void main(String[] args) throws IOException, InterruptedException {
        // Create an asynchronous server socket channel
        AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open()
                .bind(new InetSocketAddress(8080));

        // Create a publisher for incoming connections
        Publisher<AsynchronousSocketChannel> publisher = new Publisher<>() {
            @Override
            public void subscribe(Subscriber<? super AsynchronousSocketChannel> subscriber) {
                // Accept incoming connections
                serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
                    @Override
                    public void completed(AsynchronousSocketChannel channel, Void attachment) {
                        // Notify subscriber about the incoming connection
                        subscriber.onNext(channel);
                        // Continue accepting more connections
                        serverSocketChannel.accept(null, this);
                    }

                    @Override
                    public void failed(Throwable exc, Void attachment) {
                        // Handle failure
                        subscriber.onError(exc);
                    }
                });
            }
        };

        // Create a subscriber for processing data from incoming connections
        Subscriber<AsynchronousSocketChannel> subscriber = new Subscriber<AsynchronousSocketChannel>() {
            private Subscription subscription;
            private AtomicInteger demand = new AtomicInteger(0);

            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                // Request initial batch of connections
                subscription.request(3);
            }

            @Override
            public void onNext(AsynchronousSocketChannel channel) {
                // Handle data from incoming connection
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                channel.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        attachment.flip();
                        byte[] data = new byte[attachment.remaining()];
                        attachment.get(data);
                        System.out.println("Received data: " + new String(data));
                        attachment.clear();
                        // Continue reading data if demand is available
                        if (demand.decrementAndGet() > 0) {
                            channel.read(buffer, buffer, this);
                        }
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        // Handle failure
                    }
                });
                // Increment demand for more connections
                demand.incrementAndGet();
                // Request more connections if demand is available
                if (demand.get() > 0) {
                    subscription.request(1);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                // Handle errors
            }

            @Override
            public void onComplete() {
                // Handle completion
            }
        };

        // Subscribe the subscriber to the publisher
        publisher.subscribe(subscriber);


        Thread.sleep(1000000);
    }
}