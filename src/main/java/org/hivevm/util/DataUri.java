// Copyright 2024 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

/**
 * The {@link DataUri} class.
 */
public abstract class DataUri {

  private static final String EMPTY =
      "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=";

  /**
   * Avoid an instance of {@link DataUri}.
   */
  private DataUri() {}

  /**
   * Get the {@link InputStream}.
   *
   * @param uri
   */
  private static final InputStream getInputStream(String uri) throws FileNotFoundException {
    return new File(uri).exists() ? new FileInputStream(uri) : DataUri.class.getResourceAsStream(uri);
  }

  /**
   * Converts the path to an URI.
   *
   * @param base
   * @param path
   */
  public static InputStream toInputStream(File base, String path) throws IOException {
    if (path.startsWith(":")) {
      return DataUri.class.getResourceAsStream("/" + path.substring(1));
    }
    return new FileInputStream(path.startsWith("/") ? new File(path) : new File(base, path));
  }

  /**
   * Converts the path to an URI.
   *
   * @param base
   * @param path
   */
  public static URI toURI(File base, String path) {
    if (path.startsWith(":")) {
      try {
        return DataUri.class.getResource("/" + path.substring(1)).toURI();
      } catch (URISyntaxException e) {
        e.printStackTrace();
      }
    }
    return path.startsWith("/") ? new File(path).toURI() : new File(base, path).toURI();
  }

  /**
   * Load an image resource from {@link ClassLoader} as DataURL.
   *
   * @param resource
   */
  public static String loadImage(String resource) {
    String contentType = resource.toLowerCase().endsWith(".png") ? "png" : "jpeg";
    try (InputStream stream = DataUri.getInputStream(resource)) {
      byte[] bytes = DataUri.readAllBytes(stream);
      String base64 = Base64.getEncoder().encodeToString(bytes);
      return String.format("data:image/%s;base64,%s", contentType, base64);
    } catch (IOException e) {}
    return DataUri.EMPTY;
  }

  /**
   * Load an image resource from {@link ClassLoader} as DataURL.
   *
   * @param uri
   */
  public static String loadImage(URI uri) {
    String contentType = uri.toString().toLowerCase().endsWith(".png") ? "png" : "jpeg";
    try (InputStream stream = uri.toURL().openStream()) {
      byte[] bytes = DataUri.readAllBytes(stream);
      String base64 = Base64.getEncoder().encodeToString(bytes);
      return String.format("data:image/%s;base64,%s", contentType, base64);
    } catch (IOException e) {}
    return DataUri.EMPTY;
  }

  /**
   * Read all bytes from an {@link InputStream}.
   *
   * @param stream
   */
  private static byte[] readAllBytes(InputStream stream) throws IOException {
    try (ByteArrayOutputStream byteArray = new ByteArrayOutputStream()) {
      int next = stream.read();
      while (next > -1) {
        byteArray.write(next);
        next = stream.read();
      }
      byteArray.flush();
      return byteArray.toByteArray();
    }
  }
}
