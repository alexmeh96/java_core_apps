### Компиляция в native с помощью graalvm

```shell
javac -cp ../ Main.java Car.java
```
```shell
java -cp ../ graalvm.Main
```
```shell
native-image -cp ../ graalvm.Main app
```
```shell
./app
```