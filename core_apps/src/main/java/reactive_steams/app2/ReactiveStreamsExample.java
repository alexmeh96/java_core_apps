package reactive_steams.app2;

import java.util.concurrent.Flow.*;

public class ReactiveStreamsExample {
    public static void main(String[] args) {
        Publisher<Integer> publisher = new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                for (int i = 1; i <= 10; i++) {
                    subscriber.onNext(i);
                }
                subscriber.onComplete();
            }
        };

        Subscriber<Integer> subscriber = new Subscriber<>() {
            private Subscription subscription;

            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer item) {
                System.out.println("Received: " + item);
            }

            @Override
            public void onError(Throwable throwable) {
                System.err.println("Error: " + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("Processing completed");
            }
        };

        publisher.subscribe(subscriber);
    }
}
