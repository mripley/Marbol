package com.marbol.marbol;

import android.content.res.Configuration;

public interface MarbolUIFragment {
	abstract void updateAdventure(Adventure adv);

	abstract void orientationChange(Configuration newConfig);
}
