package nio.async_echo_server_app;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;

public class Main {
    public static void main(String[] args) {
        try {
            AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open();
            InetSocketAddress address = new InetSocketAddress("localhost", 8080);
            serverSocketChannel.bind(address);

            System.out.println("Server started. Listening on port 8080...");

            // добавляю обработчик MsgAcceptHandler, метод completed() которого будет вызван при успешном подключении
            // поток не блокируется, регистрирует обработчик, и сразу продолжает выполнять код ниже
            serverSocketChannel.accept(null, new MsgAcceptHandler(serverSocketChannel));

            // блокируем текущий поток
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



