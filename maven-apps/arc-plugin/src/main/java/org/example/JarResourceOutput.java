package org.example;

import io.quarkus.arc.processor.ResourceOutput;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class JarResourceOutput implements ResourceOutput {

    private File jarFile;

    public JarResourceOutput(File jarFile) {
        this.jarFile = jarFile;
    }

    @Override
    public void writeResource(Resource resource) throws IOException {

        System.out.println("-----------------1");
        System.out.println(resource);
        System.out.println("-----------------2");
        System.out.println(resource.getType());
        System.out.println("-----------------3");
        System.out.println(resource.getName());

        switch (resource.getType()) {
            case JAVA_CLASS:
                createFile("build/" + resource.getName() + ".class", resource.getData());
                break;
            case SERVICE_PROVIDER:
//                jar.addFile(
//                        target.resolve("META-INF")
//                                .resolve("services")
//                                .resolve(resource.getName()).toString(),
//                        resource.getData());
                break;
            case JAVA_SOURCE:
                break;
        }
    }


    private static void createFile(String filePath, byte[] bytes) {
        File file = new File(filePath);
        File parentDir = file.getParentFile(); // Получаем родительскую директорию файла

        if (!parentDir.exists()) { // Проверяем, существует ли родительская директория
            parentDir.mkdirs(); // Создаем родительскую директорию и все промежуточные директории
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(bytes); // Запись массива байтов в файл
            System.out.println("File created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addClassToJar(String classFilePath, JarOutputStream jarOut) throws IOException {
        File classFile = new File(classFilePath);
        try (FileInputStream fis = new FileInputStream(classFile)) {
            JarEntry entry = new JarEntry(classFile.getName());
            jarOut.putNextEntry(entry);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                jarOut.write(buffer, 0, bytesRead);
            }
            jarOut.closeEntry();
        }
    }

}


