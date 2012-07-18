package com.id.git;

import java.util.List;

public interface Repository {
  String getHead();
  Diff getDiffRelativeTo(String rev);
  void commitAll(String message);
  void init();
  List<String> getRevisionList();
}
