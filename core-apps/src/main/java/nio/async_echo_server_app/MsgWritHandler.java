package nio.async_echo_server_app;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class MsgWritHandler implements CompletionHandler<Integer, Void> {
    private final AsynchronousSocketChannel clientChannel;
    private final ByteBuffer buffer;
    private final CompletionHandler<Integer, Void> readHandler;
    MsgWritHandler(AsynchronousSocketChannel clientChannel, ByteBuffer buffer, CompletionHandler<Integer, Void> readHandler) {
        this.clientChannel = clientChannel;
        this.buffer = buffer;
        this.readHandler = readHandler;
    }
    @Override
    public void completed(Integer result, Void attachment) {
        // продолжаем чтение данных поступающие от клиента, передавая каналу обработчик на чтение
        buffer.clear();
        clientChannel.read(buffer, null, readHandler);
    }
    @Override
    public void failed(Throwable exc, Void attachment) {
        System.err.println("Failed to write data to client: " + exc);
    }
}