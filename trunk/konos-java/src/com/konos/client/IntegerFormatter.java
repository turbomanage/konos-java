package com.konos.client;

import com.google.gwt.gen2.client.SliderBar;
import com.google.gwt.gen2.client.SliderBar.LabelFormatter;

public class IntegerFormatter implements LabelFormatter
{

	@Override
	public String formatLabel(SliderBar slider, double value)
	{
		return Integer.toString((int) value);
	}

}
