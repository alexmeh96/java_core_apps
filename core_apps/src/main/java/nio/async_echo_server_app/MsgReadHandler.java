package nio.async_echo_server_app;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class MsgReadHandler implements CompletionHandler<Integer, Void> {
    private final AsynchronousSocketChannel clientChannel;
    private final ByteBuffer buffer;
    MsgReadHandler(AsynchronousSocketChannel clientChannel, ByteBuffer buffer) {
        this.clientChannel = clientChannel;
        this.buffer = buffer;
    }
    @Override
    public void completed(Integer result, Void attachment) {
        if (result > 0) {
            buffer.flip();
            byte[] data = new byte[buffer.limit()];
            buffer.get(data);
            System.out.println("Received from client: " + new String(data));

            // записываем данные в канал, для отправки их клиенту, передавая обработчик MsgWritHandler,
            // метод которого completed() будет вызван, при успешной отправки
            // текущий поток не блокируется
            clientChannel.write(ByteBuffer.wrap(data), null, new MsgWritHandler(clientChannel, buffer, this));
        } else {
            // соединение закрыто клиентом
            try {
                clientChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void failed(Throwable exc, Void attachment) {
        System.err.println("Failed to read data from client: " + exc);
    }
}