package com.konos.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.gen2.client.IntegerSlider;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PolarEngine extends RenderEngine implements RequiresResize {

  protected double maxradius = 290.;
  private Timer t;
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

    int numPoints();
  }

  public PolarEngine(Canvas canvas, final TabLayoutPanel panel) {
    super(canvas);
    front = canvas.getContext2d();
    backCanvas = Canvas.createIfSupported();
    back = backCanvas.getContext2d();
    initControlPanel();
    panel.addSelectionHandler(new SelectionHandler<Integer>() {
      @Override
      public void onSelection(SelectionEvent<Integer> event) {
        int i = event.getSelectedItem();
        // TODO handle resize properly
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
          @Override
          public void execute() {
            nSlider.onResize();
            dSlider.onResize();
          }
        });
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
        PolarEngine.this.onResize();
        refresh();
      }
    });
    onResize();
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
    options.add(new Sinusoid(maxradius, nSlider, dSlider));
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

  protected void stop() {
    if (t!=null) {
      t.cancel();
    }
  }

  @Override
  public void refresh() {
    stop();
    this.eq = eqChooser.getValue();
    if (eq == null)
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
      int numPoints = eq.numPoints();
      int deg = 0;
      int stepSize = calcStepSize(nPi, numPoints);

      @Override
      public void run() {
        if (deg <= nPi * 180) {
          drawFrame(deg);
          turns = new Integer(deg / 180);
          nTurns.setText(turns + "/" + nPi);
          deg+=stepSize;
        } else {
          this.cancel();
          front.clearRect(0, 0, width, height);
          front.drawImage(back.getCanvas(), 0, 0);
        }
      }
    };
    t.scheduleRepeating(20);
  }

  private int calcStepSize(int numHalfTurns, int numPoints) {
      return Math.max(1, 5 - numPoints / numHalfTurns);
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
