package nio.event_loop_app;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
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
                int selected = selector.select();
                System.out.printf("selected: %d", selected);
                var selectionKeys = selector.selectedKeys();

                handleSelectionKeys(selectionKeys);


                Thread.sleep(1000);
                System.out.println("run!!!");
            }
        } catch (InterruptedException | IOException e) {
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
                connCount++;
            } else if (key.isReadable()) {
                readCount++;
            } else if (key.isWritable()) {
                writeCount++;
            }
        }

    }

    private void handleAccept(SelectionKey key) {
        try {
            var serverChannel = (ServerSocketChannel) key.channel();

            var handleChannel = (Consumer<SocketChannel>)key.attachment();

            var channel = serverChannel.accept();
            if (channel == null) return;

            channel.configureBlocking(false);
            handleChannel.accept(channel);



        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
