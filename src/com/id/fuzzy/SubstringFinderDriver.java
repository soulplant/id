package com.id.fuzzy;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.id.file.File;

public class SubstringFinderDriver implements FinderDriver {
  private final File file;

  public SubstringFinderDriver(File file) {
    this.file = file;
  }

  // FinderDriver.
  @Override
  public List<String> getMatches(String query) {
    List<String> result = new ArrayList<String>();
    Pattern pattern = Pattern.compile(".*" + query + ".*");
    for (int i = 0; i < file.getLineCount(); i++) {
      String candidate = file.getLine(i);
      Matcher matcher = pattern.matcher(candidate);
      if (matcher.matches()) {
        result.add(candidate);
      }
    }
    return result;
  }
}
