package com.id.git;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileDelta {
  private final Map<Integer, String> additions = new HashMap<Integer, String>();
  private final Map<Integer, List<String>> deletions = new HashMap<Integer, List<String>>();

  public FileDelta() {
  }

  public void addNewLine(int y, String line) {
    additions.put(y, line);
  }

  public void addDeletedLine(int y, String line) {
    if (!deletions.containsKey(y)) {
      deletions.put(y, new ArrayList<String>());
    }
    deletions.get(y).add(line);
  }

  public static FileDelta fromLines(List<String> fileDeltaLines) {
    FileDelta result = new FileDelta();
    boolean atHunk = false;
    int offset = 0;

    for (String line : fileDeltaLines) {
      if (line.startsWith("@@")) {
        atHunk = true;
        offset = parseOffset(line);
        continue;
      }
      if (!atHunk) {
        continue;
      }
      String text = line.substring(1);
      if (line.startsWith("+")) {
        result.addNewLine(offset, text);
        offset++;
      } else if (line.startsWith("-")) {
        result.addDeletedLine(offset - 1, text);
      } else {
        offset++;
      }
    }
    return result;
  }

  private static int parseOffset(String line) {
    // We want the third int out of, eg:
    // @@ -6,7 +6,9 @@ import java.awt.Graphics;
    // NOTE(koz): We subtract one because patches used 1-based indexing,
    // but our code uses 0-based indexing.
    return Integer.parseInt(line.split(" ")[2].substring(1).split(",")[0]) - 1;
  }

  public Map<Integer, String> getAdditions() {
    return additions;
  }

  public Map<Integer, List<String>> getDeletions() {
    return deletions;
  }

  @Override
  public String toString() {
    return "FileDelta[" + additions + ", " + deletions + "]";
  }
}
