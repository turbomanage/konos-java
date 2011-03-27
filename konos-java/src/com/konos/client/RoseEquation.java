package com.konos.client;

import com.google.gwt.gen2.client.IntegerSlider;
import com.konos.client.PolarEngine.PolarEquation;


/**
 * Renders 
 */
public class RoseEquation implements PolarEquation {

  protected double maxR;
  private IntegerSlider dSlider;
  private IntegerSlider nSlider;
  protected int n;
  protected int d;

  public RoseEquation(double maxradius, IntegerSlider nSlider, IntegerSlider dSlider) {
    this.maxR = maxradius;
    this.nSlider = nSlider;
    this.dSlider = dSlider;
  }

  @Override
  public double calcR(double theta) {
    return maxR * Math.cos(theta * n/d);
    // double r = maxradius * theta / (20 * Math.PI);
  }

  @Override
  public int numHalfTurns() {
    // Calc # of turns required
    // int nPi = (n%d>0)? d*(1+(n+d)%2) : 2-(n/d)%2;
    int f = gcf(n, d);
    if (f > 0) {
      n /= f;
      d /= f;
    }
    return (d > 1) ? d * (1 + (n + d) % 2) : 2 - n % 2;
  }
  
  private int gcf(int n2, int d2) {
    if (n==d)
      return n;
    int max = Math.max(n2, d2);
    for (int i = max / 2; i > 1; i--)
      if ((n2 % i == 0) && (d2 % i == 0))
        return i;
    return 0;
  }

  @Override
  public String getLabel() {
    return "Rose = r cos (t * n/d)";
  }

  @Override
  public void init() {
    this.n = nSlider.getValue().intValue();
    this.d = dSlider.getValue().intValue();
  }

}
