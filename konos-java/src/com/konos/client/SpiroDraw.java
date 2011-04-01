package com.konos.client;

import java.io.IOException;
import java.util.Arrays;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d.LineCap;
import com.google.gwt.canvas.dom.client.Context2d.LineJoin;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.gen2.client.IntegerSlider;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SpiroDraw extends PolarEngine {

  private static final double PI2 = Math.PI * 2;
  // Scale factor used to scale wheel radius from 1-10 to pixels
  private VerticalPanel cp;
  private int RUnits, rUnits, dUnits;
  private double R, r, d; 
  private IntegerSlider fixedRadiusSlider;
  private IntegerSlider wheelRadiusSlider;
  private IntegerSlider penRadiusSlider;
  private ValueListBox<WheelLocation> inOrOut;
  private double lastX, lastY;
  private boolean started;
  private Timer t;
  private Label numTurns;
  protected int maxTurns;
  private GWTColorPicker colorPicker;
  private CssColor penColor = CssColor.make("red");
  private IntegerSlider penWidthSlider;
  private int penWidth;
  private int deg = 0;
  
  private enum WheelLocation {
    INSIDE(-1), OUTSIDE(1);
    private int sense;
    private WheelLocation(int sense) {
      this.sense = sense;
    }
    public int getSense() {
      return sense;
    }
  }
  
  public SpiroDraw(final Canvas canvas, TabLayoutPanel panel) {
    super(canvas, panel);
  }

  @Override
  protected void initControlPanel() {
    cp = new VerticalPanel();
    addWheelLocationChooser();
    addFixedRadiusSlider();
    addWheelRadiusSlider();
    addPenRadiusSlider();
    addColorPicker();
    addPenWidthSlider();
    addButtons();
    addCounter();
  }

  private void addFixedRadiusSlider() {
    cp.add(new Label("Fixed radius"));
    fixedRadiusSlider = new IntegerSlider(10, 19);
    fixedRadiusSlider.setValue(10.);
    fixedRadiusSlider.addValueChangeHandler(new ValueChangeHandler<Double>() {
      @Override
      public void onValueChange(ValueChangeEvent<Double> event) {
        refresh();
      }
    });
    cp.add(fixedRadiusSlider);
  }

  private void addPenWidthSlider() {
    cp.add(new Label("Pen thickness"));
    penWidthSlider = new IntegerSlider();
    penWidthSlider.setNumLabels(0);
    penWidthSlider.setValue(3.);
    penWidthSlider.addValueChangeHandler(new ValueChangeHandler<Double>() {
      @Override
      public void onValueChange(ValueChangeEvent<Double> event) {
        penWidth = event.getValue().intValue();
        drawFrame(deg);
      }
    });
    cp.add(penWidthSlider);
  }

  private void addPenRadiusSlider() {
    cp.add(new Label("Pen radius"));
    penRadiusSlider = new IntegerSlider(0, 9);
    penRadiusSlider.setValue(2.);
    penRadiusSlider.addValueChangeHandler(new ValueChangeHandler<Double>() {
      @Override
      public void onValueChange(ValueChangeEvent<Double> event) {
        refresh();
      }
    });
    cp.add(penRadiusSlider);
  }

  private void addWheelRadiusSlider() {
    cp.add(new Label("Wheel radius"));
    wheelRadiusSlider = new IntegerSlider(0, 9);
    wheelRadiusSlider.setValue(3.);
    wheelRadiusSlider.addValueChangeHandler(new ValueChangeHandler<Double>() {
      @Override
      public void onValueChange(ValueChangeEvent<Double> event) {
        refresh();
      }
    });
    cp.add(wheelRadiusSlider);
  }

  private void addWheelLocationChooser() {
    inOrOut = new ValueListBox<WheelLocation>(new Renderer<WheelLocation>() {
      @Override
      public String render(WheelLocation loc) {
        if (loc!=null)
          return loc.name();
        return "Choose circle location";
      }
      @Override
      public void render(WheelLocation loc, Appendable appendable) throws IOException {
        appendable.append(render(loc));
      }
    });
    inOrOut.setAcceptableValues(Arrays.asList(WheelLocation.values()));
    inOrOut.setValue(WheelLocation.INSIDE);
    inOrOut.addValueChangeHandler(new ValueChangeHandler<SpiroDraw.WheelLocation>() {
      @Override
      public void onValueChange(ValueChangeEvent<WheelLocation> event) {
        refresh();
      }
    });
    
    cp.add(inOrOut);
  }

  private void addColorPicker() {
    cp.add(new Label("Pen color"));
    colorPicker = new GWTColorPicker();
    colorPicker.addSelectionHandler(new SelectionHandler<String>() {
      @Override
      public void onSelection(SelectionEvent<String> event) {
        String colorString = event.getSelectedItem();
        penColor = CssColor.make(colorString);
        drawFrame(deg);
      }
    });
    cp.add(colorPicker);
  }

  private void addButtons() {
    HorizontalPanel buttonBar = new HorizontalPanel();
    Button startButton = new Button("Start");
    startButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        start();
      }
    });
    Button stopButton = new Button("Stop");
    stopButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        stop();
      }
    });
    Button clearButton = new Button("Clear");
    clearButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        clear();
      }
    });
    buttonBar.add(startButton);
    buttonBar.add(stopButton);
    buttonBar.add(clearButton);
    cp.add(buttonBar);
  }

  private void addCounter() {
    Label lblTurns = new Label("# turns");
    lblTurns.addStyleName("centered");
    cp.add(lblTurns);
    numTurns = new Label();
    numTurns.addStyleName("counter");
    cp.add(numTurns);
  }

  @Override
  public Widget getControlPanel() {
    return cp;
  }

  @Override
  public void refresh() {
    stop();
    // Reset
    lastX = 0;
    lastY = 0;
    // Compute fixed radius
    // based on starting diameter == min / 2, fixed radius == 10 units
    int min = Math.min(height, width);
    double pixelsPerUnit = min / 40.;
    RUnits = fixedRadiusSlider.getValue().intValue();
    R = RUnits * pixelsPerUnit;
    // Scale inner radius and pen distance in units of fixed radius
    rUnits = wheelRadiusSlider.getValue().intValue();
    r = rUnits * R/RUnits * inOrOut.getValue().getSense();
    dUnits = penRadiusSlider.getValue().intValue();
    d = dUnits * R/RUnits;
    maxTurns = calcTurns();
    numTurns.setText("0" + "/" + maxTurns);
    penWidth = penWidthSlider.getValue().intValue();
    drawFrame(0);
  }

  @Override
  public void drawFrame(int deg) {
    front.clearRect(0, 0, width, height);
    front.drawImage(back.getCanvas(), 0, 0);
    double theta = deg * Math.PI / 180;
    drawFixed();
    drawWheel(theta);
  }

  @Override
  public void start() {
    refresh();
    deg = 0;

    t = new Timer() {

      @Override
      public void run() {
        if (deg <= maxTurns * 360) {
          drawFrame(deg+=1);
          if (deg % 360 == 0) {
            numTurns.setText(deg/360 + "/" + maxTurns);
          }
        } else {
          stop();
        }
      }
    };
    started = true;
    t.scheduleRepeating(1);
  }

  private int calcTurns() {
    // compute ratio of wheel radius to big R then find LCM
    if ((dUnits==0) || (rUnits==0))
      return 1;
    int ru = Math.abs(rUnits);
    int wrUnits = RUnits + rUnits;
    int g = gcf (wrUnits, ru);
    return ru / g;
  }

  @Override
  protected void stop() {
    started = false;
    if (t!=null) {
      t.cancel();
    }
    // Show drawing only
    front.clearRect(0, 0, width, height);
    front.drawImage(back.getCanvas(), 0, 0);
    // Reset angle
    deg = 0;
  }

  protected void clear() {
    back.clearRect(0, 0, width, height);
    refresh();
  }

  private void drawFixed() {
    front.beginPath();
    front.setLineWidth(2);
    front.setStrokeStyle("gray");
    front.arc(xc, yc, R, 0, PI2);
    front.closePath();
    front.stroke();
  }

  /**
   * Draw the wheel with its center at angle theta
   * with respect to the fixed wheel
   * 
   * @param theta
   */
  private void drawWheel(double theta) {
    double wx = xc + ((R + r) * Math.cos(theta));
    double wy = yc - ((R + r) * Math.sin(theta));
    if (rUnits>0) {
      // Draw ring
      front.beginPath();
      front.arc(wx, wy, Math.abs(r), 0, PI2);
      front.closePath();
      front.stroke();
      // Draw center
      front.setLineWidth(1);
      front.beginPath();
      front.arc(wx, wy, 3, 0, PI2);
      front.setFillStyle("black");
      front.fill();
      front.closePath();
      front.stroke();
    }
    drawTip(wx, wy, theta);
  }

  /**
   * Draw a rotating line that shows the wheel rolling and leaves
   * the pen trace
   * 
   * @param wx X coordinate of wheel center
   * @param wy Y coordinate of wheel center
   * @param theta Angle of wheel center with respect to fixed circle
   */
  private void drawTip(double wx, double wy, double theta) {
    // Calc wheel rotation angle
    double rot = (r==0) ? theta : theta * (R+r) / r;
    // Find tip of line
    double tx = wx + d * Math.cos(rot);
    double ty = wy - d * Math.sin(rot);
    front.beginPath();
    front.setFillStyle(penColor);
    front.arc(tx, ty, penWidth/2+2, 0, PI2);
    front.fill();
    front.moveTo(wx, wy);
    front.setStrokeStyle("black");
    front.lineTo(tx, ty);
    front.closePath();
    front.stroke();
    if (started)
      drawSegmentTo(tx, ty);
  }

  private void drawSegmentTo(double tx, double ty) {
    if (lastX > 0)
    {
      back.beginPath();
      back.setStrokeStyle(penColor);
      back.setLineWidth(penWidth);
      back.setLineCap(LineCap.SQUARE);
      back.setLineJoin(LineJoin.MITER);
      back.moveTo(lastX, lastY);
      back.lineTo(tx, ty);
      back.closePath();
      back.stroke();
    }
    lastX = tx;
    lastY = ty;
  }

}
