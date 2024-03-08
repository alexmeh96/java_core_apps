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
добавление агента к уже запушенному процессу:
```shell
jcmd <pid> JVMTI.agent_load ./dynamic_agent.so
```

### Утилиты

вывод всех запущенных java-процессов и их pid(1-й способ):
```shell
jcmd
```
вывод всех запущенных java-процессов и их pid(2-й способ):
```shell
jps -l
```
получить дампа потоков
Она просит jvm предоставить дамп потоков. Jvm выполняет эту просьбу, на мгновении приостанавливая
все потоки в безопасной зоне и снимает дамп потоков, после чего потоки запускаются и продолжают свою работу
```shell
jstack <pid>
```
получить дампа потоков(нужно использовать когда jvm зависла и плохо работае)
Она полностью останавливает процесс, и читает стэки потоков, потом возобновляет работу процесса
```shell
jstack -F <pid>
```
получить дамп хипа
```shell
jmap -dump:live,format=b,file=heap.dump <pid>
```
построить гистограммы классов вместо дампа хипа целиком
```shell
jmap -histo <pid>
```
Снять дамп с мёртвых и зависших процессов
```shell
# снять дамп всего процесса
sudo gcore <pid>
# отправить дамп на аналих jmap
jmap -F -dump:format=b,file=heap.dump <java_path> <dump_file>
```


### Ресурсы
https://habr.com/ru/companies/odnoklassniki/articles/458812/

https://habr.com/ru/companies/jugru/articles/325064/