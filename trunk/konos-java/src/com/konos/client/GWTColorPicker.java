package com.konos.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;

public class GWTColorPicker extends Composite implements MouseMoveHandler, MouseDownHandler, HasSelectionHandlers<String> {

  private static final int COLS = 18;
  // Block height, width, padding
  private static final int BH = 10;
  private static final int BW = 10;
  private static final int BP = 1;
  private Canvas canvas = Canvas.createIfSupported();
  private CssColor selectedColor = CssColor.make("red");
  private int height = 160;
  private int width = 180;
  private Context2d ctx;

  public GWTColorPicker() {
    canvas.getElement().setId("palette");
    canvas.setPixelSize(width, height);
    canvas.setCoordinateSpaceWidth(width);
    canvas.setCoordinateSpaceHeight(height);
    ctx = canvas.getContext2d();
    drawPalette();
    addHandlers();
    showSelected();
    initWidget(canvas);
  }

  public CssColor getSelectedColor() {
    return selectedColor;
  }

  @Override
  public void onMouseMove(MouseMoveEvent event) {
    int x = event.getX();
    int y = event.getY() - 40;
    if ((y<0) || (x>=width))
      return;
    ctx.setFillStyle(getColor(x, y));
    ctx.fillRect(0, 0, width/2, 30);
  }

  @Override
  public void onMouseDown(MouseDownEvent event) {
    int x = event.getX();
    int y = event.getY() - 40;
    if ((y<0) || (x>=width))
      return;
    selectedColor = getColor(x, y);
    showSelected();
    fireSelected(selectedColor);
  }

  @Override
  public HandlerRegistration addSelectionHandler(SelectionHandler<String> handler) {
    return super.addHandler(handler, SelectionEvent.getType());
  }

  private void addHandlers() {
    canvas.addMouseMoveHandler(this);
    canvas.addMouseDownHandler(this);
  }

  private void drawPalette() {
    int i=0;
    for (int r=0; r<256; r+=51) {
      for (int g=0; g<256; g+=51) {
        for (int b=0; b<256; b+=51) {
          CssColor color = CssColor.make(r, g, b);
          ctx.setFillStyle(color);
          int x = BW * (i%COLS);
          int y = BH * (i/COLS) + 40;
          ctx.fillRect(x+BP, y+BP, BW-2*BP, BH-2*BP);
          i++;
        }
      }
    }
  }

  private void fireSelected(CssColor color) {
      SelectionEvent.fire(this, color.value());
  }

  private CssColor getColor(int x, int y) {
    // Get color index 0-215 using row, col
    int i = y / BH * COLS + x / BW;
    // Convert to RGB using modulus
    int r = i / 36 * 51;
    int g = (i % 36) / 6 * 51;
    int b = i % 6 * 51;
    return CssColor.make(r, g, b);
  }

  private void showSelected() {
    ctx.setFillStyle(selectedColor);
    ctx.fillRect(width/2, 0, width/2, 30);
    ctx.setFillStyle("white");
    ctx.fillRect(0, 0, width/2, 30);
  }
  
}
