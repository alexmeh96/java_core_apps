package org.example;

import io.quarkus.runtime.QuarkusApplication;
import jakarta.inject.Inject;
import org.example.component.MyComponent1;


public class App implements QuarkusApplication {
    @Inject
    MyComponent1 myComponent1;

    @Override
    public int run(String... args) {
        System.out.println("Hello world");
        myComponent1.method();
        return 0;
    }
}
