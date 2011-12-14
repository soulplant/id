package com.id.app;

import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.id.app.ListModel.Listener;

@SuppressWarnings("serial")
public class ListPanel<T> extends JPanel implements Listener<T> {
  public interface Factory<T> {
    JComponent makeComponentFor(T t);
  }

  private final ListModel<T> model;
  private final Factory<T> factory;

  public ListPanel(ListModel<T> model, Factory<T> factory) {
    this.model = model;
    this.factory = factory;
    this.model.addListener(this);
    setLayout(new FlowLayout(FlowLayout.LEADING));
    for (int i = 0; i < model.size(); i++) {
      onAdded(i, model.get(i));
    }
    if (!model.isEmpty()) {
      onFocusChanged(model.getFocusedIndex(), model.getFocusedItem());
    }
  }

  @Override
  public void onAdded(int i, T t) {
    JComponent component = factory.makeComponentFor(model.get(i));
    add(component, i);
    revalidate();
  }

  @Override
  public void onFocusChanged(int i, T t) {

  }

  @Override
  public void onRemoved(int i, T t) {
    remove(i);
    revalidate();
  }

  @Override
  public void onFocusLost() {

  }

  public static void main(String[] args) {
    final ListModel<String> model = new ListModel<String>();
    ListPanel<String> listPanel = new ListPanel<String>(model, new Factory<String>() {
      @Override
      public JComponent makeComponentFor(String text) {
        return new JLabel(text);
      }
    });
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(listPanel);
    model.add("hi");
    model.add("there");
    model.removeFocused();
    frame.pack();
    frame.setVisible(true);
    frame.addMouseListener(new MouseListener() {
      @Override
      public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
      }

      @Override
      public void mousePressed(MouseEvent e) {
        model.add("woo!");
      }

      @Override
      public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
      }

      @Override
      public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
      }
    });
  }
}
