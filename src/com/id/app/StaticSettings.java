package com.id.app;

import java.util.List;

import com.id.util.Util;

public class StaticSettings {
  private String fontName = "Inconsolata.ttf";
  private float fontSize = 16;

  public StaticSettings() {
  }

  public void readFromFile(String filename) {
    if (!Util.isFile(filename)) {
      return;
    }
    List<String> settingsLines = Util.readFile(filename);
    for (String line : settingsLines) {
      String[] parts = line.split("=");
      if ("fontSize".equals(parts[0])) {
        this.fontSize = Float.parseFloat(parts[1]);
      } else if ("fontName".equals(parts[0])) {
        this.fontName = parts[1];
      }
    }
  }

  public String getFontName() {
    return fontName;
  }

  public float getFontSize() {
    return fontSize;
  }

  public static StaticSettings fromFile(String filename) {
    StaticSettings result = new StaticSettings();
    result.readFromFile(filename);
    return result;
  }
}
