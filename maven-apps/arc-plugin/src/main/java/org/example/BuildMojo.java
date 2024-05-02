package org.example;


import io.quarkus.arc.processor.BeanProcessor;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.jboss.jandex.Index;
import org.jboss.jandex.Indexer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Mojo(name = "build", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)
public class BuildMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    // add all .class files to index
    private static void indexJar(String jarFilePath, Indexer indexer) {
        System.out.println(jarFilePath);
        try (ZipInputStream zip = new ZipInputStream(new BufferedInputStream(new FileInputStream(jarFilePath)))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    indexer.index(zip);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void execute() {
        Indexer indexer = new Indexer();

        // Индексация классов в jar файле
        indexJar(project.getArtifact().getFile().getAbsolutePath(), indexer);

        for (Artifact artifact : project.getArtifacts()) {
            indexJar(artifact.getFile().getAbsolutePath(), indexer);
        }

        Index index = indexer.complete();

//            index.getKnownClasses().stream().filter(classInfo -> classInfo.isAnnotation()).forEach(classInfo -> {
//                System.out.println(classInfo.name());
//            });


        BeanProcessor build = BeanProcessor.builder()
                .setApplicationIndex(index)
                .setComputingBeanArchiveIndex(index)
                .setImmutableBeanArchiveIndex(index)
                .setGenerateSources(true)
                .setOutput(new JarResourceOutput(project.getArtifact().getFile()))
                .setTransformUnproxyableClasses(true)
                .build();

        try {
            build.process();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}