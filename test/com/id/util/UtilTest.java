package com.id.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import com.id.platform.InMemoryFileSystem;

public class UtilTest {
  private class Walker implements Util.FileWalker {
    public final Set<String> visitedFiles = new HashSet<String>();

    @Override
    public void visit(String filename) {
      visitedFiles.add(filename);
    }
  }

  @Test
  public void testFileWalker() {
    InMemoryFileSystem fileSystem = new InMemoryFileSystem();
    fileSystem.insertFile("dir/file1");
    fileSystem.insertFile("dir/file2");
    fileSystem.insertFile("dir2/file3");
    Walker walker = new Walker();
    Util.walkFiles(fileSystem, ".", walker);
    assertEquals(3, walker.visitedFiles.size());
  }
}
