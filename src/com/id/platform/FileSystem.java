package com.id.platform;

import com.id.file.File;

public interface FileSystem {
  boolean isFile(String path);
  boolean isDirectory(String path);
  boolean isExistent(String path);
  File getFile(String path);
  String[] getSubdirectories(String path);
  void save(File file);
}
