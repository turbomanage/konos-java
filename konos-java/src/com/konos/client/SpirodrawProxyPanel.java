package com.konos.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;

public class SpirodrawProxyPanel extends Composite {
  private TabLayoutPanel tabPanel;
  private Canvas canvas;
  private FlowPanel cp = new FlowPanel();
  private RenderEngine instance;

  public SpirodrawProxyPanel(final Canvas canvas, final TabLayoutPanel tabLayoutPanel) {
    this.canvas = canvas;
    this.tabPanel = tabLayoutPanel;
    this.cp = new FlowPanel();
    this.initWidget(cp);
    tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {

      @Override
      public void onSelection(SelectionEvent<Integer> event) {
        int i = event.getSelectedItem();
        if (i == tabPanel.getWidgetIndex(SpirodrawProxyPanel.this)) {
          GWT.runAsync(new RunAsyncCallback() {

            @Override
            public void onSuccess() {
              if (instance==null) {
                instance = new SpiroDraw(canvas,tabLayoutPanel);
                cp.add(instance.getControlPanel());
              }
              instance.refresh();
            }
            
            @Override
            public void onFailure(Throwable reason) {
              // TODO Auto-generated method stub
            }
          });
        }
      }
    });
  }

}
