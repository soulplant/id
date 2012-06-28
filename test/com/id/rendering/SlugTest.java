package com.id.rendering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SlugTest {

  @Test
  public void test() {
    Slug slug = new Slug(5);
    slug.setLetter(0, 'a');
    slug.setVisual(0, true);
    assertEquals('a', slug.getLetter(0));
    assertTrue(slug.isVisual(0));
  }
}
