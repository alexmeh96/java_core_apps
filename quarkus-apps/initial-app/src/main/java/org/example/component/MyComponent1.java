package org.example.component;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MyComponent1 {

    @Inject
    MyComponent2 myComponent2;

    public void method() {
        System.out.println("MyComponent1: method");
        myComponent2.method();
    }
}
