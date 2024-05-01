package nio.event_loop_app;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

public class EventLoop implements Runnable {
    public void start() {
        new Thread(this).start();
    }

    public Selector selector;

    static EventLoop createDefault() {
        try {
            var eventLoop = new EventLoop();
            eventLoop.selector = Selector.open();

            return eventLoop;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void run() {
        try {
            while (true) {
                selector.select(1000);
                var selectionKeys = selector.selectedKeys();

                handleSelectionKeys(selectionKeys);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ServerSocketChannel listen(InetSocketAddress address, Consumer<SocketChannel> acceptHandler) {
        try {
            var serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(address);
            serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT, acceptHandler);

            System.out.printf("Server started on port %s. Listening...\n%n", address.getPort());
            return serverSocketChannel;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void handleSelectionKeys(Set<SelectionKey> selectionKeys) {
        var iter = selectionKeys.iterator();
        int acceptCount = 0, connCount = 0, readCount = 0, writeCount = 0;

        while (iter.hasNext()) {
            var key = iter.next();
            iter.remove();

            if (key.isAcceptable()) {
                handleAccept(key);
                acceptCount++;
            } else if (key.isConnectable()) {
                handleConnect(key);
                connCount++;
            } else if (key.isReadable()) {
                handleRead(key);
                readCount++;
            } else if (key.isWritable()) {
                handleWrite(key);
                writeCount++;
            }
        }

    }

    private void handleAccept(SelectionKey key) {
        try {
            var serverChannel = (ServerSocketChannel) key.channel();
            var handleChannel = (Consumer<SocketChannel>) key.attachment();

            var channel = serverChannel.accept();
            if (channel == null) return;

            channel.configureBlocking(false);
            handleChannel.accept(channel);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleConnect(SelectionKey key) {
        try {
            var channel = (SocketChannel) key.channel();
            var handleChannel = (Consumer<SocketChannel>) key.attachment();

            boolean connect;

            try {
                connect = channel.finishConnect();
            } catch (Exception e) {
                System.out.println("close connection");
                channel.close();
                return;
            }

            if (connect) {
                handleChannel.accept(channel);
            } else {
                System.out.println("connection continue");
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleWrite(SelectionKey key) {
        try {
            var channel = (SocketChannel) key.channel();
            var buffer = ByteBuffer.wrap("Hello, World !!!".getBytes());

            channel.write(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleRead(SelectionKey key) {
        try {
            var channel = (SocketChannel)key.channel();

            var buffer = ByteBuffer.allocate(1024);
            int read = channel.read(buffer);

            if (read > 0) {
                buffer.flip();
                byte[] bytes = new byte[read];
                buffer.get(bytes);
                System.out.print("Received: " + new String(bytes));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
