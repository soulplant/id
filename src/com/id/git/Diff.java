package com.id.git;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Diff {
  private final Map<String, FileDelta> fileDeltas;

  public Diff(Map<String, FileDelta> fileDeltas) {
    this.fileDeltas = fileDeltas;
  }

  public static Diff fromLines(List<String> lines) {
    Map<String, FileDelta> fileDeltas = new HashMap<String, FileDelta>();
    List<String> fileDeltaLines = new ArrayList<String>();
    String currentFile = null;
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      if (line.startsWith("diff --git")) {
        if (currentFile != null) {
          fileDeltas.put(currentFile, FileDelta.fromLines(fileDeltaLines));
        }
        fileDeltaLines.clear();
        currentFile = parseFilenameFromDiffHeader(line);
      }
      fileDeltaLines.add(line);
    }
    if (!fileDeltaLines.isEmpty()) {
      fileDeltas.put(currentFile, FileDelta.fromLines(fileDeltaLines));
    }
    return new Diff(fileDeltas);
  }

  private static String parseFilenameFromDiffHeader(String line) {
    return line.split(" ")[2].substring(2);
  }

  public int getFileCount() {
    return fileDeltas.size();
  }

  public FileDelta getDelta(String filename) {
    return fileDeltas.get(filename);
  }

  public Set<String> getModifiedFiles() {
    return fileDeltas.keySet();
  }

  @Override
  public String toString() {
    return "Diff[" + fileDeltas + "]";
  }
}
