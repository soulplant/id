package com.id.platform;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.id.file.File;

public class RealFileSystem implements FileSystem {
  private final java.io.File workingDirectory;

  public RealFileSystem(java.io.File workingDirectory) {
    this.workingDirectory = workingDirectory;
  }

  public RealFileSystem() {
    this(null);
  }

  private java.io.File file(String path) {
    return new java.io.File(workingDirectory, path);
  }

  @Override
  public boolean isFile(String path) {
    return file(path).isFile();
  }

  @Override
  public boolean isDirectory(String path) {
    return file(path).isDirectory();
  }

  @Override
  public boolean isExistent(String path) {
    return file(path).exists();
  }

  @Override
  public File getFile(String path) {
    if (!isExistent("./" + path)) {
      return null;
    }
    return loadFile(path);
  }

  @Override
  public String[] getSubdirectories(String path) {
    return file(path).list();
  }

  private File loadFile(String filename) {
    File file;
    try {
      FileReader fileReader = new FileReader(file(filename));
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      file = File.loadFrom(bufferedReader);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    file.setFilename(filename);
    return file;
  }

  @Override
  public void save(File file) {
    if (file.getFilename() == null) {
      throw new IllegalArgumentException("Can't save a file that doesn't have a name");
    }
    save(file.getFilename(), file.getLines());
  }

  @Override
  public void save(String filename, String... contents) {
    try {
      FileWriter fileWriter = new FileWriter(file(filename));
      BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
      for (String line : contents) {
        bufferedWriter.write(line + "\n");
      }
      bufferedWriter.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
