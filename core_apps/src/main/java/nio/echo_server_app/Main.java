package nio.echo_server_app;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Main {
    private static final String POISON_PILL = "POISON_PILL";

    public static void start() throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress("localhost", 5454));
        // сетевой канал ServerSocketChannel будет работать не в блокирующем режиме
        serverSocket.configureBlocking(false);
        // регистрируем канал в селекторе, который будет нас оповещать при возникновении событий с этим каналом
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        // todo: переместить буффер
        ByteBuffer buffer = ByteBuffer.allocate(256);

        System.out.println("Started!");
        new Thread(() -> {
            while (true) {
                try {
                    // блокируемся, пока не будет доступен канал
                    selector.select();
                    // получаем доступные каналы
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iter = selectedKeys.iterator();
                    while (iter.hasNext()) {

                        SelectionKey key = iter.next();

                        if (key.isAcceptable()) {
                            register(selector, serverSocket);
                        }

                        if (key.isReadable()) {
                            answerWithEcho(buffer, key);
                        }

                        // удаляем канал, чтобы в дальнейшем он мог нас снова оповестить,
                        // когда мы будем слушать в методе select();
                        iter.remove();
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }).start();
    }

    private static void answerWithEcho(ByteBuffer buffer, SelectionKey key)
            throws IOException {

        SocketChannel client = (SocketChannel) key.channel();
        // читаем данные из канала в буффер(блокируется, если установлен режим блокировки)
        int r = client.read(buffer);
        if (r == -1 || new String(buffer.array()).trim().equals(POISON_PILL)) {
            client.close();
            System.out.println("Not accepting client messages anymore");
        } else {
            buffer.flip();
            client.write(buffer);
            buffer.clear();
        }
    }

    private static void register(Selector selector, ServerSocketChannel serverSocket)
            throws IOException {

        // получаем соединение(происходит блокировка потока, если serverSocket установлен в блокиркющий режим)
        SocketChannel client = serverSocket.accept();
        // сетевой канал SocketChannel будет работать не в блокирующем режиме
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }
}
