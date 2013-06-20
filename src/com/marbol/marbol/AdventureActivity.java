package com.marbol.marbol;

import java.util.Locale;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class AdventureActivity extends FragmentActivity implements
		ActionBar.TabListener {

	private AdventureDataSource dSource;
	private SharedPreferences prefs;
	private LocationListener locationListener;
	private CountDownTimer timer;
	private Adventure curAdventure;
	private Location curLocation;
	private int gpsPollTime;
	private LocationManager locationManager;
	
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adventure);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		dSource = new AdventureDataSource(this);
		
		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		// register the location listener
		locationListener = new MarbolLocationListener();
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		
		dSource.open();
		if (savedInstanceState != null){
			savedInstanceState = getIntent().getExtras();
			int curID = savedInstanceState.getInt("curAdventure", -1 );
			if (curID == -1){
				curAdventure = new Adventure();
			}
			else{
				// TODO: Go get this adventure from the DB
				curAdventure = dSource.getAdventure((long)curID);
			}
		}
		else{
			curAdventure = new Adventure();
		}			
		
		dSource.close();
		
		// count down timer set to our gps poll time. 
		timer = new CountDownTimer(gpsPollTime, 1000){
			@Override 
			public void onFinish(){
				
				if (curAdventure == null){
					Log.i("ERROR", "Cowardly refusing to update due to null cur adventure");
					return;
				}
				if (curLocation == null){
					Log.i("ERROR", "Cowardly refusing to update due to null location ");
					return;
				}
				
				Log.i("GPS", "Adding gpsPoint! Lat:"+curLocation.getLatitude()+" Long:"+ curLocation.getLongitude());
				curAdventure.addGpsPoint(curLocation);
			}

			@Override
			public void onTick(long milliUntilFinished) {
				// TODO Auto-generated method stub
				
			}
			
		};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.adventure, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}


	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			
			Fragment fragment;
			
			switch(position){
			case 0:
				fragment = new AdventureFragment();
				break;
			case 1:
				fragment = new MapFragment();
				break;
			default:
				fragment = new Fragment();
			}
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			//Bundle args = new Bundle();
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.stats_section).toUpperCase(l);
			case 1:
				return getString(R.string.map_section).toUpperCase(l);
			}
			return null;
		}
	}

	public Adventure getCurAdventure() {
		return curAdventure;
	}

	public void setCurAdventure(Adventure curAdventure) {
		this.curAdventure = curAdventure;
	}

	public void setRunning(boolean running) {
		// start the countdown timer
		if (running){
			timer.start();
		}
		else{
			timer.cancel();
		}
		
		// sync the current adventure with the database
	}
	
}
