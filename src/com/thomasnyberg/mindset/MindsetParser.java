package com.thomasnyberg.mindset;

import java.lang.Thread;
import java.io.InputStream;

public class MindsetParser {
  MindsetParser(InputStream stream) {
    dataStream = stream;
  }

  public Integer[] next() {
    Integer[] data = new Integer[]{0,0,0,0,0,0,0,0};
    wasteSomeTime(1000);
    return data;
  }

  private InputStream dataStream;

  private void wasteSomeTime(int msec) {
    try {
      Thread.sleep(msec);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
