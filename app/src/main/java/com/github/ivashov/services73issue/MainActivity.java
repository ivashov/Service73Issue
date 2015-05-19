package com.github.ivashov.services73issue;

import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.PipedInputStream;

public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks {
	private GoogleApiClient googleClient;

	private boolean isTestWithData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (intent.getAction() == null) {
			return;
		}

		if (intent.getAction().equals("com.github.ivashov.services73issue.ACTION_LOCATION")) {
			Location location = intent.getParcelableExtra(FusedLocationProviderApi.KEY_LOCATION_CHANGED);
			int data = intent.getIntExtra("data", 0);

			Toast.makeText(this, "Location: " + location, Toast.LENGTH_SHORT).show();
			Toast.makeText(this, "Data: " + data, Toast.LENGTH_SHORT).show();

			if (googleClient.isConnected()) {
				googleClient.disconnect();
			}
		}
	}

	@Override
	public void onConnected(Bundle bundle) {
		LocationRequest locationRequest = LocationRequest.create();
		locationRequest.setExpirationDuration(60000);
		locationRequest.setNumUpdates(1);
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		LocationServices.FusedLocationApi.requestLocationUpdates(googleClient, locationRequest, getLocationPendingIntent());
	}

	@Override
	public void onConnectionSuspended(int i) {
	}

	private PendingIntent getLocationPendingIntent() {
		Intent intent = new Intent("com.github.ivashov.services73issue.ACTION_LOCATION", null, this, MainActivity.class);

		if (isTestWithData) {
			intent.putExtra("data", 123);
		}
		return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
	}

	private void connect() {
		googleClient = new GoogleApiClient.Builder(this)
				.addApi(LocationServices.API)
				.addConnectionCallbacks(this)
				.build();
		googleClient.connect();
	}

	public void onTestWithDataClicked(View view) {
		isTestWithData = true;
		connect();
	}

	public void onTestWithoutDataClicked(View view) {
		isTestWithData = false;
		connect();
	}
}
