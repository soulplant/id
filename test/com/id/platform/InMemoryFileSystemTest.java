package com.id.platform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.id.file.File;

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
  public void bareNames() {
    fileSystem.insertFile("a", "line 1");
    assertTrue(fileSystem.isFile("a"));
    assertEquals(1, fileSystem.getSubdirectories("").length);
  }

  @Test
  public void gettingNonExistentFileShouldntCrash() {
    fileSystem.getFile("doesn't exist");
  }

  @Test
  public void saveFile() {
    fileSystem.insertFile("./a", "line1");
    File file = fileSystem.getFile("./a");
    file.changeLine(0, "changed");
    fileSystem.save(file);
    assertEquals("changed", fileSystem.getFile("./a").getLine(0));
  }

  @Test
  public void getFileOrNewFile() {
    File file = fileSystem.getFileOrNewFile("asdf");
    assertTrue(file.isEmpty());
    assertTrue(file.isModified());
    fileSystem.save(file);
    assertTrue(fileSystem.getFile("asdf").isEmpty());
  }
}
