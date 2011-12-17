package com.id.platform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class InMemoryFileSystemTest {
  private InMemoryFileSystem fileSystem;

  @Before
  public void setup() {
    fileSystem = new InMemoryFileSystem();
  }

  @Test
  public void test() {
    fileSystem.insertFile("this/is/a/test", "line 1", "line 2");
    assertTrue(fileSystem.isFile("this/is/a/test"));
    assertTrue(fileSystem.isDirectory("this"));
    assertEquals(1, fileSystem.getSubdirectories("this").length);
  }

  @Test
  public void gettingNonExistentFileShouldntCrash() {
    fileSystem.getFile("doesn't exist");
  }
}
