package com.id.util;

import java.net.URI;
import java.net.URISyntaxException;

public class StringUtils {
  public static String normalizePath(String filename) {
    String normalizedFilename = null;
    try {
      normalizedFilename = new URI(filename).normalize().getPath();
    } catch (URISyntaxException e) {
      return null;
    }
    return normalizedFilename;
  }
}
