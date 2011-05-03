package com.konos.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class RenderEngine {
  protected Canvas canvas;
  protected ValueChangeHandler<Double> refreshVCH;
  protected int height = 600;
  protected int width = 600;
  protected int yc;
  protected int xc;
  protected Context2d front;
  protected Context2d back;
  protected Canvas backCanvas;

  public RenderEngine(Canvas canvas) {
    this.canvas = canvas;
    this.refreshVCH = new ValueChangeHandler<Double>() {
      @Override
      public void onValueChange(ValueChangeEvent<Double> event) {
        refresh();
      }
    };
  }

  public RenderEngine(Canvas canvas, final TabLayoutPanel panel) {
    this(canvas);
    front = canvas.getContext2d();
    backCanvas = Canvas.createIfSupported();
    back = backCanvas.getContext2d();
    initControlPanel();
    panel.addSelectionHandler(new SelectionHandler<Integer>() {
      @Override
      public void onSelection(SelectionEvent<Integer> event) {
        int i = event.getSelectedItem();
        // TODO handle resize properly
//        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
//          @Override
//          public void execute() {
//            nSlider.onResize();
//            dSlider.onResize();
//          }
//        });
        if (panel.getWidget(i)==getControlPanel()) {
          refresh();
        } else {
          stop();
        }
      }
    });
    Window.addResizeHandler(new ResizeHandler() {
      @Override
      public void onResize(ResizeEvent event) {
        RenderEngine.this.onResize();
        refresh();
      }
    });
    onResize();
  }
  
  abstract void stop();

  abstract Widget getControlPanel();

  abstract void refresh();

  abstract void drawFrame(int deg);

  abstract void start();
  
  public void onResize() {
    height = Window.getClientHeight();
    width = Window.getClientWidth() - 200;
    yc = height/2;
    xc = width/2;
    canvas.setCoordinateSpaceHeight(height);
    canvas.setCoordinateSpaceWidth(width);
    backCanvas.setCoordinateSpaceHeight(height);
    backCanvas.setCoordinateSpaceWidth(width);
  }

  public static int gcf(int n, int d) {
    if (n==d)
      return n;
    int max = Math.max(n, d);
    for (int i = max / 2; i > 1; i--)
      if ((n % i == 0) && (d % i == 0))
        return i;
    return 1;
  }

  protected void initControlPanel() {
    // TODO Auto-generated method stub
    
  }
}