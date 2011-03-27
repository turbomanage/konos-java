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
}