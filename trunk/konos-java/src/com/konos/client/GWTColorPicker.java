package com.konos.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.ui.Composite;

public class GWTColorPicker extends Composite implements MouseMoveHandler, MouseDownHandler {

  private Canvas canvas = Canvas.createIfSupported();
  private CssColor selectedColor = CssColor.make("red");
  private Context2d ctx;

  public GWTColorPicker() {
    canvas.setPixelSize(180, 160);
    canvas.setCoordinateSpaceWidth(180);
    canvas.setCoordinateSpaceHeight(160);
    ctx = canvas.getContext2d();
    drawPalette();
    addHandlers();
    showSelected();
    initWidget(canvas);
  }

  private void drawPalette() {
    int i=0;
    for (int r=0; r<256; r+=51) {
      for (int g=0; g<256; g+=51) {
        for (int b=0; b<256; b+=51) {
          CssColor color = CssColor.make(r, g, b);
          ctx.setFillStyle(color);
          int x = 10 * (i%18);
          int y = 10 * (i/18) + 40;
          ctx.fillRect(x+1, y+1, 8, 8);
          i++;
        }
      }
    }
  }

  private void addHandlers() {
    canvas.addMouseMoveHandler(this);
    canvas.addMouseDownHandler(this);
  }

  public CssColor getSelectedColor() {
    return selectedColor;
  }

  @Override
  public void onMouseMove(MouseMoveEvent event) {
    int x = event.getX();
    int y = event.getY() - 40;
    if ((y<0) || (x>179))
      return;
    ctx.setFillStyle(getColor(x, y));
    ctx.fillRect(0, 0, 90, 30);
  }

  @Override
  public void onMouseDown(MouseDownEvent event) {
    int x = event.getX();
    int y = event.getY() - 40;
    if ((y<0) || (x>179))
      return;
    selectedColor = getColor(x, y);
    showSelected();
  }

  private CssColor getColor(int x, int y) {
    // Get color index 0-215 using row, col
    int i = y / 10 * 18 + x / 10;
    // Convert to RGB using modulus
    int r = i / 36 * 51;
    int g = (i % 36) / 6 * 51;
    int b = i % 6 * 51;
    return CssColor.make(r, g, b);
  }

  private void showSelected() {
    ctx.setFillStyle(selectedColor);
    ctx.fillRect(90, 0, 90, 30);
    ctx.setFillStyle("white");
    ctx.fillRect(0, 0, 90, 30);
  }
  
}
