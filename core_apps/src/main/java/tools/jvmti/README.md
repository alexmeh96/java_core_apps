JVMTI(JVM Tool Interface)

JVMTI - штука, которая позволяет загрузить код на c/c++ как динамическую библиотеку в jvm, тем самым расширив её функционал
не внося изменения в сам код jvm

Пример создая динамической библиотеки на c(агента) и подключение её при запуске java-программы

компилирование agent.c в динамическую библиотеку, с указанием расположения заголовочных файлов JVMTI:
```shel
gcc -fPIC -shared -o agent.so agent.c -I$JAVA_HOME/include -I$JAVA_HOME/include/linux
```
компилируем основную программу:
```shell
javac Main.java
```
указание агента(динамическую библиотеку), которая будет загружена в jvm:
```shell
java -agentpath:./agent.so Main
```
Jvm выполнит её перед основным кодом программы на java

### Динамическое подключение агента к работающей программе

```shel
gcc -fPIC -shared -o dynamic_agent.so dynamic_agent.c -I$JAVA_HOME/include -I$JAVA_HOME/include/linux
```
```shell
javac Main.java
```
вывод всех запущенных java-процессов и их pid:
```shell
jcmd
```
добавление агента к уже запушенному процессу:
```shell
jcmd <pid> JVMTI.agent_load ./dynamic_agent.so
```
