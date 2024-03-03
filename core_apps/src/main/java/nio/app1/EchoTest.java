package nio.app1;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EchoTest {

//    EchoServer server;
    static EchoClient client;

    @Test
    public void echo_server_test() {
        System.out.println("QQQ!");
    }

    @BeforeAll
    public static void setup() throws IOException, InterruptedException {
        EchoServer.start();
        client = EchoClient.start();
    }

    @Test
    public void givenServerClient_whenServerEchosMessage_thenCorrect() throws InterruptedException {
        String resp1 = client.sendMessage("hello");
        String resp2 = client.sendMessage("world");
        assertEquals("hello", resp1);
        assertEquals("world", resp2);
    }

    @AfterAll
    public static void teardown() throws IOException {
//        server.destroy();
        EchoClient.stop();
    }


}
