// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.Duration;

import javax.inject.Inject;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.internal.provider.DefaultProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.work.ChangeType;
import org.gradle.work.Incremental;
import org.gradle.work.InputChanges;

import org.hivevm.doc.commonmark.MarkdownReader;

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

      File file = c.getFile();
      MarkdownReader reader = new MarkdownReader(file);
      File target = new File(getOutput().get().getAsFile(), file.getName());
      getLogger().warn("Merge '{}' into '{}'", file.getAbsolutePath(), target.getAbsolutePath());
      try (Writer writer = new FileWriter(target)) {
        writer.write(reader.readAll());
      } catch (IOException e) {
        getLogger().error("Failed to merge the file", e);
      }
    });
  }
}