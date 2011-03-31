package com.konos.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.gen2.client.IntegerSlider;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PolarEngine extends RenderEngine {

  protected double maxradius = 290.;
  protected int yc;
  protected int xc;
  private Timer t;
  protected Context2d front;
  protected Context2d back;
  private Label nTurns = new Label();
  private IntegerSlider nSlider = new IntegerSlider();
  private IntegerSlider dSlider = new IntegerSlider();
  private VerticalPanel controlPanel;
  private PolarEquation eq;
  private double lastX;
  private double lastY;
  private ValueListBox<PolarEquation> eqChooser;
  private List<PolarEquation> options = new ArrayList<PolarEquation>();

  public interface PolarEquation {
    int numHalfTurns();
    double calcR(double theta);
    String getLabel();
    void init();
  }

  public PolarEngine(Canvas canvas, final TabLayoutPanel panel) {
    super(canvas);
    front = canvas.getContext2d();
    // This one is intentionally not attached to the DOM
    Canvas backCanvas = Canvas.createIfSupported();
    backCanvas.setCoordinateSpaceHeight(height);
    backCanvas.setCoordinateSpaceWidth(width);
    back = backCanvas.getContext2d();
    xc = width / 2;
    yc = height / 2;
    initControlPanel();
    panel.addSelectionHandler(new SelectionHandler<Integer>() {
      @Override
      public void onSelection(SelectionEvent<Integer> event) {
        int i = event.getSelectedItem();
        if (panel.getWidget(i)==getControlPanel())
            refresh();
        else if (t!=null)
          t.cancel();
      }
    });
  }

  protected void initControlPanel() {
    controlPanel = new VerticalPanel();
    addEquationChooser();
    addSliders();
    addCounter();
  }

  private void addCounter() {
    Label halfTurns = new Label("# half turns");
    halfTurns.addStyleName("centered");
    controlPanel.add(halfTurns);
    nTurns.addStyleName("counter");
    controlPanel.add(nTurns);
  }

  private void addSliders() {
    nSlider.setValue(1.);
    dSlider.setValue(1.);
    nSlider.addValueChangeHandler(refreshVCH);
    dSlider.addValueChangeHandler(refreshVCH);

    controlPanel.add(eqChooser);
    controlPanel.add(new Label("Numerator"));
    controlPanel.add(nSlider);
    controlPanel.add(new Label("Denominator"));
    controlPanel.add(dSlider);
  }

  private void addEquationChooser() {
    eqChooser = new ValueListBox<PolarEquation>(new Renderer<PolarEquation>() {
      @Override
      public String render(PolarEquation eq) {
        if (eq != null)
          return eq.getLabel();
        return "Select an equation";
      }
      
      @Override
      public void render(PolarEquation eq, Appendable appendable) throws IOException {
        appendable.append(eq.getLabel());
      }
    });
    options.add(new RoseEquation(maxradius, nSlider, dSlider));
//    options.add(new Sinusoid(maxradius, nSlider, dSlider));
//    options.add(new Lemniscate(maxradius));
    eqChooser.setAcceptableValues(options);
    eqChooser.addValueChangeHandler(new ValueChangeHandler<PolarEquation>() {
      @Override
      public void onValueChange(ValueChangeEvent<PolarEquation> event) {
        refresh();
      }
    });
  }

  @Override
  public Widget getControlPanel() {
    return controlPanel;
  }

  @Override
  public void refresh() {
    if (t!=null)
      t.cancel();
    this.eq = eqChooser.getValue();
    if (eq==null)
      return;
    eq.init();
    back.clearRect(0, 0, width, height);

    start();
  }

  @Override
  public void start() {
    // Reset timer
    if (t != null)
      t.cancel();

    t = new Timer() {
      private Integer turns;
      int nPi = eq.numHalfTurns();
      int deg = 0;

      @Override
      public void run() {
        if (deg <= nPi * 180) {
          drawFrame(deg++);
          turns = new Integer(deg / 180);
          nTurns.setText(turns + "/" + nPi);
        } else
          this.cancel();
      }
    };
    t.scheduleRepeating(1);
  }

  @Override
  public void drawFrame(int deg) {
    double theta = deg * Math.PI / 180;
    double r = eq.calcR(theta);
    drawSegment(r, theta);
    front.clearRect(0, 0, width, height);
    front.drawImage(back.getCanvas(), 0, 0);
    drawSweep(maxradius, r, theta);
  }

  private void drawSegment(double r, double theta) {
    int x = xc + getX(r, theta);
    int y = yc - getY(r, theta);
    if (theta > 0) {
      back.beginPath();
      back.moveTo(lastX, lastY);
      // back.setStrokeStyle("red");
      if (r > 0)
        back.setStrokeStyle("green");
      else
        back.setStrokeStyle("blue");
      back.lineTo(x, y);
      back.stroke();
      back.closePath();
    }
    lastX = x;
    lastY = y;
  }

  private void drawSweep(double s, double r, double theta) {
    // Draw rotating dial
    int x = xc + getX(r, theta);
    int y = yc - getY(r, theta);
    int sx = xc + getX(s, theta);
    int sy = yc - getY(s, theta);
    int bx = xc - getX(s, theta);
    int by = yc + getY(s, theta);
    // Draw center
    front.beginPath();
    front.setStrokeStyle("black");
    front.arc(xc, yc, 4, 0, Math.PI * 2);
    front.closePath();
    front.stroke();
    // Draw bubble
    front.beginPath();
    front.setFillStyle(back.getFillStyle());
    front.arc(x, y, 7, 0, Math.PI * 2);
    front.fill();
    front.closePath();
    front.stroke();
    // Draw sweep
    front.beginPath();
    front.setStrokeStyle("green");
    front.moveTo(xc, yc);
    front.lineTo(sx, sy);
    front.closePath();
    front.stroke();
    // Draw back sweep
    front.beginPath();
    front.setStrokeStyle("blue");
    front.moveTo(xc, yc);
    front.lineTo(bx, by);
    front.closePath();
    front.stroke();
  }

  public static int getY(double r, double theta) {
    return (int) (r * Math.sin(theta));
  }

  public static int getX(double r, double theta) {
    return (int) (r * Math.cos(theta));
  }
}
