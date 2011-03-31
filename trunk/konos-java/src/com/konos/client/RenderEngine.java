package com.konos.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;

public abstract class RenderEngine {
  protected Canvas canvas;
  protected ValueChangeHandler<Double> refreshVCH;
  protected int height = 600;
  protected int width = 600;

  public RenderEngine(Canvas canvas) {
    this.canvas = canvas;
    this.refreshVCH = new ValueChangeHandler<Double>() {
      @Override
      public void onValueChange(ValueChangeEvent<Double> event) {
        refresh();
      }
    };
  }

  abstract Widget getControlPanel();

  abstract void refresh();

  abstract void drawFrame(int deg);

  abstract void start();
  
  public static int gcf(int n, int d) {
    if (n==d)
      return n;
    int max = Math.max(n, d);
    for (int i = max / 2; i > 1; i--)
      if ((n % i == 0) && (d % i == 0))
        return i;
    return 1;
  }
}