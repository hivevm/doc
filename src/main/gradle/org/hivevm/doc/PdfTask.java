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
import org.hivevm.doc.fo.FoRequestHandler;
import org.hivevm.doc.fo.pdf.PdfRenderer;
import org.hivevm.doc.md.MarkdownRequestHandler;
import org.hivevm.doc.template.Template;
import org.hivevm.util.ReplacerRequestHandler;
import org.hivevm.util.lambda.RequestStreamBuilder;
import org.hivevm.util.lambda.RequestStreamHandler;

import javax.inject.Inject;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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

        String templatePath = getTemplate().getOrElse((config == null || config.template == null) ? ":default.ui.xml" : config.template);
        if (templatePath.startsWith(":") && templatePath.endsWith(":")) {
            templatePath = String.format("%s.ui.xml", templatePath.substring(0, templatePath.length() - 1).toLowerCase());
        } else if (!templatePath.isEmpty() && !templatePath.startsWith(":") && !templatePath.startsWith("/")) {
            templatePath = new File(getProject().getRootDir(), templatePath).getAbsolutePath();
        }

        Map<String, String> props = new HashMap<>();
        System.getProperties().entrySet().stream()
                .filter(e -> e.getValue() != null)
                .forEach(e -> props.put((String) e.getKey(), (String) e.getValue()));
//    getProject().getProperties().entrySet().stream()
//            .filter(e -> e.getValue() != null)
//            .forEach(e -> props.put((String) e.getKey(), (String) e.getValue()));

        try {
            Template template = Template.parse(templatePath, source);

            RequestStreamBuilder builder = new RequestStreamBuilder();
            builder.append(new MarkdownRequestHandler());
            builder.append(new ReplacerRequestHandler(props));
            builder.append(new FoRequestHandler(template, false));
            builder.append(new PdfRenderer(template));
            RequestStreamHandler handler = builder.build();

            for (File md : source.listFiles(f -> f.getName().endsWith(".md"))) {
                File output = new File(getProject().getBuildDir(), md.getName() + ".pdf");
                try (FileOutputStream ostream = new FileOutputStream(output)) {
                    try (InputStream istream = new FileInputStream(md)) {
                        handler.handleRequest(istream, ostream, md.getParentFile());
                    }
                }
            }
        } catch (IOException e) {
            getLogger().error("Failed to merge the file", e);
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