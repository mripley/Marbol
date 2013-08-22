package com.marbol.marbol;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Fragment containing the logic to handle the "Map" Fragment
 */
public class AdventureFragment extends Fragment implements View.OnClickListener, MarbolUIFragment{

	private View rootView;
	private Boolean running;
	private Boolean firstRun;
	private long lastPause;
	
	private Adventure curAdventure;
	private AdventureDataSource dSource;
	
	public AdventureFragment() {
		curAdventure = null;
		rootView = null;
		running = false;
		firstRun = true;
		lastPause = 0;
		Log.i("Adventure", "Adventure Fragment created!");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.adventure_fragment, container, false);
		
		// get the data source
		dSource = new AdventureDataSource(this.getActivity());
		
		// set the on click listener for the start button
		ImageButton button = (ImageButton)rootView.findViewById(R.id.start_adventure_button);
		button.setOnClickListener(this);
		
		// set the on click listener for the stop button
		button = (ImageButton)rootView.findViewById(R.id.stop_adventure_button);
		button.setOnClickListener(this);
		
		return rootView;
	}
		
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		AdventureActivity activity = (AdventureActivity)this.getActivity();
		this.curAdventure = activity.getCurAdventure();
		
		EditText text = (EditText)rootView.findViewById(R.id.AdventureNameEdit);
		text.setText(curAdventure.getAdvName());
		
		
		if (!activity.isNewAdventure()){
			firstRun = false;
			Chronometer chrono = (Chronometer)rootView.findViewById(R.id.timer_clock);
		
			long seconds = convertToSeconds(curAdventure.getAdvTime());
			chrono.setText("0:"+Long.toString(seconds));
			lastPause = curAdventure.getAdvTime();
			
			chrono.setBase(SystemClock.elapsedRealtime() - lastPause);
			
			ImageButton stopButton = (ImageButton)rootView.findViewById(R.id.start_adventure_button);
			//stopButton.setText("Resume Adventure");
			stopButton.setImageResource(R.drawable.new_adventure_button_sm);
			stopButton.setVisibility(Button.VISIBLE);
			
			updateUI(curAdventure);
		}
		else
		{
			firstRun = true;
		}
	}
	
	public void onClick(View v){		
		switch(v.getId()){
		case R.id.start_adventure_button:
			startAdventureButtonClicked(v);
			break;
		case R.id.stop_adventure_button:
			stopAdventureButtonClicked(v);
			break;
		default:
			Log.e("UI", "Spurrious button click");
		}
		
	}
	
	private void stopAdventureButtonClicked(View v) {
		running = false;
		toggleUI(running);
		
		// got tell our activity that we are running
		AdventureActivity activity = (AdventureActivity)this.getActivity();
		activity.setRunning(false);
	}

	public void startAdventureButtonClicked(View v){
		EditText advNameEdit = (EditText)rootView.findViewById(R.id.AdventureNameEdit);
		String newAdvName = advNameEdit.getText().toString();
		
		running = true;
	
		toggleUI(running);

		curAdventure  = ((AdventureActivity)this.getActivity()).getCurAdventure();
		curAdventure.setAdvName(newAdvName);
		
		TextView advTitle = (TextView)rootView.findViewById(R.id.adventure_title);
		advTitle.setText(newAdvName);

		if (firstRun)
		{
			// open the data base
			dSource.open();
			// write our adventure to the db before we start recording
			dSource.addAdventure(curAdventure);
			dSource.close();	
		}
		
		if (firstRun){
			firstRun = false;
		}		
		// got tell our activity that we are running
		AdventureActivity activity = (AdventureActivity)this.getActivity();
		activity.setRunning(true);
	}
	
	private void updateUI(Adventure adv){
		TextView text;
		text = (TextView)rootView.findViewById(R.id.total_points_view);
		text.setText(adv.getNumGpsPoints().toString());
	
		Double area = adv.getAdvArea();
		text = (TextView)rootView.findViewById(R.id.area_view);
		text.setText(area.toString());
		
		Double distance = adv.getAdvDistance();
		text = (TextView)rootView.findViewById(R.id.distance_view);
		text.setText(distance.toString());
		
		Double elevation = adv.getElevationChange();
		text = (TextView)rootView.findViewById(R.id.elevation_view);
		text.setText(elevation.toString());
		
		Double speed = adv.getAverageSpeed();
		text = (TextView)rootView.findViewById(R.id.speed_view);
		text.setText(speed.toString());
	}
	
	// call back for the main adventure activity to update the current adventure
	public void updateAdventure(Adventure adv){
		this.curAdventure = adv;
		updateUI(curAdventure);
	}
	
	public Boolean isRunning(){
		return this.running;
	}
	
	public void toggleUI(Boolean running){
		
		ImageButton startButton = (ImageButton)rootView.findViewById(R.id.start_adventure_button);
		ImageButton endButton = (ImageButton)rootView.findViewById(R.id.stop_adventure_button);

		startButton.setVisibility(running ? Button.INVISIBLE : Button.VISIBLE);
		startButton.setClickable(!running);
		
		// if this isn't our first run update the text to "resume"
//		if (!firstRun){
//			startButton.setText("Resume Adventure");
//		}
//		
		endButton.setVisibility(running ? Button.VISIBLE : Button.INVISIBLE);
		endButton.setClickable(running);
		
		Chronometer chrono = (Chronometer)rootView.findViewById(R.id.timer_clock);
		if (running){
			Log.i("CHRONO", "Setting chrono to "+this.lastPause);
			chrono.setBase(SystemClock.elapsedRealtime() - lastPause);
			chrono.start();
		}
		else{
			lastPause = convertToMilli(chrono.getText().toString());
			curAdventure.setAdvTime(lastPause);
			Log.i("CHRONO", "setting chronoBase to "+this.lastPause);
			chrono.stop();
		}

		EditText adventureEdit = (EditText)rootView.findViewById(R.id.AdventureNameEdit);
		adventureEdit.setVisibility(running ? EditText.INVISIBLE : EditText.VISIBLE);
		
		// all the other labels
		TextView text = (TextView)rootView.findViewById(R.id.adventure_title);
		text.setVisibility(running ? TextView.VISIBLE : TextView.INVISIBLE);
		
		ImageView img;
		img = (ImageView)rootView.findViewById(R.id.area_icon);
		img.setVisibility(running ? TextView.VISIBLE : TextView.INVISIBLE);
		
		img = (ImageView)rootView.findViewById(R.id.elevation_icon);
		img.setVisibility(running ? TextView.VISIBLE : TextView.INVISIBLE);
		
		img = (ImageView)rootView.findViewById(R.id.speed_icon);
		img.setVisibility(running ? TextView.VISIBLE : TextView.INVISIBLE);
		
		img = (ImageView)rootView.findViewById(R.id.distance_icon);
		img.setVisibility(running ? TextView.VISIBLE : TextView.INVISIBLE);
		
		text = (TextView)rootView.findViewById(R.id.total_points_label);
		text.setVisibility(running ? TextView.VISIBLE : TextView.INVISIBLE);
		
		// the actual values for said labels
		text = (TextView)rootView.findViewById(R.id.total_points_view);
		text.setVisibility(running ? TextView.VISIBLE : TextView.INVISIBLE);
		
		text = (TextView)rootView.findViewById(R.id.area_view);
		text.setVisibility(running ? TextView.VISIBLE : TextView.INVISIBLE);
		
		text = (TextView)rootView.findViewById(R.id.distance_view);
		text.setVisibility(running ? TextView.VISIBLE : TextView.INVISIBLE);
		
		text = (TextView)rootView.findViewById(R.id.elevation_view);
		text.setVisibility(running ? TextView.VISIBLE : TextView.INVISIBLE);
		
		text = (TextView)rootView.findViewById(R.id.speed_view);
		text.setVisibility(running ? TextView.VISIBLE : TextView.INVISIBLE);
		
		// lets make the layout and their strokes visable
		LinearLayout layout = (LinearLayout)rootView.findViewById(R.id.distance_layout);
		layout.setVisibility(running ? LinearLayout.VISIBLE : LinearLayout.INVISIBLE);
		
		layout = (LinearLayout)rootView.findViewById(R.id.area_layout);
		layout.setVisibility(running ? LinearLayout.VISIBLE : LinearLayout.INVISIBLE);
		
		layout = (LinearLayout)rootView.findViewById(R.id.elevation_layout);
		layout.setVisibility(running ? LinearLayout.VISIBLE : LinearLayout.INVISIBLE);
		
		layout = (LinearLayout)rootView.findViewById(R.id.speed_layout);
		layout.setVisibility(running ? LinearLayout.VISIBLE : LinearLayout.INVISIBLE);
	}
	
	// convert a string containing a human readable time into milliseconds
	private long convertToMilli(String time) {
		
		String[] split = time.split(":");
		// the H:MM:SS case
		if(split.length == 3){
			long hours = Long.parseLong(split[0]) * 3600;
			long minutes = Long.parseLong(split[1]) * 60;
			long seconds = Long.parseLong(split[2]);
			
			return (hours + minutes + seconds) * 1000;
		}
		else{  // just MM:SS
			long minutes = Long.parseLong(split[0]) * 60;
			long seconds = Long.parseLong(split[1]);
			return (minutes + seconds) * 1000;
		}
	}
	
	private long convertToSeconds(long milliTime){
		return milliTime / 1000;
	
	}

}