package reactive_steams.app2;

import java.util.concurrent.Flow.*;
import java.util.concurrent.SubmissionPublisher;

public class ReactiveStreamsExample2 {
    public static void main(String[] args) throws InterruptedException {
        // Create a SubmissionPublisher (Publisher) emitting integers
        SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>();

        // Create a Subscriber to process emitted integers
        Subscriber<Integer> subscriber = new Subscriber<>() {
            private Subscription subscription;
            private final int MAX_ITEMS = 3; // Max items to process at a time

            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                // Request initial items from the Publisher
                subscription.request(MAX_ITEMS);
            }

            @Override
            public void onNext(Integer item) {
                // Simulate processing time
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                // Print the processed item
                System.out.println("Processed item: " + item);
                // Request more items if necessary
                subscription.request(MAX_ITEMS);
            }

            @Override
            public void onError(Throwable throwable) {
                // Handle errors
                System.out.println("onError");
            }

            @Override
            public void onComplete() {
                // Handle completion
                System.out.println("onComplete");
            }
        };

        // Subscribe the Subscriber to the Publisher
        publisher.subscribe(subscriber);

        // Emit integers asynchronously
        for (int i = 1; i <= 10; i++) {
            publisher.submit(i);
        }

        // Wait for processing to complete
        Thread.sleep(15000);

        // Close the publisher
        publisher.close();
    }
}
