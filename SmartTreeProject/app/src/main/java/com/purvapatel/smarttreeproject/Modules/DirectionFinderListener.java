package com.purvapatel.smarttreeproject.Modules;

import java.util.List;

/**
 * Created by Purva Patel on 9/12/2017.
 */
public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
