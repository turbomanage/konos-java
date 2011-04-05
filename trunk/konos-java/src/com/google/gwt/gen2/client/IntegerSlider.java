package com.google.gwt.gen2.client;


/**
 * Convenience {@link SliderBar} with Integer values
 * and 10 steps
 */
public class IntegerSlider extends SliderBar {

  public static class IntegerFormatter implements LabelFormatter {

    @Override
    public String formatLabel(SliderBar slider, double value) {
      return String.valueOf((int) value);
    }
  }

  /**
   * Convenience constructor creates a {@link SliderBar} with
   * the integers 1-10
   */
  public IntegerSlider() {
    this(1,10);
  }
  
  public IntegerSlider(int minValue, int maxValue) {
    this(minValue, maxValue, new IntegerFormatter());
  }
 
  public IntegerSlider(double minValue, double maxValue, LabelFormatter formatter) {
    super(minValue, maxValue, formatter);
    this.setNumLabels(9);
    this.setNumTicks(9);
    this.setStepSize((maxValue - minValue + 1) / 10);
    this.setPixelSize(184, 40);
  }

  public void setValue(Integer intValue) {
    super.setValue(intValue.doubleValue());
  }
}
