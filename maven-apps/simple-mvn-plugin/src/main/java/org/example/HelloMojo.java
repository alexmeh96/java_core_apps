package org.example;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

//определение цели
@Mojo(name = "Hello")
public class HelloMojo extends AbstractMojo {

    // передать параметр можно в теге <configuration> или через системную переменную
    // например: mvn validate -Dhello.message=myMessage
    @Parameter(defaultValue = "Hello from plugin!", property = "hello.message")
    private String message;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info(message);
    }
}