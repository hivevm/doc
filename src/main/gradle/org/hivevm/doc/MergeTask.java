// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.*;
import org.gradle.api.tasks.options.Option;
import org.gradle.work.ChangeType;
import org.gradle.work.Incremental;
import org.gradle.work.InputChanges;
import org.hivevm.doc.md.MarkdownReader;
import org.hivevm.util.Resolver;

import javax.inject.Inject;
import java.io.*;

/**
 * The {@link MergeTask} class.
 */
public abstract class MergeTask extends DefaultTask {

    @Inject
    public MergeTask() {
        setGroup("HiveVM");
        setDescription("Merges a CommonMark document");
    }

    @Incremental
    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    @Option(option = "input", description = "The input directory")
    public abstract DirectoryProperty getInput();

    @OutputDirectory
    @Option(option = "output", description = "The output directory")
    public abstract DirectoryProperty getOutput();

    @TaskAction
    public void process(InputChanges changes) {
        System.out.println(changes.isIncremental() ? "Executing incrementally" : "Executing non-incrementally");

        changes.getFileChanges(getInput()).forEach(c -> {
            if (c.getChangeType() == ChangeType.REMOVED || !c.getFile().getName().endsWith(".md"))
                return;

            File source = c.getFile();
            Resolver resolver = new Resolver.PathResolver(source.getParentFile());
            File target = new File(getOutput().get().getAsFile(), source.getName());

            getLogger().warn("Merge '{}' into '{}'", source.getAbsolutePath(), target.getAbsolutePath());

            try (Reader reader = new FileReader(source);
                 Writer writer = new FileWriter(target)) {
                MarkdownReader md = new MarkdownReader(reader, resolver);
                writer.write(md.readAll());
            } catch (IOException e) {
                getLogger().error("Failed to merge the file", e);
            }
        });
    }
}