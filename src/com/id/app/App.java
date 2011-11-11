package com.id.app;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.id.app.FuzzyFinderFrame.Listener;
import com.id.editor.Editor;
import com.id.file.File;
import com.id.file.FileView;
import com.id.fuzzy.FuzzyFinder;
import com.id.platform.FileSystem;
import com.id.platform.RealFileSystem;

public class App implements Listener {
  private static final String APP_NAME = "id";
  private final FuzzyFinder fuzzyFinder;
  private final FileSystem fileSystem;
  private final EditorSwapperPanel editorSwapper;

  static int filenameIndex = 0;
  private final JFrame frame;
  private EditorPanel makeEditorPanel(String... contents) {
    File file = new File(contents);
    file.setFilename("<" + filenameIndex++ + ">");
    FileView fileView = new FileView(file);
    Editor editor = new Editor(fileView);
    EditorPanel panel = new EditorPanel(editor);
    return panel;
  }

  public App() {
    this.fileSystem = new RealFileSystem();
    this.fuzzyFinder = new FuzzyFinder(fileSystem);
    fuzzyFinder.addPathToIndex(".");

    frame = new JFrame(APP_NAME);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setFont(new Font("Monospaced.plain", Font.PLAIN, 12));

    editorSwapper = new EditorSwapperPanel();
    editorSwapper.addEditor(makeEditorPanel("first"));
    editorSwapper.addEditor(makeEditorPanel("second", "second"));
    frame.getContentPane().add(new FileListPanel(editorSwapper), BorderLayout.LINE_START);
    final EditorPanel stack = makeEditorPanel("stack");
    frame.getContentPane().add(editorSwapper, BorderLayout.CENTER);
    frame.getContentPane().add(stack, BorderLayout.LINE_END);
    frame.pack();
    frame.setVisible(true);

    frame.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
      }

      @Override
      public void keyReleased(KeyEvent e) {
      }

      @Override
      public void keyPressed(KeyEvent e) {
        // NOTE KeyEvent.getKeyCode() is only defined in keyPressed when control
        // is down.
        if (e.getKeyCode() == KeyEvent.VK_Q && e.isControlDown()) {
          System.exit(0);
        }
        boolean handled = editorSwapper.handleKeyPress(e);
        if (!handled) {
          switch (e.getKeyCode()) {
          case KeyEvent.VK_T:
            showFuzzyFinder();
            handled = true;
            break;
          }
        }
        if (handled) {
          frame.pack();
        }
      }
    });
  }

  private void showFuzzyFinder() {
    FuzzyFinderFrame frame = new FuzzyFinderFrame(fuzzyFinder, this);
    frame.setVisible(true);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        //    GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        //    Font[] fonts = e.getAllFonts(); // Get the fonts
        //    for (Font f : fonts) {
        //      System.out.println(f.getFontName());
        //    }
        //    System.exit(0);
        new App();
      }
    });
  }

  @Override
  public void onSelected(String item) {
    openFile(item);
  }

  private void openFile(String filename) {
    if (editorSwapper.focusByFilename(filename)) {
      frame.pack();
      return;
    }
    File file = fileSystem.getFile(filename);
    if (!file.getFilename().equals(filename)) {
      throw new IllegalStateException();
    }
    editorSwapper.addEditor(makeEditorPanel(file));
    editorSwapper.focusByFilename(filename);
    frame.pack();
  }

  private EditorPanel makeEditorPanel(File file) {
    return new EditorPanel(new Editor(new FileView(file)));
  }
}
