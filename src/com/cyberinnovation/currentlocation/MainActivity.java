package com.cyberinnovation.currentlocation;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.internal.lg;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener {

	private static final String TAG = MainActivity.class.getSimpleName();
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
	private Location myLocation;
	private GoogleApiClient myGoogleApiClient;

	private boolean mRequestingLocationUpdates = false;

	private LocationRequest myLocationRequest;

	private static int UPDATE_INTERVAL = 10000; // 10 sec
	private static int FATEST_INTERVAL = 5000; // 5 sec
	private static int DISPLACEMENT = 10; // 10 meters

	private Geocoder geocoder;
	private List<Address> addresses;

	double latitude;
	double longitude;
	private TextView lblLocation;
	private Button btnShowLocation, btnStartLocationUpdates;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		geocoder = new Geocoder(this, Locale.getDefault());
		lblLocation = (TextView) findViewById(R.id.lblLocation);
		btnShowLocation = (Button) findViewById(R.id.btnShowLocation);
		btnStartLocationUpdates = (Button) findViewById(R.id.btnLocationUpdates);

		if (checkPlayServices()) {
			buildGoogleApiClient();
		}

		btnShowLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				displayLocation();

				try {
					addresses = geocoder
							.getFromLocation(latitude, longitude, 5);

					String address = addresses.get(0).getAddressLine(0);
					String city = addresses.get(0).getAddressLine(1);
					String countyr = addresses.get(0).getAddressLine(2);
					String a = addresses.get(0).getAdminArea();
					TextView textView = new TextView(getApplicationContext());
					textView.setText(address + "" + city + "" + countyr);
					Toast.makeText(
							getApplicationContext(),
							"Address :" + address + ", " + city + ", " + countyr
									+ " ," + a, Toast.LENGTH_LONG).show();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * Method to display the location on UI
	 * */
	private void displayLocation() {

		myLocation = LocationServices.FusedLocationApi
				.getLastLocation(myGoogleApiClient);

		if (myLocation != null) {

			latitude = myLocation.getLatitude();
			longitude = myLocation.getLongitude();

			lblLocation.setText(latitude + ", " + longitude);

		} else {

			lblLocation
					.setText("(Couldn't get the location. Make sure location is enabled on the device)");
		}
	}

	/**
	 * Creating google api client object
	 * */
	protected synchronized void buildGoogleApiClient() {
		myGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();
	}

	/**
	 * Method to verify google play services on the device
	 * */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Toast.makeText(getApplicationContext(),
						"This device is not supported.", Toast.LENGTH_LONG)
						.show();
				finish();
			}
			return false;
		}
		return true;
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (myGoogleApiClient != null) {
			myGoogleApiClient.connect();
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		checkPlayServices();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub

		Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
				+ result.getErrorCode());
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub

		displayLocation();

	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub

		myGoogleApiClient.connect();
	}
}
