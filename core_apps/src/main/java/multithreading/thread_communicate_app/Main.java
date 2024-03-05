package multithreading.thread_communicate_app;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.util.concurrent.CountDownLatch;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        var pipe = Pipe.open();

        // создаю канал для записи
        var sinkChannel = pipe.sink();
        // создаю какнал для чтения
        var sourceChannel = pipe.source();

        // счётчик потоков
        var countTh = new CountDownLatch(3);

        new Thread(() -> getUser(sinkChannel, countTh)).start();
        new Thread(() -> getInfo(sinkChannel, countTh)).start();
        new Thread(() -> getWallet(sinkChannel, countTh)).start();

        countTh.await();

        var buffer = ByteBuffer.allocate(128);
        //читаю данные из канала в буффер
        sourceChannel.read(buffer);
        buffer.flip();
        byte[] data = new byte[buffer.limit()];
        buffer.get(data);
        System.out.printf("Received methods:\n%s\n", new String(data));
        buffer.clear();
    }

    static void getUser(Pipe.SinkChannel ch, CountDownLatch countTh) {
        try {
            Thread.sleep(300);

            var buffer = ByteBuffer.allocate(128);
            buffer.clear();
            buffer.put("method1\n".getBytes());
            buffer.flip();
            // записываю данные в канал
            ch.write(buffer);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            countTh.countDown();
        }

    }

    static void getInfo(Pipe.SinkChannel ch, CountDownLatch countTh) {
        try {
            Thread.sleep(50);

            var buffer = ByteBuffer.allocate(128);
            buffer.clear();
            buffer.put("method2\n".getBytes());
            buffer.flip();
            ch.write(buffer);

        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            countTh.countDown();
        }
    }

    static void getWallet(Pipe.SinkChannel ch, CountDownLatch countTh) {
        try {
            Thread.sleep(600);

            var buffer = ByteBuffer.allocate(128);
            buffer.clear();
            buffer.put("method3\n".getBytes());
            buffer.flip();
            ch.write(buffer);

        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            countTh.countDown();
        }
    }
}
