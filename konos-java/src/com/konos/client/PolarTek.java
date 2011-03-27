package com.konos.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PolarTek implements EntryPoint {

  private Canvas canvas;
  protected int height = 600;
  protected int width = 600;
  
  
  public void onModuleLoad() {

    canvas = Canvas.createIfSupported();
    canvas.setPixelSize(width, height);
    DockLayoutPanel dp = new DockLayoutPanel(Unit.PCT);
    dp.setPixelSize(800, 600);
    canvas.setCoordinateSpaceHeight(height);
    canvas.setCoordinateSpaceWidth(width);

    RenderEngine engine = new PolarEngine(canvas);
    dp.addWest(engine.getControlPanel(), 25);
    dp.add(canvas);
    RootLayoutPanel.get().add(dp);

    engine.refresh();
  }

}
