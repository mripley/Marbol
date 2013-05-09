package com.marbol.marbol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Fragment containing the logic to handle the "Map" Fragment
 */
public class NewAdventureFragment extends Fragment implements View.OnClickListener {

	public NewAdventureFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.new_adventure_fragment_layout, container, false);
		Button newAdvButton = (Button)rootView.findViewById(R.id.NewAdventureButtonMain);
		newAdvButton.setOnClickListener(this);
		
		return rootView;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.NewAdventureButtonMain:
			Log.i("UI", "Main New Adventure Button Pressed!");
			Intent launcher = new Intent(this.getActivity(), AdventureActivity.class);
			launcher.putExtra("curAdventure", -1);
			this.startActivity(launcher);
		}
	}
}