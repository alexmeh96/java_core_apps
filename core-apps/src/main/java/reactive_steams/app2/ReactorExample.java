package reactive_steams.app2;

import reactor.core.publisher.Flux;

public class ReactorExample {
    public static void main(String[] args) {
        // Create a Flux (Publisher) emitting integers from 1 to 10
        Flux<Integer> flux = Flux.range(1, 10);

        // Subscribe to the Flux and process each element asynchronously
        flux.subscribe(
                // onNext callback to process each element
                value -> System.out.println("Received: " + value),
                // onError callback to handle errors
                error -> System.err.println("Error: " + error),
                // onComplete callback to handle completion
                () -> System.out.println("Processing completed")
        );
    }
}
