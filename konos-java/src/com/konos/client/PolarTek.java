package com.konos.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PolarTek implements EntryPoint {

  private Canvas canvas;
  protected int height;
  protected int width;
  
  
  public void onModuleLoad() {

    canvas = Canvas.createIfSupported();
    DockLayoutPanel dp = new DockLayoutPanel(Unit.PX);
    TabLayoutPanel tabLayoutPanel = new TabLayoutPanel(10, Unit.PCT);
    RenderEngine polarEngine = new PolarEngine(canvas, tabLayoutPanel);
    SpiroDraw spirograph = new SpiroDraw(canvas, tabLayoutPanel);
    tabLayoutPanel.add(spirograph.getControlPanel(), "SpiroDraw!");
    tabLayoutPanel.add(polarEngine.getControlPanel(), "PolarTek");
    dp.addWest(tabLayoutPanel, 200);
    dp.add(canvas);
    RootLayoutPanel.get().add(dp);

    polarEngine.refresh();
  }

}
