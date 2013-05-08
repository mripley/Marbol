package com.marbol.marbol;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;

/**
 * Fragment containing the logic to handle the "Map" Fragment
 */
public class AdventureFragment extends Fragment implements View.OnClickListener{

	private View rootView;
	private Boolean running;
	private long chronoBase;
	public AdventureFragment() {
		rootView = null;
		running = false;
		chronoBase = 0;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.adventure_fragment, container, false);
		
		// set the on click listener for the start button
		Button button = (Button)rootView.findViewById(R.id.start_adventure_button);
		button.setOnClickListener(this);
		
		// set the on click listener for the stop button
		button = (Button)rootView.findViewById(R.id.stop_adventure_button);
		button.setOnClickListener(this);
		
		return rootView;
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
	}

	public void startAdventureButtonClicked(View v){
		running = true;
		toggleUI(running);
	}
	
	public Boolean isRunning(){
		return this.running;
	}
	
	public void toggleUI(Boolean running){
		
		Button startButton = (Button)rootView.findViewById(R.id.start_adventure_button);
		Button endButton = (Button)rootView.findViewById(R.id.stop_adventure_button);

		startButton.setVisibility(running ? Button.INVISIBLE : Button.VISIBLE);
		startButton.setClickable(!running);
		
		endButton.setVisibility(running ? Button.VISIBLE : Button.INVISIBLE);
		endButton.setClickable(running);
		
		Chronometer chrono = (Chronometer)rootView.findViewById(R.id.timer_clock);
		if (running){
			Log.i("CHRONO", "Setting chrono to "+this.chronoBase);
			chrono.setBase(chronoBase);
			chrono.start();
		}
		else{
			chronoBase = chrono.getBase();
			Log.i("CHRONO", "setting chronoBase to "+this.chronoBase);
			chrono.stop();
		}
	}
}