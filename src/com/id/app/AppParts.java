package com.id.app;

import java.awt.Dimension;

import com.id.editor.Editor;
import com.id.editor.EditorList;
import com.id.editor.Hideable;
import com.id.editor.Minibuffer;
import com.id.editor.Register;
import com.id.editor.SharedEditorSettings;
import com.id.editor.Stack;
import com.id.editor.StackList;
import com.id.file.File;
import com.id.file.FileView;
import com.id.fuzzy.Finder;
import com.id.fuzzy.FuzzyFinderDriver;
import com.id.git.GitRepository;
import com.id.git.Repository;
import com.id.platform.FileSystem;
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

public class AppParts {
  public final Controller controller;
  public final EditorList editorList;
  private final StackList stackList;
  private final Minibuffer minibuffer;
  private final Finder finder;
  private final EditorOpener editorOpener;
  private final File files;

  public AppParts(FileSystem fileSystem, Shell shell, File files) {
    this.files = files;
    editorList = new EditorList();
    stackList = new StackList();
    minibuffer = new Minibuffer();
    Repository repository = new GitRepository(shell);
    finder = new Finder(files);
    HighlightState highlightState = new HighlightState();
    FocusManager focusManager = new FocusManager(editorList, stackList);
    ViewportTracker viewportTracker = new ViewportTracker(focusManager);

    Register register = new Register();
    SharedEditorSettings editorSettings = new SharedEditorSettings();
    EditorFactory editorFactory = new EditorFactory(highlightState, register,
        viewportTracker, editorSettings);
    editorOpener = new EditorOpener(editorFactory, focusManager,
        editorList, stackList, fileSystem, finder);

    CommandExecutor commandExecutor = new CommandExecutor(editorOpener, focusManager);
    MinibufferSubsystem minibufferSubsystem = new MinibufferSubsystem(
        minibuffer, commandExecutor, focusManager);
    controller = new Controller(editorList, fileSystem,
        finder, repository, highlightState, stackList, minibufferSubsystem,
        commandExecutor, null, new FuzzyFinderDriver(files), focusManager,
        editorOpener, editorSettings);
  }

  private static class EditorViewFactory implements ViewFactory<Editor, EditorPanel> {
    private final boolean selfScrolling;

    public EditorViewFactory(boolean selfScrolling) {
      this.selfScrolling = selfScrolling;
    }

    @Override
    public EditorPanel createView(Editor editor) {
      EditorPanel editorPanel = new EditorPanel(editor, selfScrolling);
      return editorPanel;
    }
  }

  private static <M, V> void bindList(ListModel<M> list,
      ViewFactory<M, V> viewFactory, ViewContainer<M, V> viewContainer) {
    ListModelBinder<M, V> binder = new ListModelBinder<M, V>(list, viewFactory, viewContainer);
    list.addListener(binder);
    if (list instanceof Hideable && viewContainer instanceof Hideable.Listener) {
      Hideable tracked = (Hideable) list;
      Hideable.Listener listener = (Hideable.Listener) viewContainer;
      tracked.addHideableListener(listener);
    }
  }

  public void showSwingView() {
    SpotlightView<Editor, EditorPanel> spotlightView = new SpotlightView<Editor, EditorPanel>();
    bindList(editorList, new EditorViewFactory(true), spotlightView);

    final FileListView fileListView = new FileListView(editorList);

    final SpotlightView<Stack, StackView> stackSpotlight = new SpotlightView<Stack, StackView>();
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

    editorList.addListener(fileListView);

    AppFrame fullscreenAppFrame = new AppFrame(panel, true);
    AppFrame normalAppFrame = new AppFrame(panel, false, new Dimension(1024, 768));

    new FullscreenSwapper(normalAppFrame, fullscreenAppFrame);
  }

  public void openFiles() {
    editorOpener.openFileView(new FileView(files));
  }
}
