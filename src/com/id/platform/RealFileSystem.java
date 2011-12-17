package com.id.platform;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.id.file.File;

public class RealFileSystem implements FileSystem {

  @Override
  public boolean isFile(String path) {
    return new java.io.File(path).isFile();
  }

  @Override
  public boolean isDirectory(String path) {
    return new java.io.File(path).isDirectory();
  }

  @Override
  public boolean isExistent(String path) {
    return new java.io.File(path).exists();
  }

  @Override
  public File getFile(String path) {
    if (!isExistent(path)) {
      return null;
    }
    return loadFile(path);
  }

  @Override
  public String[] getSubdirectories(String path) {
    return new java.io.File(path).list();
  }

  private File loadFile(String filename) {
    File file;
    try {
      FileReader fileReader = new FileReader(filename);
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      file = File.loadFrom(bufferedReader);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    file.setFilename(filename);
    return file;
  }
}
