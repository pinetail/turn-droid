package jp.pinetail.android.turndroid;

import java.util.List;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class GameActivity extends Activity implements SensorEventListener {

	private SensorManager mSensorManager;
	private Integer[] Azimuth = {0,0};
	private Integer TurnCnt = 0;
	private float FirstAzimuth = 0;
	private float FirstAzimuth90 = 0;
	private float TaikakuAzimuth = 0;
	private float TaikakuAzimuth90 = 0;
    private static final int MATRIX_SIZE = 16;
    /* ��]�s�� */
    float[]  inR = new float[MATRIX_SIZE];
    float[] outR = new float[MATRIX_SIZE];
    float[]    I = new float[MATRIX_SIZE];
    
    /* �Z���T�[�̒l */
    float[] orientationValues   = new float[3];
    float[] magneticValues      = new float[3];
    float[] accelerometerValues = new float[3];
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	}
	
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
	}

	/*
	public void onSensorChanged(SensorEvent event) {
		
		if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) return;
	
		switch (event.sensor.getType()) {
			case Sensor.TYPE_MAGNETIC_FIELD:
				magneticValues = event.values.clone();
				break;
			case Sensor.TYPE_ACCELEROMETER:
				accelerometerValues = event.values.clone();
	                             break;
		}
	
		if (magneticValues != null && accelerometerValues != null) {
	
			SensorManager.getRotationMatrix(inR, I, accelerometerValues, magneticValues);
	
			//Activity�̕\�����c�Œ�̏ꍇ�B�������ɂȂ�ꍇ�A�C�����K�v�ł�
			SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_X, SensorManager.AXIS_Z, outR);
			SensorManager.getOrientation(outR, orientationValues);
	
			TextView text    = (TextView) findViewById(R.id.text);
			text.setText(
	          String.valueOf( radianToDegree(orientationValues[0]) ) + ", " + //Z������,azmuth
	          String.valueOf( radianToDegree(orientationValues[1]) ) + ", " + //X������,pitch
		            String.valueOf( radianToDegree(orientationValues[2]) ) );       //Y������,roll
		}
	}
	
	int radianToDegree(float rad){
		return (int) Math.floor( Math.toDegrees(rad) ) ;
	}
*/
	public void onSensorChanged(SensorEvent event) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			
			if (FirstAzimuth == 0) {
				FirstAzimuth = event.values[0];
			}
			
			TextView text    = (TextView) findViewById(R.id.text);
			text.setText("���ʁF"+String.valueOf(event.values[0])+":�X���F"+String.valueOf(event.values[1])+":"+String.valueOf(event.values[2]));
			
			if (0 <= FirstAzimuth && FirstAzimuth <= 90) {
				// 180~270
				TaikakuAzimuth = FirstAzimuth + 180;				

				if (FirstAzimuth <= event.values[0] && event.values[0] <= FirstAzimuth + 90) {
					check();
					Azimuth[0] = 1;
				} else if (TaikakuAzimuth <= event.values[0] && event.values[0] <= TaikakuAzimuth + 90) {
					Azimuth[1] = 1;
				}

			} else if (91 <= FirstAzimuth && FirstAzimuth <= 180) {
				// 271~360
				TaikakuAzimuth = FirstAzimuth + 180;				
				
				if (FirstAzimuth <= event.values[0] && event.values[0] <= FirstAzimuth + 90) {
					check();
					Azimuth[0] = 1;
				} else if (   (TaikakuAzimuth <= event.values[0] && event.values[0] <= 360)
						|| (0 <= event.values[0] && event.values[0] <= TaikakuAzimuth + 90 - 360)) {
					Azimuth[1] = 1;
				}

			} else if (181 <= FirstAzimuth && FirstAzimuth <= 270) {
				// 1~90
				TaikakuAzimuth = FirstAzimuth + 180 - 360;				
				
				if (FirstAzimuth <= event.values[0] && event.values[0] <= FirstAzimuth + 90) {
					check();
					Azimuth[0] = 1;
				} else if (TaikakuAzimuth <= event.values[0] && event.values[0] <= TaikakuAzimuth + 90) {
					Azimuth[1] = 1;
				}

			} else if (271 <= FirstAzimuth && FirstAzimuth <= 360) {
				// 91~180
				TaikakuAzimuth = FirstAzimuth + 180 - 360;

				//
				if (   (FirstAzimuth <= event.values[0] && event.values[0] <= 360)
					|| (0 <= event.values[0] && event.values[0] <= FirstAzimuth + 90 - 360)) {
					check();
					Azimuth[0] = 1;
				} else if (TaikakuAzimuth <= event.values[0] && event.values[0] <= TaikakuAzimuth + 90) {
					Azimuth[1] = 1;
				}

			}
			
	
		}
		
	}
	
	private void check() {
		int sum = 0;
		for (int i = 0;i < Azimuth.length; i++) {
			sum += Azimuth[i];
		}
		
		if (sum == 2) {
			TurnCnt += 1;
			for (int i = 0;i < Azimuth.length; i++) {
				Azimuth[i] = 0;
			}
			
		}
		
		TextView txt_cnt = (TextView) findViewById(R.id.txt_cnt);
		txt_cnt.setText("��]���F"+String.valueOf(TurnCnt));
	}
	/*
	@Override
	protected void onPause() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		super.onPause();

		//�Z���T�[�}�l�[�W���̃��X�i�o�^�j��
	    if (mIsMagSensor || mIsAccSensor) {
	        mSensorManager.unregisterListener(this);
	        mIsMagSensor = false;
	        mIsAccSensor = false;
	    }
	}
*/
	public void onPause() {
		super.onPause();
		//
		mSensorManager.unregisterListener(this);
	}
	/*
	private boolean mIsMagSensor;
	private boolean mIsAccSensor;

	@Override
	protected void onResume() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		super.onResume();

		// �Z���T�̎擾
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

	    // �Z���T�}�l�[�W���փ��X�i�[��o�^(implements SensorEventListener�ɂ��Athis�œo�^����)
        for (Sensor sensor : sensors) {

        	if( sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
        		mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        		mIsMagSensor = true;
        	}

        	if( sensor.getType() == Sensor.TYPE_ACCELEROMETER){
        		mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        		mIsAccSensor = true;
        	}
        }
	}

	*/
	public void onResume() {
		super.onResume();
		//
		List<Sensor> list = mSensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
		
		if (list.size() < 1) {
			return;
		}
		
		Sensor sensor = list.get(0);
		
		mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
	}
	

}
