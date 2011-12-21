package com.id.git;

public interface Repository {
  String getHead();
  Diff getDiffTo(String rev);
  void commitAll(String message);
  void init();
}
