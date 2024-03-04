package nio.async_echo_server_app;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class MsgAcceptHandler implements CompletionHandler<AsynchronousSocketChannel, Void> {
    private final AsynchronousServerSocketChannel serverSocketChannel;
    MsgAcceptHandler(AsynchronousServerSocketChannel serverSocketChannel) {
        this.serverSocketChannel = serverSocketChannel;
    }
    @Override
    public void completed(AsynchronousSocketChannel clientChannel, Void attachment) {
        // продолжаем принимать следующее соединение, передавая в него текущий обработчик
        serverSocketChannel.accept(null, this);

        // обрабатываем подключённое соединение ассинхронно
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            // читаем данные с канала ассинхронно, передавая ему обработчик MsgReadHandler,
            // метод completed() которого будет вызван, при приходе данных на этот канал
            // текущий поток не блокируется
            clientChannel.read(buffer, null, new MsgReadHandler(clientChannel, buffer));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable exc, Void attachment) {
        System.err.println("Failed to accept connection: " + exc);
    }
}