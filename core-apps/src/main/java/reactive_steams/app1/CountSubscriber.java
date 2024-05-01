package reactive_steams.app1;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class CountSubscriber implements Subscriber<Integer> {
    private Subscription subscription;
    private final int batchSize = 3;
    private int pendingRequests = batchSize;

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        // Request initial batch of items
        subscription.request(batchSize);
    }

    @Override
    public void onNext(Integer item) {
        // Process each emitted item
        System.out.println("Received: " + item);
        // Decrement pending requests
        pendingRequests--;
        // Request more items if necessary
        if (pendingRequests == 0) {
            pendingRequests = batchSize;
            subscription.request(batchSize);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        System.err.println("Error: " + throwable.getMessage());
    }

    @Override
    public void onComplete() {
        System.out.println("Processing completed");
    }
}
