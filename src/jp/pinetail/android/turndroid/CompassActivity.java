package jp.pinetail.android.turndroid;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

public class CompassActivity extends Activity 
		implements LocationListener, SensorEventListener {

  private LocationManager locationManager;
  private SensorManager sensorManager;
  private TextView trueNorthView;
  private TextView magneticNorthView;
  private GeomagneticField geomagnetic;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    trueNorthView = (TextView)findViewById(R.id.trueNorth);
    magneticNorthView = (TextView)findViewById(R.id.magneticNorth);
    sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
  }

  @Override
  protected void onResume() {
    super.onResume();
    List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
    for (Sensor s : sensors) {
      sensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
    }
    String provider = locationManager.getBestProvider(new Criteria(), true);
    locationManager.requestLocationUpdates(provider, 0, 0, this);
  }

  public void onLocationChanged(Location location) {
    float latitude = new Double(location.getLatitude()).floatValue();
    float longitude = new Double(location.getLongitude()).floatValue();
    float altitude = new Double(location.getAltitude()).floatValue();
    geomagnetic = new GeomagneticField(
        latitude, longitude, altitude, new Date().getTime());
  }

  public void onProviderDisabled(String provider) {
  }

  public void onProviderEnabled(String provider) {
  }

  public void onStatusChanged(String provider, int status, Bundle extras) {
  }

  public void onAccuracyChanged(Sensor sensor, int accuracy) {
  }

  public void onSensorChanged(SensorEvent e) {
    switch(e.sensor.getType()) {
    // åXÇ´
    case Sensor.TYPE_ORIENTATION:
      float magneticNorth = e.values[SensorManager.DATA_X];
      if (geomagnetic != null) {
        // ê^ñk
        float trueNorth = magneticNorth + geomagnetic.getDeclination();
        trueNorthView.setText(String.valueOf(trueNorth));
      }
      // é•ñk
      magneticNorthView.setText(String.valueOf(magneticNorth));
      break;
    }
  }
	
  @Override
  protected void onPause() {
    locationManager.removeUpdates(this);
    sensorManager.unregisterListener(this);
    super.onPause();
  }
}
