package nio.event_loop_app;

import nio.event_loop_app.net.TcpSocket;

import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

public class Main {

    public static void main(String[] args) {
        var eventLoop = EventLoop.createDefault();
        Consumer<SocketChannel> socketConnected = socketChannel -> {
            TcpSocket tcpSocket = new TcpSocket(socketChannel);

            try {
                tcpSocket.socketChannel.register(eventLoop.selector, SelectionKey.OP_READ);
            } catch (ClosedChannelException e) {
                throw new RuntimeException(e);
            }

            System.out.println("Socket connected");
        };
        eventLoop.listen(new InetSocketAddress("localhost", 9988), socketConnected);

        eventLoop.start();
    }
}
