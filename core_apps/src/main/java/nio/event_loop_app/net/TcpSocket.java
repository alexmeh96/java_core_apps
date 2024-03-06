package nio.event_loop_app.net;

import java.nio.channels.SocketChannel;

public class TcpSocket {

    public SocketChannel socketChannel;

    public TcpSocket(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }
}
