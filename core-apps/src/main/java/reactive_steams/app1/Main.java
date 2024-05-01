package reactive_steams.app1;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class Main {
    public static void main(String[] args) {
        Publisher<Integer> publisher = new CountPublisher();
        Subscriber<Integer> subscriber = new CountSubscriber();
        publisher.subscribe(subscriber);
    }
}

