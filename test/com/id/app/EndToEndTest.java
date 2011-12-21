package com.id.app;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.id.editor.Editor;
import com.id.events.KeyStroke;
import com.id.file.Tombstone;
import com.id.fuzzy.FuzzyFinder;
import com.id.git.RealRepository;
import com.id.git.Repository;
import com.id.platform.RealFileSystem;

public class EndToEndTest {
  private ListModel<Editor> editors;
  private Repository repository;
  private FuzzyFinder fuzzyFinder;
  private Controller controller;
  private File tempDir;
  private RealFileSystem fileSystem;
  private RealShell shell;

  @Before
  public void setup() throws IOException {
    tempDir = createTempDirectory();
    shell = new RealShell(tempDir);
    File a = new File(tempDir, "a");
    a.createNewFile();
    initGitRepo();
    editors = new ListModel<Editor>();
    fileSystem = new RealFileSystem(tempDir);
    repository = new RealRepository(shell);
    fuzzyFinder = new FuzzyFinder(fileSystem);
    fuzzyFinder.addPathToIndex(".");
    controller = new Controller(editors, fileSystem, fuzzyFinder, repository);
  }

  @After
  public void shutdown() throws IOException {
    shell.exec("rm -rf " + tempDir.getAbsolutePath());
  }

  @Test
  public void openDiffsWith1() {
    controller.openFile("a");
    typeString("ithis is a test");
    type(KeyStroke.escape());
    type(KeyStroke.fromControlChar('s'));
    typeString("q1");
    Editor editor = editors.get(0);
    assertEquals("a", editor.getFilename());
    assertEquals(Tombstone.Status.NEW, editor.getStatus(0));
  }

  private void typeString(String string) {
    for (int i = 0; i < string.length(); i++) {
      char c = string.charAt(i);
      type(KeyStroke.fromChar(c));
    }
  }

  private void type(KeyStroke keyStroke) {
    controller.handleKeyStroke(keyStroke);
  }

  private File createTempDirectory() throws IOException {
    String tempPath = System.getProperty("java.io.tmpdir");
    File file = new File(tempPath, "idtest");
    if (!file.mkdir()) {
      exec("rm -rf " + file.getAbsolutePath());
      file.mkdir();
    }
    return file;
  }

  private void initGitRepo() throws IOException {
    shell.exec("ls .");
    shell.exec("pwd");
    shell.exec("git init");
    shell.exec("git add .");
    shell.exec("git commit -m initial");
    shell.exec("git show --stat");
  }

  private void exec(String string) throws IOException {
    Runtime.getRuntime().exec(string, null, tempDir);
  }

}
