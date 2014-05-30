package com.marbol.marbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class AdventureActivity extends FragmentActivity implements ActionBar.TabListener {

	private AdventureDataSource dSource;
	private SharedPreferences prefs;
	private MarbolLocationListener locationListener;
	private Adventure curAdventure;
	private int gpsPollTime;
	private LocationManager locationManager;
	private boolean newAdventure;
	private List<Fragment> fragmentList;
	private long curID; 
	private final int numFragments = 2;
	private boolean running = false;
	private long chronoBase;
	
	private MarbolWorkerThread workerThread;
	private Timer marbolTimer;

	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	public AdventureActivity(){
		newAdventure = false;
		fragmentList = new ArrayList<Fragment>();
		marbolTimer = new Timer("MarbolTimer", true);
		chronoBase = 0;
	}
	
	public boolean isNewAdventure() {
		return newAdventure;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_adventure);
		
		Log.i("INFO", "Adventure Activity on create called");
		if (savedInstanceState != null){
			running = savedInstanceState.getBoolean("com.marbol.marbol.isRunning", false);
		}
		
		dSource = new AdventureDataSource(this);
		
		// get the preference and the gps poll time
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.gpsPollTime = Integer.parseInt(prefs.getString("gpsPollTime", "30")) * 1000;
		
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		
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

		// open the DB and got fetch our current adventure if we have one
		dSource.open();
		savedInstanceState = getIntent().getExtras();
		if (savedInstanceState != null){
			curID = savedInstanceState.getLong("com.marbol.marbol.curAdventure", -1 );
			if (curID == -1){
				Log.i("INFO", "No current adventure provided.");
				curAdventure = new Adventure();
				newAdventure = true;
			}
			else{
				Log.i("INFO", "Loading adventure: " + (long)curID);
				curAdventure = dSource.getAdventure((long)curID);
				newAdventure = false;
			}
			
			running =  savedInstanceState.getBoolean("com.marbol.marbol.isRunning", false);
		}
		else{
			Log.i("INFO", "No saved instance state!");
			curAdventure = new Adventure();
			newAdventure = true;
		}			
		
		dSource.close();
		workerThread = new MarbolWorkerThread(this, dSource);
		
		// only start us running if we were running before the activity was created
		// i.e if we've had a orientation change and now need to keep running
		if (running) {
			setRunning(running);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putLong("curAdvID", curID);
		savedInstanceState.putBoolean("isRunning", running);
		savedInstanceState.putLong("chronoBase", 0);
	}
	
	@Override
	public void onDestroy(){
		workerThread.setRunning(false);
		super.onDestroy();
	}
	
	@Override
	public void onAttachFragment (Fragment fragment) {
		
		// if this fragment is a MarbolUIFragment then add it to the fragment list.
		if (MarbolUIFragment.class.isAssignableFrom(fragment.getClass()))
		{
			fragmentList.add(fragment);	
		}
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
				fragment = new MarbolMapFragment();
				break;
			default:
				fragment = new Fragment();
			}
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return numFragments;
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
		this.newAdventure = false;
	}

	public void setRunning(boolean running) {
		
		if (running){
			this.gpsPollTime = Integer.parseInt(prefs.getString("gpsPollTime", "30")) * 1000;
			//we are running so we should subscribe to location updates
			//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
			workerThread.setRunning(true);
			marbolTimer.schedule(workerThread, this.gpsPollTime, this.gpsPollTime);
			running = true;
		}
		else{
			marbolTimer.cancel();
			running = false;
			workerThread.setRunning(false);
		}
		
		// sync the current adventure with the database
		if (curAdventure != null && running == false)
		{
			Log.i("DB", "Updating adventure");
			dSource.open();
			dSource.updateAdventure(curAdventure);
			dSource.close();
		}
		
	}

	// in the event that we can't get a location from the GPS or our location has changed loop over
	// all location providers and try to provide a "best guess"
	public Location findNearestLocation()
	{
		Location bestLocation = null;
		// register the location listener
		locationListener = new MarbolLocationListener();
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		
		List<String> providers = locationManager.getAllProviders();

		for (String provider : providers)
		{
			Location lastLocation = locationManager.getLastKnownLocation(provider);

			if (lastLocation != null && bestLocation != null)
			{
				// use the most accurate location we have
				bestLocation = bestLocation.getAccuracy() > lastLocation.getAccuracy() ? bestLocation : lastLocation;
				
			}
			else if (bestLocation == null && lastLocation != null)
			{
				bestLocation = lastLocation;
			}
		}
		
		locationManager.removeUpdates(this.locationListener);
		return bestLocation;
	}
	
	public void updateAdventures(Adventure updatedAdventure)
	{
		// NOTE the fragmentList should only ever contain MarbolUIFragments otherwise explosions happen
		this.curAdventure = updatedAdventure;
		for (Fragment f : fragmentList){
			((MarbolUIFragment) f).updateAdventure(curAdventure);
		}
		Log.i("ADVENTURE ACTIVITY", "Updating all fragments");
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    for (Fragment f: fragmentList) {
	    	((MarbolUIFragment)f).orientationChange(newConfig);
	    }
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){		
		switch(item.getItemId()){
		case R.id.action_settings:
			// launch the settings activity
			Intent settingsIntent = new Intent(this, SettingsActivity.class);
			startActivity(settingsIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}

	public long getChronoBase() {
		return chronoBase;
	}
	
	public void setChronoBase(long newBase) {
		chronoBase = newBase;
	}
}
