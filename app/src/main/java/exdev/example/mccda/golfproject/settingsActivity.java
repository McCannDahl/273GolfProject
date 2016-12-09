package exdev.example.mccda.golfproject;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.DataPointInterface ;

import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class settingsActivity extends AppCompatActivity implements SensorEventListener, AdapterView.OnItemSelectedListener {

    int lastX = 0;
    Spinner spinner = null;
    SeekBar seekBar = null;
    double v0 = 0;
    double thetaUp0 = 45;
    double omega0 = 0;
    double v = 0;
    double fraction = 1.0;//power%
    double thetaUp = 0;
    double omega = 0;
    double spinX = 0;
    double spinY = 0;
    double spinZ = 0;
    double velX = 0;
    double velY = 0;
    double velZ = 0;

    double diameter = .0427;//m
    double radius = diameter/2.0;
    double mass = 45.9/1000.0;//kg
    double p = 1.225;//kg/m^3    //row = density of air
    double A = 3.14 * radius * radius;//Math.PI*Math.pow(radius,2); //cross-sectional area of the ball
    double CD = -0.3;//= Math.pow(.3,3);//coefficient of drag  //question should this be negative?
    double g = 9.81;//m/s^2
    double Vw = 0;//Velocity of wind

    //double Vr = v-Vw;//rotational velocities
    //double aD = (CD/(2*mass))*p*A*Math.pow(Vr,2);//acceleration of Drag
    //double CL = (radius*omega)/Vr;//coefficient of lift
    //double aM = ((CL*p*A*Vr)/(2*mass*omega))*(omega cross Vr);//magnus force
    double VRx = 0;
    double VRy = 0;
    double VRz = 0;
    double aDx = 0;
    double aDy = 0;
    double aDz = 0;
    double CLx = 0;
    double CLy = 0;
    double CLz = 0;
    double aMx = 0;
    double aMy = 0;
    double aMz = 0;

    //double V;//Velocity of the ball
    //dobule A;//Acceleration of the ball
    //double VW;//velocity of the wind
    double Vx = 0;
    double Vy = 0;
    double Vz = 0;
    double VWx = 0;
    double VWy = 0;
    double VWz = 0;
    double Ax = 0;
    double Ay = 0;
    double Az = 0;
    double ARx = 0;
    double ARy = 0;
    double ARz = 0;
    double x = 0;
    double y = 0;
    double z = 0;
    boolean inTheAir = true;
    boolean graphing = false;

    ArrayList<Double> X = new ArrayList<Double>();
    ArrayList<Double> Y = new ArrayList<Double>();
    ArrayList<Double> Z = new ArrayList<Double>();
    ArrayList<Double> T = new ArrayList<Double>();

    GraphView graph = null;
    GraphView graph2 = null;
    LineGraphSeries<DataPoint> series = null;
    LineGraphSeries<DataPoint> series2 = null;
    PointsGraphSeries<DataPoint> seriesHole1 = null;
    PointsGraphSeries<DataPoint> seriesHole2 = null;
    PointsGraphSeries<DataPoint> seriesWind1 = null;
    PointsGraphSeries<DataPoint> seriesWind2 = null;
    EditText editText = null;
    EditText editText2 = null;
    EditText editText3 = null;

    double distanceToHole = 200;
    double windSpeed = 0;
    double windDirection = 0;
    double thetaLeft = 0;
    double thetaLeft0 = 0;

    private SensorManager sensorManager;
    double accX,accY,accZ,acc,maxAcc;   // these are the acceleration in x,y and z axis
    boolean swinging = false;
    int swingingInt = 0;
    boolean checkBoxClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        editText = (EditText) findViewById(R.id.editText);
        editText.setText((int)windSpeed+"");
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    windSpeed = Double.parseDouble(editText.getText().toString());
                }catch (Exception e){}
            }
        });
        editText2 = (EditText) findViewById(R.id.editText2);
        editText2.setText((int)windDirection+"");
        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    windDirection = Double.parseDouble(editText2.getText().toString());
                    windDirection = windDirection * Math.PI/180.0;
                }catch (Exception e){}
            }
        });
        editText3 = (EditText) findViewById(R.id.editText3);
        editText3.setText((int)distanceToHole+"");
        editText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    distanceToHole = Double.parseDouble(editText3.getText().toString());
                    updateFlag();
                    scaleMaps();
                }catch (Exception e){}
            }
        });

        spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.clubs, android.R.layout.simple_spinner_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<>();
        graph.addSeries(series);
        seriesHole1 = new PointsGraphSeries<>(new DataPoint[] {
                new DataPoint(distanceToHole, 0)
        });
        graph.addSeries(seriesHole1);
        seriesHole1.setColor(Color.GREEN);
        seriesHole1.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(10);
                canvas.drawLine(x, y, x, y-100, paint);
                canvas.drawLine(x, y-100, x+30, y-70, paint);
                canvas.drawLine(x+30, y-70, x, y-70, paint);
            }
        });
        seriesWind1 = new PointsGraphSeries<>(new DataPoint[] {
                new DataPoint(distanceToHole, 15)
        });
        graph.addSeries(seriesWind1);
        seriesWind1.setColor(Color.RED);
        seriesWind1.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(8);
                canvas.drawLine(x, y, x+(float)(windSpeed*Math.cos(windDirection)), y, paint);
                //canvas.drawLine(x, y-100, x+30, y-70, paint);
                //canvas.drawLine(x+30, y-70, x, y-70, paint);
            }
        });

        graph2 = (GraphView) findViewById(R.id.graph2);
        series2 = new LineGraphSeries<>();
        graph2.addSeries(series2);
        seriesHole2 = new PointsGraphSeries<>(new DataPoint[] {
                new DataPoint(distanceToHole, 0)
        });
        graph2.addSeries(seriesHole2);
        seriesHole2.setColor(Color.GREEN);
        seriesHole2.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(1);
                canvas.drawCircle(x,y,10,paint);
            }
        });
        seriesWind2 = new PointsGraphSeries<>(new DataPoint[] {
                new DataPoint(distanceToHole, 5)
        });
        graph2.addSeries(seriesWind2);
        seriesWind2.setColor(Color.RED);
        seriesWind2.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(8);
                canvas.drawLine(x, y, x+(float)(windSpeed*Math.cos(windDirection)), y-(float)(windSpeed*Math.sin(windDirection)), paint);
                canvas.drawLine(x+(float)(windSpeed*Math.cos(windDirection)), y-(float)(windSpeed*Math.sin(windDirection)),x+(float)(windSpeed*.8*Math.cos(windDirection+.3)), y-(float)(windSpeed*.8*Math.sin(windDirection+.3)), paint);
                canvas.drawLine(x+(float)(windSpeed*Math.cos(windDirection)), y-(float)(windSpeed*Math.sin(windDirection)),x+(float)(windSpeed*.8*Math.cos(windDirection-.3)), y-(float)(windSpeed*.8*Math.sin(windDirection-.3)), paint);
            }
        });

        scaleMaps();

        seekBar = (SeekBar)findViewById(R.id.seekBar);
        final TextView seekBarValue = (TextView)findViewById(R.id.textView7);
        seekBar.setProgress((int)(fraction*100));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                seekBarValue.setText("Swing Power ("+progress+"%)");
                fraction =  (double)progress/100.0;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });
        seekBarValue.setText("Swing Power ("+(fraction*100)+"%)");

        //SeekBar seekBar2 = (SeekBar)findViewById(R.id.seekBar2);
        //final TextView seekBarValue2 = (TextView)findViewById(R.id.textView8);

        /*seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                int thetaUp = (int)(((double)progress/100.0)*180);
                seekBarValue2.setText("Swing Angle up and down ("+thetaUp+"deg)");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });*/

        SeekBar seekBar3 = (SeekBar)findViewById(R.id.seekBar3);
        final TextView seekBarValue3 = (TextView)findViewById(R.id.textView9);
        seekBar3.setProgress(50);

        seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                thetaLeft0 = (int)(((double)progress/100.0)*10)-5;
                seekBarValue3.setText("Swing Angle right and left "+(progress-50)+"%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                swing();
            }
        });

        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);

        checkBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    checkBoxClicked = true;
                }

            }
        });


    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            if(checkBoxClicked) {
                if(graphing){

                }else {

                    accX = event.values[0];
                    accY = event.values[1];
                    accZ = event.values[2];
                    acc = Math.pow(accX * accX + accY * accY + accZ * accZ, (1.0 / 3.0));
                    //if(accX>10||accY>10||accZ>10){
                    //    Log.v("mccacc",Math.pow(accX*accX+accY*accY+accZ*accZ,(1.0/3.0))+"");//from 5 to 10 is a swing
                    //}
                    if (swinging) {
                        if (acc > maxAcc) {
                            maxAcc = acc;
                        }
                    }
                    if (acc > 5) {
                        if (swingingInt > 5) {
                            swingingInt = 0;
                            swinging = true;
                            maxAcc = acc;
                        } else {
                            swingingInt++;
                        }
                    }
                    if (acc < 5) {
                        if (swinging) {
                            swinging = false;
                            fraction = ((maxAcc - 5.0) / 4.0);
                            if (fraction > 1) {
                                fraction = 1;
                            }
                            seekBar.setProgress((int) (fraction * 100));
                            swing();
                            graphing = true;
                            Log.v("mccacc", "fraction = " + fraction);//from 5 to 10 is a swing
                        }
                    }

                }
            }
        }
    }


    public void updateFlag(){
        graph.removeSeries(seriesHole1);
        graph.removeSeries(seriesWind1);
        seriesHole1 = new PointsGraphSeries<>(new DataPoint[] {
                new DataPoint(distanceToHole, 0)
        });
        graph.addSeries(seriesHole1);
        seriesHole1.setColor(Color.GREEN);
        seriesHole1.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(10);
                canvas.drawLine(x, y, x, y-100, paint);
                canvas.drawLine(x, y-100, x+30, y-70, paint);
                canvas.drawLine(x+30, y-70, x, y-70, paint);
            }
        });
        graph.addSeries(seriesWind1);
        seriesWind1.setColor(Color.RED);
        seriesWind1.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(10);
                canvas.drawLine(x, y, x+(float)(windSpeed*Math.cos(windDirection)), y, paint);
                //canvas.drawLine(x, y-100, x+30, y-70, paint);
                //canvas.drawLine(x+30, y-70, x, y-70, paint);
            }
        });

        graph2.removeSeries(seriesHole2);
        graph2.removeSeries(seriesWind2);
        graph2 = (GraphView) findViewById(R.id.graph2);
        series2 = new LineGraphSeries<>();
        graph2.addSeries(series2);
        seriesHole2 = new PointsGraphSeries<>(new DataPoint[] {
                new DataPoint(distanceToHole, 0)
        });
        graph2.addSeries(seriesHole2);
        seriesHole2.setColor(Color.GREEN);
        seriesHole2.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(1);
                canvas.drawCircle(x,y,10,paint);
            }
        });
        seriesWind2 = new PointsGraphSeries<>(new DataPoint[] {
                new DataPoint(distanceToHole, 5)
        });
        graph2.addSeries(seriesWind2);
        seriesWind2.setColor(Color.RED);
        seriesWind2.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(8);
                canvas.drawLine(x, y, x+(float)(windSpeed*Math.cos(windDirection)), y-(float)(windSpeed*Math.sin(windDirection)), paint);
                canvas.drawLine(x+(float)(windSpeed*Math.cos(windDirection)), y-(float)(windSpeed*Math.sin(windDirection)),x+(float)(windSpeed*.8*Math.cos(windDirection+.3)), y-(float)(windSpeed*.8*Math.sin(windDirection+.3)), paint);
                canvas.drawLine(x+(float)(windSpeed*Math.cos(windDirection)), y-(float)(windSpeed*Math.sin(windDirection)),x+(float)(windSpeed*.8*Math.cos(windDirection-.3)), y-(float)(windSpeed*.8*Math.sin(windDirection-.3)), paint);
            }
        });
    }

    public void swing(){

        VWx = windSpeed*Math.cos(windDirection);
        VWz = windSpeed*Math.sin(windDirection);

        v=v0*fraction*.44704; // convert to m/s from mph
        omega = omega0*fraction*.10472; // convert from rpm to rad/s
        thetaUp = thetaUp0 * Math.PI/180.0;
        if(thetaLeft0<0){
            thetaLeft0 = thetaLeft0+360;
        }
        thetaLeft = thetaLeft0 * Math.PI/180.0;
        spinX=omega*Math.sin(thetaLeft); //No rotation about x axis0000
        spinY=0; //No hook or slice spin
        spinZ=omega*Math.cos(thetaLeft); //All spin about horizontal axis
        if(spinX>0){
            spinX = spinX*-1;//spinX should always be negative
        }
        //spinZ = spinZ*10;
        velX = v*Math.cos(thetaUp)*Math.cos(thetaLeft); //X coordinate
        velY = v*Math.sin(thetaUp); //Y coordinate
        velZ = -v*Math.cos(thetaUp)*Math.sin(thetaLeft); // No pull or push velocity

        //KNOWNS
        //omega, thetaUP, spinX, spinY, spinZ, velX, velY, velZ
        //windVelx, windVely, windVelz
        //Vrx, Vry, Vrz
        //aDx,aDy,aDz
        //CLx,CLy,CLz
        //aMx,aMy,aMz


        X = new ArrayList<Double>();
        Y = new ArrayList<Double>();
        Z = new ArrayList<Double>();
        T = new ArrayList<Double>();
        x = 0;
        y = 0;
        z = 0;

        VRx = velX-VWx;//rotational velocities
        aDx = (CD/(2*mass))*p*A*Math.pow(VRx,2);//acceleration of Drag
        CLx = (radius*omega)/VRx;//coefficient of lift
        aMx = ((CLx*p*A*VRx)/(2*mass*omega))*(omega*VRy);//magnus force
        VRy = velY-VWy;//rotational velocities
        aDy = (CD/(2*mass))*p*A*Math.pow(VRy,2);//acceleration of Drag
        CLy = (radius*omega)/VRy;//coefficient of lift
        aMy = ((CLx*p*A*VRx)/(2*mass*omega))*(omega*VRx);//magnus force
        VRz = velZ-VWz;//rotational velocities
        aDz = (CD/(2*mass))*p*A*Math.pow(VRz,2);//acceleration of Drag
        CLz = (radius*omega)/VRz;//coefficient of lift
        aMz = 0;//magnus force

        //Vx = VRx + VWx;
        //Vy = VRy + VWy;
        //Vz = VRz + VWz;
        //VRx = Vx - VWx;
        //VRy = Vy - VWy;
        //VRz = Vz - VWz;

        //Ax = -(CD*p*A/(2.0*mass)) * VRx * Math.abs(VRx) + (radius*p*A/(2*mass))*(spinY*VRz-spinZ*VRy);
        //Ay = -g -(CD*p*A/(2.0*mass)) * VRy * Math.abs(VRy) + (radius*p*A/(2*mass))*(spinZ*VRz-spinX*VRx);
        //Az = -(CD*p*A/(2.0*mass)) * VRz * Math.abs(VRz) + (radius*p*A/(2*mass))*(spinX*VRy-spinY*VRx);

        //Ax = ARx;
        //Ay = ARy;
        //Az = ARz;

        //THINGS THAT CHANGE
        //VR and V

        double tTotal = 100;
        double h = .03;
        double t_i = 0;

        double[] R_i =  new double[3];
        double[] R_i_Adjusted =  new double[3];
        double[] R_ii  = new double[3];
        double[] VR_i  = new double[3];
        double[] VR_i_Adjusted  = new double[3];
        double[] VR_ii = new double[3];
        double[] V_i  = new double[3];
        double[] V_i_Adjusted  = new double[3];
        double[] V_ii = new double[3];
        double[] k_1dr = new double[3];
        double[] k_2dr = new double[3];
        double[] k_3dr = new double[3];
        double[] k_4dr = new double[3];
        double[] k_1ddr = new double[3];
        double[] k_2ddr = new double[3];
        double[] k_3ddr = new double[3];
        double[] k_4ddr = new double[3];



        R_i[0] = 0;
        R_i[1] = 0;
        R_i[2] = 0;
        VR_i[0] = velX - VWx;//initial conditions
        VR_i[1] = velY - VWy;
        VR_i[2] = velZ - VWz;
        V_i[0] = velX;//initial conditions
        V_i[1] = velY;
        V_i[2] = velZ;
        Vx = velX;
        Vy = velY;
        Vz = velZ;

        Log.v("mccData","v = "+v);
        Log.v("mccData","omega = "+omega);
        Log.v("mccData","thetaUp = "+thetaUp);
        Log.v("mccData","VRx = "+VRx+",VRy = "+VRy+",VRz = "+VRz);
        Log.v("mccData","Vx = "+Vx+",Vy = "+Vy+",Vz = "+Vz);
        Log.v("mccData","R_i[0] = "+R_i[0]+",R_i[1] = "+R_i[1]+",R_i[2]"+R_i[2]);
        Log.v("mccData","VR_i[0] = "+VR_i[0]+",VR_i[1] = "+VR_i[1]+",VR_i[2]"+VR_i[2]);

        inTheAir = true;

        X.add(new Double(0));
        Y.add(new Double(0));
        Z.add(new Double(0));

        while(inTheAir){
            //for (int i = 0; i < (tTotal / h); i++) {

            for(int j=0;j<3;j++) {
                k_1dr[j] = dr(j, t_i, R_i, VR_i,V_i);
                k_1ddr[j] = ddr(j, t_i, R_i, VR_i,V_i);
            }
            for(int j=0;j<3;j++) {
                for (int k = 0; k < 3; k++) {
                    R_i_Adjusted[k] = R_i[k] + k_1dr[j] * (h / 2.0);
                    VR_i_Adjusted[k] = VR_i[k] + k_1ddr[j] * (h / 2.0);
                    V_i_Adjusted[k] = V_i[k] + k_1ddr[j] * (h / 2.0);
                }
                k_2dr[j] = dr(j, t_i + (h / 2.0), R_i_Adjusted, VR_i_Adjusted, V_i_Adjusted);
                k_2ddr[j] = ddr(j, t_i + (h / 2.0), R_i_Adjusted, VR_i_Adjusted, V_i_Adjusted);
            }
            for(int j=0;j<3;j++) {
                for (int k = 0; k < 3; k++) {
                    R_i_Adjusted[k] = R_i[k] + k_2dr[j] * (h / 2.0);
                    VR_i_Adjusted[k] = VR_i[k] + k_2ddr[j] * (h / 2.0);
                    V_i_Adjusted[k] = V_i[k] + k_2ddr[j] * (h / 2.0);
                }
                k_3dr[j] = dr(j, t_i + (h / 2.0), R_i_Adjusted, VR_i_Adjusted, V_i_Adjusted);
                k_3ddr[j] = ddr(j, t_i + (h / 2.0), R_i_Adjusted, VR_i_Adjusted, V_i_Adjusted);
            }
            for(int j=0;j<3;j++) {
                for(int k=0; k<3; k++){
                    R_i_Adjusted[k] = R_i[k] + k_3dr[j] * h;
                    VR_i_Adjusted[k] = VR_i[k] + k_3ddr[j] * h;
                    V_i_Adjusted[k] = V_i[k] + k_3ddr[j] * h;
                }
                k_4dr[j] = dr(j,t_i + h, R_i_Adjusted, VR_i_Adjusted, V_i_Adjusted);
                k_4ddr[j] = ddr(j,t_i + h, R_i_Adjusted, VR_i_Adjusted, V_i_Adjusted);

                R_ii[j] = R_i[j] + (1.0 / 6.0) * (k_1dr[j] + 2.0 * k_2dr[j] + 2.0 * k_3dr[j] + k_4dr[j]) * h;//position
                VR_ii[j] = VR_i[j] + (1.0 / 6.0) * (k_1ddr[j] + 2.0 * k_2ddr[j] + 2.0 * k_3ddr[j] + k_4ddr[j]) * h;//vel
                V_ii[j] = V_i[j] + (1.0 / 6.0) * (k_1ddr[j] + 2.0 * k_2ddr[j] + 2.0 * k_3ddr[j] + k_4ddr[j]) * h;//vel

            }

            Log.v("mccData","R_ii[0] = "+R_ii[0]+",R_ii[1]"+R_ii[1]+",R_ii[2] = "+R_ii[2]);
            Log.v("mccData","VR_ii[0] = "+VR_ii[0]+",VR_ii[1] = "+VR_ii[1]+",VR_ii[2]"+VR_ii[2]);
            Log.v("mccData","V_ii[0] = "+V_ii[0]+",V_ii[1] = "+V_ii[1]+",V_ii[2]"+V_ii[2]);

            Vx = VR_ii[0]+VWx;
            Vy = VR_ii[1]+VWy;
            Vz = VR_ii[2]+VWz;
            V_i[0] = VR_ii[0]+VWx;
            V_i[1] = VR_ii[1]+VWy;
            V_i[2] = VR_ii[2]+VWz;

            //x += Vx*h;
            //y += Vy*h;
            //z += Vz*h;
            //Log.v("mccData","x = "+x+",y = "+y+",z = "+z);

            x = R_ii[0] + VWx*t_i;//this is not true, only with no wind
            y = R_ii[1] + VWy*t_i;
            z = -R_ii[2] - VWz*t_i;//z axis is wuird
            Log.v("mccData","x = "+x+",y = "+y+",z = "+z);

            //Log.v("mccData","x = "+x+",y = "+y+",z = "+z);


            if(y<0){

                //double lastXInArray = X.get(X.size()-1);
                //double lastTInArray = T.get(T.size()-1);
                //double xDividedByT = lastXInArray/lastTInArray;
                //for(int i=0;i<T.size();i++){
                //    T.set(i,T.get(i)*xDividedByT);
                //}

                inTheAir = false;
                lastX = 0;
                graph.removeSeries(series);
                series = new LineGraphSeries<>();
                graph.addSeries(series);

                graph2.removeSeries(series2);
                series2 = new LineGraphSeries<>();
                graph2.addSeries(series2);

                plotRealTime();

                holeInOneQuestionMark();

            }else{

                X.add(new Double(x));
                Y.add(new Double(y));
                Z.add(new Double(z));
                T.add(new Double(t_i));

                R_i = R_ii;
                VR_i = VR_ii;
                V_i = V_ii;

                t_i += h;
            }
        }


    }

    public void scaleMaps(){
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(distanceToHole + 20);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(20);
        graph2.getViewport().setXAxisBoundsManual(true);
        graph2.getViewport().setMinX(0);
        graph2.getViewport().setMaxX(distanceToHole + 20);
        graph2.getViewport().setYAxisBoundsManual(true);
        graph2.getViewport().setMinY(-10);
        graph2.getViewport().setMaxY(10);
    }

    @Override
    public void onBackPressed() {
        finish();
        return;
    }

    @Override
    public void onPause() {
        finish();
        return;
    }

    private void addEntry() {
        try{
            series.appendData(new DataPoint(X.get(lastX).doubleValue(),Y.get(lastX).doubleValue()), false, 10000);
            series2.appendData(new DataPoint(X.get(lastX).doubleValue(),Z.get(lastX).doubleValue()), false, 10000);
            scaleMaps();
            //Log.v("mccData","addEntry"+lastX);
            lastX++;
            if(lastX==X.size()){
                graphing = false;
            }
        }catch (IllegalArgumentException e){
            Log.v("mccData","e = "+e);
        }
    }
    private void plotRealTime() {

        new Thread(new Runnable() {

            @Override
            public void run() {

                for (int i = 0; i < X.size(); i++) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            addEntry();                        }
                    });

                    // sleep to slow down the add of entries
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }
                }



            }
        }).start();

    }

    public void holeInOneQuestionMark(){
        double dist = Math.abs(Math.sqrt(Math.pow((distanceToHole-x),2.0) + z*z));
        if(dist<.10795){//4.25in
            Toast.makeText(this, "HOLE IN ONE!!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "You were "+(int)dist+" meters off", Toast.LENGTH_SHORT).show();
        }
    }

    //2spinner voids
    public void onItemSelected(AdapterView<?> parent, View view,int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        String selectedItem = "";
        try {
            selectedItem = spinner.getSelectedItem().toString();
            Log.v("mccData","selectedItem = "+selectedItem);
        }catch (Exception e){
            Log.v("mccData","err = "+e);
        }
        switch (selectedItem) {
            case "Driver":
                v0 = 167;
                thetaUp0 = 11;
                omega0 = 2700;
                break;
            case "2 Wood":
                v0 = 163;
                thetaUp0 = 10;
                omega0 = 3100;
                break;
            case "3 Wood":
                v0 = 158;
                thetaUp0 = 9;
                omega0 = 3600;
                break;
            case "4 Wood":
                v0 = 155;
                thetaUp0 = 9;
                omega0 = 3900;
                break;
            case "5 Wood":
                v0 = 152;
                thetaUp0 = 9;
                omega0 = 4300;
                break;
            case "6 Wood":
                v0 = 149;
                thetaUp0 = 10;
                omega0 = 4400;
                break;
            case "7 Wood":
                v0 = 146;
                thetaUp0 = 10;
                omega0 = 4500;
                break;
            case "3 Iron":
                v0 = 142;
                thetaUp0 = 10;
                omega0 = 4600;
                break;
            case "4 Iron":
                v0 = 137;
                thetaUp0 = 11;
                omega0 = 4800;
                break;
            case "5 Iron":
                v0 = 132;
                thetaUp0 = 12;
                omega0 = 5400;
                break;
            case "6 Iron":
                v0 = 127;
                thetaUp0 = 14;
                omega0 = 6200;
            case "7 Iron":
                v0 = 120;
                thetaUp0 = 16;
                omega0 = 7100;
                break;
            case "8 Iron":
                v0 = 115;
                thetaUp0 = 18;
                omega0 = 8000;
                break;
            case "9 Iron":
                v0 = 109;
                thetaUp0 = 20;
                omega0 = 8600;
                break;
            case "Wedge":
                v0 = 102;
                thetaUp0 = 24;
                omega0 = 9300;
                break;
            case "Chuck Norris":
                v0 = 167000;
                thetaUp0 = 11;
                omega0 = 0;
                break;
            default:
                v0 = 0;
                thetaUp0 = 0;
                omega0 = 0;
        }


    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


    double ddr(double j,double t_i,double[] R_i,double[] VR_i,double[] V_i) {
        if(j==0){
            if(VR_i[0]>0){//CD is oposite velocity
                return -(((-1*CD)*p*A)/(2.0*mass)) * VR_i[0] * Math.abs(VR_i[0]) + ((radius*p*A)/(2.0*mass))*(spinY*VR_i[2]-spinZ*VR_i[1]);
            }else{
                return -((CD*p*A)/(2.0*mass)) * VR_i[0] * Math.abs(VR_i[0]) + ((radius*p*A)/(2.0*mass))*(spinY*VR_i[2]-spinZ*VR_i[1]);
            }
        }else if(j==1){
            if(VR_i[1]>0) {//CD is oposite velocity
                return -g - (((-1*CD) * p * A) / (2.0 * mass)) * VR_i[1] * Math.abs(VR_i[1]) + ((radius * p * A) / (2.0 * mass)) * (spinZ * VR_i[0] - spinX * VR_i[2]);
            }else{
                return -g - ((CD * p * A) / (2.0 * mass)) * VR_i[1] * Math.abs(VR_i[1]) + ((radius * p * A) / (2.0 * mass)) * (spinZ * VR_i[0] - spinX * VR_i[2]);
            }
        }else if(j==2){
            if(VR_i[2]>0) {//CD is oposite velocity
                return -(((-1*CD) * p * A) / (2.0 * mass)) * VR_i[2] * Math.abs(VR_i[2]) + ((radius * p * A) / (2.0 * mass)) * (spinX * VR_i[1] - spinY * VR_i[0]);
            }else{
                return -((CD * p * A) / (2.0 * mass)) * VR_i[2] * Math.abs(VR_i[2]) + ((radius * p * A) / (2.0 * mass)) * (spinX * VR_i[1] - spinY * VR_i[0]);
            }
        }
        return 0;
    }
    double dr(double j,double t_i,double[] R_i,double[] VR_i,double[] V_i) {
        if(j==0){
            //return V_i[0]-VWx;
            return Vx-VWx;
        }else if(j==1){
            //return V_i[1]-VWy;
            return Vy-VWy;
        }else if(j==2){
            //return V_i[2]-VWy;
            return Vz-VWz;
        }
        return 0;
    }

}
