package com.ociweb;


import com.ociweb.iot.maker.*;
import static com.ociweb.iot.maker.Port.D3;
import static com.ociweb.iot.maker.Port.A1;
import static com.ociweb.iot.grove.simple_digital.SimpleDigitalTwig.Button;
import static com.ociweb.iot.grove.simple_analog.SimpleAnalogTwig.AngleSensor;

public class IoTApp implements FogApp
{
 
    private static Port BUTTON_PORT = D3;
	public static final Port ANGLE_SENSOR_PORT = A1;


    @Override
    public void declareConnections(Hardware hardware) {
        hardware.connect(Button, BUTTON_PORT);
        hardware.connect(AngleSensor, ANGLE_SENSOR_PORT);
    }


    @Override
    public void declareBehavior(FogRuntime runtime) {
    	runtime.registerListener(new SortBehavior(runtime, BUTTON_PORT, ANGLE_SENSOR_PORT));
    }
          
}