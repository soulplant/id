package com.id.session;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.id.file.File;

public class FileManager {
  private final Map<String, File> loadedFiles = new HashMap<String, File>();

  public File loadFile(String filename) {
    if (loadedFiles.containsKey(filename)) {
      return loadedFiles.get(filename);
    }
    File file = new File();
    try {
      FileReader fileReader = new FileReader(filename);
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        file.insertLine(file.getLineCount(), line);
      }
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    loadedFiles.put(filename, file);
    return file;
  }
}
