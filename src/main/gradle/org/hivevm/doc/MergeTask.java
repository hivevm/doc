// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import org.hivevm.doc.commonmark.MarkdownReader;
import org.hivevm.util.Replacer;

/**
 * The {@link MergeTask} class.
 */
public abstract class MergeTask extends DefaultTask {

  @Inject
  public MergeTask() {
    setGroup("HiveVM");
    setDescription("Merges a CommonMark document");
  }

  @Input
  @Option(option = "source", description = "The input file or folder containing markdown files.")
  public abstract Property<String> getSource();

  @InputDirectory
  @Option(option = "target", description = "The output folder.")
  public abstract DirectoryProperty getTarget();

  @TaskAction
  public void process() {
    GradleConfig config = getProject().getExtensions().findByType(GradleConfig.class);

    if (config == null) {
      getProject().getLogger().error("No configuration defined");
      return;
    }

    Map<String, String> properties = new HashMap<>();
    System.getProperties().forEach((k, v) -> properties.put(k.toString(), v.toString()));
    Replacer replacer = new Replacer(properties);

    // Collect all markdown files
    List<File> files = new ArrayList<>();
    File source = getFile(getSource().get());
    if (source.isDirectory()) {
      for (File file : source.listFiles()) {
        if (file.getName().toLowerCase().endsWith(".md")) {
          files.add(file);
        }
      }
    } else if (source.getName().toLowerCase().endsWith(".md")) {
      files.add(source);
    }

    for (File file : files) {
      MarkdownReader reader = new MarkdownReader(file);
      File target = new File(getTarget().get().getAsFile(), source.getName());
      getLogger().warn("Merge '{}' into '{}'", source.getAbsolutePath(), target.getAbsolutePath());
      try (Writer writer = new FileWriter(target)) {
        writer.write(replacer.replaceAll(reader.readAll()));
      } catch (IOException e) {
        getLogger().error("Failed to merge the file", e);
      }
    }
  }

  protected File getFile(String pathname) {
    if (pathname == null) {
      return null;
    }

    File file = new File(pathname);
    if (file.isAbsolute()) {
      return file;
    }

    File projectDir = getProject().getProjectDir();
    return new File(projectDir, pathname);
  }
}