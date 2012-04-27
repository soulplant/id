package com.id.file;

// Denotes a range of lines in a file.
public class Range {
  private int start;
  private int end;

  public Range(int start, int end) {
    this.start = start;
    this.end = end;
  }

  public int getStart() {
    return start;
  }

  public int getEnd() {
    return end;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Range)) {
      return false;
    }
    Range range = (Range) other;
    return range.start == start && range.end == end;
  }

  public boolean isOverlapping(Range other) {
    return contains(other.start)
        || contains(other.end)
        || other.contains(start);
  }

  public boolean contains(int x) {
    return x >= start && x <= end;
  }
}
