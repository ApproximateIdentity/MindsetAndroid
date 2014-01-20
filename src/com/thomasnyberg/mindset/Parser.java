package com.thomasnyberg.mindset;

import java.lang.Thread;
import java.io.InputStream;
import java.io.IOException;

public class Parser {
  /* PARSE CODE BYTES */
  private final int SYNC = 0xAA;

  /* I only consider payloads with this length. */
  private final int PLENGTH = 32; 

  Parser(InputStream stream) {
    dataStream = stream;
  }

  public Integer[] next() {
    int b = 0;

    boolean sync = false;
    boolean doubleSync = false;

    Integer[] payload;

    for (;;) {
      try {
        b = dataStream.read();
      } catch (IOException e) {
        /* Don't know how to handle this... */
      }

      if (doubleSync) {
        sync = false;
        doubleSync = false;

        if (b != PLENGTH) {
          continue;
        } else {
          payload = getPayload();
          break;
        }
      }

      if (b == SYNC) {
        if (sync) {
          doubleSync = true;
        }
        sync = true;
      }
    }

    return payload;
  }

  private Integer[] getPayload() {
    Integer[] payload = new Integer[11];
    int b = 0;

    /* Empty byte */
    try {
      b = dataStream.read();
    } catch (IOException e) {}

    /* Signal Quality */
    try {
      payload[0] = dataStream.read();
    } catch (IOException e) {}

    /* Empty byte */
    try {
      b = dataStream.read();
    } catch (IOException e) {}

    /* The next 24 bytes are 8 groups of 3 bytes. Each group of 3 bytes is an
       unsigned integer sent bigendian order. */
    for (int i = 0; i < 8; i++) {
      int value = 0;
      try {
        value = dataStream.read();
      } catch (IOException e) {}

      for (int j = 0; j < 2; j++) {
        try {
          value *= 256;
          value += dataStream.read();
        } catch (IOException e) {}
      }
      payload[i + 1] = value;
    }

    /* Empty byte */
    try {
      b = dataStream.read();
    } catch (IOException e) {}

    try {
      payload[9] = dataStream.read();
    } catch (IOException e) {}
    
    /* Empty byte */
    try {
      b = dataStream.read();
    } catch (IOException e) {}

    try {
      payload[10] = dataStream.read();
    } catch (IOException e) {}

    return payload;
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
