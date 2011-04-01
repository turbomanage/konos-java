package com.konos.client;

import com.google.gwt.gen2.client.IntegerSlider;

public class Sinusoid extends RoseEquation {

  public Sinusoid(double maxradius, IntegerSlider nSlider, IntegerSlider dSlider) {
    super(maxradius, nSlider, dSlider);
  }

  @Override
  public double calcR(double theta) {
    double rPrime = Math.pow(maxR, n) * Math.cos(theta * n / d);
    return Math.pow(rPrime, 1. / n);
  }

  @Override
  public int numHalfTurns() {
    return 2;
  }

  @Override
  public String getLabel() {
    return "Sinusoid";
  }

}