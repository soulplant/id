package com.id.fuzzy;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.id.platform.FileSystem;

public class FuzzyFinder {
  private final List<String> paths = new ArrayList<String>();
  private final List<String> filenames = new ArrayList<String>();
  private final FileSystem fileSystem;

  public FuzzyFinder(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  public void addPathToIndex(String path) {
    paths.add(path);
    addAllFilesUnder(path);
  }

  public List<String> getMatches(String query) {
    List<String> result = new ArrayList<String>();
    Pattern pattern = Pattern.compile(".*" + query + ".*");
    for (String candidate : filenames) {
      Matcher matcher = pattern.matcher(candidate);
      if (matcher.matches()) {
        result.add(candidate);
      }
    }
    return result;
  }

  private void addAllFilesUnder(String path) {
    if (!fileSystem.isExistent(path)) {
      return;
    }
    if (fileSystem.isDirectory(path)) {
      for (String filename : fileSystem.getSubdirectories(path)) {
        String subDirectory = path + "/" + filename;
        addAllFilesUnder(subDirectory);
      }
    } else if (fileSystem.isFile(path)) {
      filenames.add(path);
    }
  }

  public boolean contains(String filename) {
    return filenames.contains(filename);
  }
}
