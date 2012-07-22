package com.id.app;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.SwingUtilities;

import com.id.editor.Editor;
import com.id.editor.EditorList;
import com.id.editor.Minibuffer;
import com.id.editor.Stack;
import com.id.editor.StackList;
import com.id.file.File;
import com.id.file.FileView;
import com.id.file.FilesRenameInterpreter;
import com.id.fuzzy.Finder;
import com.id.fuzzy.FuzzyFinderDriver;
import com.id.git.GitRepository;
import com.id.git.Repository;
import com.id.platform.FileSystem;
import com.id.platform.RealFileSystem;
import com.id.ui.ListModelBinder;
import com.id.ui.SpotlightView;
import com.id.ui.ViewContainer;
import com.id.ui.ViewFactory;
import com.id.ui.app.AppFrame;
import com.id.ui.app.AppPanel;
import com.id.ui.app.FileListView;
import com.id.ui.app.FinderPanel;
import com.id.ui.app.FullscreenSwapper;
import com.id.ui.app.StackView;
import com.id.ui.editor.EditorPanel;
import com.id.ui.editor.TextPanel;

public class App {
  public static final Font FONT = loadFont();

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        startApp();
      }
    });
  }

  private static <M, V> void bindList(ListModel<M> list,
      ViewFactory<M, V> viewFactory, ViewContainer<M, V> viewContainer) {
    ListModelBinder<M, V> binder = new ListModelBinder<M, V>(list, viewFactory, viewContainer);
    list.addListener(binder);
  }

  private static class EditorViewFactory implements ViewFactory<Editor, EditorPanel> {
    private final boolean showScrollbars;

    public EditorViewFactory(boolean showScrollbars) {
      this.showScrollbars = showScrollbars;
    }

    @Override
    public EditorPanel createView(Editor editor) {
      return new EditorPanel(editor, showScrollbars);
    }
  }

  private static void startApp() {
    final EditorList editorList = new EditorList();
    final StackList stackList = new StackList();
    Minibuffer minibuffer = new Minibuffer();
    CommandExecutor commandExecutor = new CommandExecutor();
    FileSystem fileSystem = new RealFileSystem();
    BashShell shell = new BashShell(null);
    Repository repository = new GitRepository(shell);
    File files = getFilesFile(fileSystem, shell);
    Finder finder = new Finder(files);
    HighlightState highlightState = new HighlightState();
    final Controller controller = new Controller(editorList, fileSystem,
        finder, repository, highlightState, stackList, minibuffer,
        commandExecutor, null, new FuzzyFinderDriver(files));

    SpotlightView<Editor, EditorPanel> spotlightView = new SpotlightView<Editor, EditorPanel>();
    bindList(editorList, new EditorViewFactory(true), spotlightView);

    final FileListView fileListView = new FileListView(editorList);

    SpotlightView<Stack, StackView> stackSpotlight = new SpotlightView<Stack, StackView>();
    bindList(stackList, new ViewFactory<Stack, StackView>() {
      @Override
      public StackView createView(Stack model) {
        StackView stackView = new StackView();
        bindList(model, new EditorViewFactory(false), stackView);
        return stackView;
      }
    }, stackSpotlight);
    TextPanel minibufferView = new TextPanel(minibuffer.getEditor());
    FinderPanel fuzzyFinderPanel = new FinderPanel(finder);
    final AppPanel panel = new AppPanel(fileListView, spotlightView, stackSpotlight,
        controller, fuzzyFinderPanel, minibufferView);

    controller.addListener(panel);

    editorList.addListener(fileListView);

    AppFrame fullscreenAppFrame = new AppFrame(panel, true);
    AppFrame normalAppFrame = new AppFrame(panel, false, new Dimension(1024, 768));

    new FullscreenSwapper(normalAppFrame, fullscreenAppFrame);
    controller.openFileView(new FileView(files));
  }

  private static File getFilesFile(final FileSystem fileSystem,
                                   final Shell shell) {
    File file = fileSystem.getFileOrNewFile(".files");
    file.setSaveAction(new FilesRenameInterpreter(fileSystem, shell));
    return file;
  }

  public static boolean isMacOS() {
    return "Mac OS X".equals(System.getProperty("os.name"));
  }

  public static void configureFont(Graphics g) {
    g.setFont(App.FONT);
    // For some reason antialiasing is really slow on Mac, and it antialiases
    // the text even when it's off anyway.
    if (!isMacOS()) {
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }
  }

  public static void dumpFonts() {
    GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
    Font[] fonts = e.getAllFonts(); // Get the fonts
    for (Font f : fonts) {
      System.out.println(f.getFontName());
    }
    System.exit(0);
  }

  public static Font loadFont() {
    Font font = null;
    try {
      ClassLoader classLoader = App.class.getClassLoader();
      URL fontUrl = classLoader.getResource("Inconsolata.ttf");
      InputStream fontInputStream = fontUrl.openStream();
      font = Font.createFont(Font.TRUETYPE_FONT, fontInputStream);
    } catch (FontFormatException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return font.deriveFont(13f);
  }
}
