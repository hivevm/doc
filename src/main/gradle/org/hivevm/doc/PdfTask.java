// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.File;
import java.util.Properties;

import javax.inject.Inject;

/**
 * The {@link PdfTask} class.
 */
public abstract class PdfTask extends DefaultTask {

  @Inject
  public PdfTask() {
    setGroup("HiveVM");
    setDescription("Creates PDF's from Markdown");
  }

  @InputDirectory
  @Optional
  @Option(option = "source", description = "The input folder containing markdown files.")
  public abstract DirectoryProperty getSource();

  @Input
  @Optional
  @Option(option = "template", description = "The template.")
  public abstract Property<String> getTemplate();

  @TaskAction
  public void process() {
    GradleConfig config = getProject().getExtensions().findByType(GradleConfig.class);
    File source = getFile((config == null) ? null : config.source);

    String template = getTemplate().getOrElse((config == null) ? ":DEFAULT:" : config.template);
    if (!template.isEmpty() && !template.startsWith(":") && !template.startsWith("/")) {
      template = new File(getProject().getRootDir(), template).getAbsolutePath();
    }

    DocumentBuilder builder = new DocumentBuilder(getProject().getRootDir());
    builder.setConfig(template);
    builder.setSource(getSource().getAsFile().getOrElse(source).getAbsolutePath());
    builder.setTarget(getProject().getBuildDir());

    builder.onInfo(m -> getLogger().info(m));
    builder.onError(t -> getLogger().error("An Error occured!", t));
    builder.addProperties(System.getProperties());
    getProject().getProperties().entrySet().stream().filter(e -> e.getValue() != null)
        .forEach(e -> builder.addProperty(e.getKey(), e.getValue()));

    // Adding GIT informations to the filename
    builder.setSuffix(PdfTask.getFileSuffix(builder.getProperties()));

    builder.build();
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
    return "";
  }
}