package com.id.app;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.id.app.FuzzyFinderFrame.Listener;
import com.id.editor.Editor;
import com.id.file.File;
import com.id.file.FileView;
import com.id.fuzzy.FuzzyFinder;
import com.id.platform.FileSystem;
import com.id.platform.RealFileSystem;
import com.id.ui.editor.EditorPanel;

public class App implements Listener {
  private static final String APP_NAME = "id";
  public static final Font FONT = loadFont();
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

  public static Font loadFont() {
    Font font = null;
    try {
      font = Font.createFont(Font.TRUETYPE_FONT, new java.io.File(
          "resources/Inconsolata.ttf"));
    } catch (FontFormatException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return font.deriveFont(12f);
  }

  public App() {
    this.fileSystem = new RealFileSystem();
    this.fuzzyFinder = new FuzzyFinder(fileSystem);
    fuzzyFinder.addPathToIndex(".");

    frame = new JFrame(APP_NAME);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new AppLayout());

    editorSwapper = new EditorSwapperPanel();
    editorSwapper.addEditor(makeEditorPanel("first"));
    editorSwapper.addEditor(makeEditorPanel("second", "second"));
    frame.getContentPane().add(new FileListPanel(editorSwapper), "filelist");
    final EditorPanel stack = makeEditorPanel("stack");
    frame.getContentPane().add(editorSwapper, "spotlight");
    frame.getContentPane().add(stack, "stack");
    frame.setSize(new Dimension(1024, 768));
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

    frame.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        frame.pack();
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

  public static void configureFont(Graphics g) {
    g.setFont(App.FONT);
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
  }
}
