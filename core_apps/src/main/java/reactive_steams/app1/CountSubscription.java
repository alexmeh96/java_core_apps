package reactive_steams.app1;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class CountSubscription implements Subscription {
    private int counter = 1;
    private final int totalItems = 10;
    private boolean completed = false;
    private final Subscriber<Integer> subscriber;

    public CountSubscription(Subscriber<Integer> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void request(long l) {
        for (int i = 0; i < l && counter <= totalItems; i++) {
            subscriber.onNext(counter++);
        }
        if (counter > totalItems && !completed) {
            completed = true;
            subscriber.onComplete();
        }
    }

    @Override
    public void cancel() {

    }
}
