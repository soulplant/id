package com.id.platform;

import static org.junit.Assert.*;

import org.junit.Test;

public class InMemoryFileSystemTest {

  @Test
  public void test() {
    InMemoryFileSystem fileSystem = new InMemoryFileSystem();
    fileSystem.insertFile("this/is/a/test", "line 1", "line 2");
    assertTrue(fileSystem.isFile("this/is/a/test"));
    assertTrue(fileSystem.isDirectory("this"));
    assertEquals(1, fileSystem.getSubdirectories("this").length);
  }
}
