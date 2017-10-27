package com.ociweb;

import com.ociweb.gl.api.ShutdownListener;
import com.ociweb.gl.api.StartupListener;
import com.ociweb.iot.grove.lcd_rgb.Grove_LCD_RGB;
import com.ociweb.iot.maker.DigitalListener;
import com.ociweb.iot.maker.AnalogListener;
import com.ociweb.iot.maker.FogCommandChannel;
import com.ociweb.iot.maker.FogRuntime;
import com.ociweb.iot.maker.Port;
import static com.ociweb.iot.maker.FogCommandChannel.I2C_WRITER;

public class SortBehavior implements AnalogListener, DigitalListener, ShutdownListener, StartupListener {

	private static FogCommandChannel LCDChannel;

	// state 1: push to make random numbers
	// state 2: push to begin sorting
	// state 3: advance to next sorting step
	// state 4: rotate to pick search value
	// state 5: advance to next search step
	// state 6: start over

	private String state = "number generation";
	private static int[] numbers = new int[7];
	private final Port buttonPort, angleSensorPort;
	private static int min_index;
	private static int key;
	private static int j;
	private static int l;
	private static int r;
	private int step = 0;
	private int choice = 0;

	public SortBehavior(FogRuntime runtime, Port buttonPort, Port angleSensorPort) {
		LCDChannel = runtime.newCommandChannel(I2C_WRITER | FogRuntime.PIN_WRITER);
		this.buttonPort = buttonPort;
		this.angleSensorPort = angleSensorPort;
	}

	@Override
	public void startup() {
		Grove_LCD_RGB.commandForTextAndColor(LCDChannel, "Push to make\nrandom numbers", 0, 255, 255);
	}

	@Override
	public void digitalEvent(Port port, long time, long duration, int value) {
		if (port == buttonPort && value == 1) {
			switch (state) {
			case "number generation":
				numbers = generateNumbers(numbers);
				Grove_LCD_RGB.commandForTextAndColor(LCDChannel, numbersToString(numbers) + "\nPush to pick sort", 0,
						255, 255);
				state = "picking algorithm";
				break;
			case "picking algorithm":
				switch (choice) {
				case 1:
					state = "selection";
					break;
				case 2:
					state = "insertion";
					break;
				case 3:
					state = "merge";
					break;
				}
			case "selection":
				min_index = SelectionSort.findMinIndex(numbers, step);
				Grove_LCD_RGB.commandForTextAndColor(LCDChannel, "i:" + step + " Val:" + numbers[step] + " Min:"
						+ numbers[min_index] + "\n" + numbersToString(numbers), 0, 255, 255);
				state = "selectionHelper";
				break;
			case "selectionHelper":
				if (step < 6) {
					state = "selection";
					SelectionSort.swap(numbers, min_index, step);
					step++;
				} else {
					Grove_LCD_RGB.commandForTextAndColor(LCDChannel, "Sorted Array\n" + numbersToString(numbers), 0,
							255, 255);
					state = "done";
				}
				break;
			case "done":
				Grove_LCD_RGB.commandForTextAndColor(LCDChannel, "Sorted Array\n" + numbersToString(numbers), 0, 255,
						255);
				break;
			case "insertion":
				key = numbers[step];
				j = step - 1;
				Grove_LCD_RGB.commandForTextAndColor(LCDChannel,
						"i:" + step + " Val:" + numbers[step] + "\n" + numbersToString(numbers), 0, 255, 255);
				state = "insertionHelper";
				break;
			case "insertionHelper":
				if (step < 6) {
					state = "insertion";
					InsertionSort.sort(numbers, j, key);
					step++;
				} else {
					Grove_LCD_RGB.commandForTextAndColor(LCDChannel, "Sorted Array\n" + numbersToString(numbers), 0,
							255, 255);
					state = "done";
				}
				break;
			case "merge":
				if (step == 0) {
				l = 0;
				r = numbers.length-1;
				}
				state = "mergeHelper";
				break;
			case "mergeHelper":
				if (step < 6) {
					state = "merge";
					MergeSort.mergeSort(numbers, l, r);
					step++;
				} else {
					Grove_LCD_RGB.commandForTextAndColor(LCDChannel, "Sorted Array\n" + numbersToString(numbers), 0,
							255, 255);
					state = "done";
				}
				break;
			}
		}
	}

	@Override
	public void analogEvent(Port port, long time, long durationMillis, int average, int value) {
		if (port == angleSensorPort && state == "picking algorithm") {
			if (value < 341) {
				Grove_LCD_RGB.commandForTextAndColor(LCDChannel, "Selection Sort->\nUse angle sensor", 0, 255, 255);
				choice = 1;
			} else if (value < 682) {
				Grove_LCD_RGB.commandForTextAndColor(LCDChannel, "<Insertion Sort>\nUse angle sensor", 0, 255, 255);
				choice = 2;
			} else {
				Grove_LCD_RGB.commandForTextAndColor(LCDChannel, "<- Merge Sort\nUse angle sensor", 0, 255, 255);
				choice = 3;
			}
		}
	}

	public int[] generateNumbers(int n[]) {
		for (int i = 0; i < 7; i++) {
			n[i] = (int) (Math.random() * 10);
		}
		return n;
	}

	public static String numbersToString(int n[]) {
		String numberString = "";

		for (int i = 0; i < numbers.length; i++) {
			numberString += " " + Integer.toString(n[i]);
		}
		return numberString;
	}

	public boolean acceptShutdown() {
		Grove_LCD_RGB.clearDisplay(LCDChannel);
		return true;
	}
	
	public static void display(String s) {
		Grove_LCD_RGB.commandForTextAndColor(LCDChannel, s, 0, 255, 255);
	}
}
