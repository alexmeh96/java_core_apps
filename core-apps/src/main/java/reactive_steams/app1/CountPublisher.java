package reactive_steams.app1;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class CountPublisher implements Publisher<Integer> {

    @Override
    public void subscribe(Subscriber subscriber) {
        subscriber.onSubscribe(new CountSubscription(subscriber));
    }
}
