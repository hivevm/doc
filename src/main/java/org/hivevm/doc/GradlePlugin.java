// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.hivevm.doc.commonmark.MarkdownReader;
import org.hivevm.doc.util.Replacer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GradlePlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    GradleConfig config = project.getExtensions().create("markdown", GradleConfig.class);

    project.task("markdown").doLast(task -> buildMarkdown(config, project));
    project.task("md-merge").doLast(task -> mergeMarkdown(config, project));
  }

  /**
   * Generates the PDF from the markdown.
   *
   * @param gradle
   * @param project
   */
  private void buildMarkdown(GradleConfig gradle, Project project) {
    String config = gradle.config;
    if (!config.isEmpty() && !config.startsWith(":") && !config.startsWith("/")) {
      config = new File(project.getRootDir(), config).getAbsolutePath();
    }

    DocumentBuilder builder = new DocumentBuilder(project.getRootDir());
    builder.setConfig(config);
    builder.setSource(gradle.source);
    builder.setTarget(project.getBuildDir());

    builder.onInfo(m -> project.getLogger().info(m));
    builder.onError(t -> project.getLogger().error("An Error occured!", t));
    builder.addProperties(System.getProperties());
    project.getProperties().entrySet().stream().filter(e -> e.getValue() != null)
        .forEach(e -> builder.addProperty(e.getKey(), e.getValue()));

    // Adding GIT informations to the filename
    builder.setSuffix(GradlePlugin.getFileSuffix(builder.getProperties()));

    builder.build();
  }

  /**
   * Generates the PDF from the markdown.
   *
   * @param gradle
   * @param project
   */
  private void mergeMarkdown(GradleConfig gradle, Project project) {
    // Load properties
    Properties properties = new Properties();
    project.getProperties().entrySet().stream().filter(e -> e.getValue() != null)
        .forEach(e -> properties.put(e.getKey(), e.getValue()));
    properties.putAll(System.getProperties());
    Replacer replacer = new Replacer(properties);

    // Collect all markdown files
    List<File> files = new ArrayList<>();
    File source = new File(project.getRootDir(), gradle.source);
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
      File target = new File(project.getBuildDir(), file.getName());
      try (Writer writer = new FileWriter(target)) {
        writer.write(replacer.replaceAll(reader.readAll()));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Gets the file-suffix from the {@link Properties}.
   *
   * @param properties
   */
  private static String getFileSuffix(Properties properties) {
    if (properties.containsKey("GIT_VERSION")) {
      String suffix = "-" + properties.get("GIT_VERSION");
      if (properties.containsKey("BUILD_NUMBER")) {
        suffix += "+" + properties.get("BUILD_NUMBER");
      }
      return suffix;
    } else if (properties.containsKey("git.version")) {
      String suffix = "-" + properties.get("git.version");
      if (properties.containsKey("git.buildnumber")) {
        suffix += "+" + properties.get("git.buildnumber");
      }
      return suffix;
    }
    return null;
  }
}
