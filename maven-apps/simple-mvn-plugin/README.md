Добавление плагина в мавен проект:
```xml
<plugin>
    <groupId>org.example</groupId>
    <artifactId>simple-mvn-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
    <executions>
        <execution>
            <goals>
                <goal>Hello</goal>
            </goals>
            <phase>validate</phase>
        </execution>
    </executions>
    <configuration>
        <message>Hello from pom!</message>
    </configuration>
</plugin>
```

Плагин custom, который имеет цель Hello, которая выводит на консоль сообщение переданное в теге *message*
![img_1.png](img_1.png)