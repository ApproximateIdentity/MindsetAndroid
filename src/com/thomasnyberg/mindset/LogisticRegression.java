package com.thomasnyberg.mindset;

import java.util.Random;

public class LogisticRegression {
  public double[][] data;
  public double[] coefficients;

  public double[] getCoefficients() {
    return coefficients;
  }

  public void fit() {
    /* Implement later */
    coefficients = new double[]{1.0, 2.0, 3.0};
  }

  public int predict(Integer[] data) {
    return new Random().nextInt(2) + 1;
  }
}
