package com.id.session;

import java.util.HashMap;
import java.util.Map;

import com.id.file.File;
import com.id.platform.FileSystem;

public class FileManager {
  private final Map<String, File> loadedFiles = new HashMap<String, File>();
  private final FileSystem fileSystem;

  public FileManager(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  public File loadFile(String filename) {
    if (loadedFiles.containsKey(filename)) {
      return loadedFiles.get(filename);
    }
    File file = fileSystem.getFile(filename);
    loadedFiles.put(filename, file);
    return file;
  }
}
