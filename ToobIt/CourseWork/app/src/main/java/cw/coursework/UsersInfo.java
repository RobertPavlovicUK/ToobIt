package cw.coursework;

import android.app.Activity;
import android.location.Location;

/**
 * Created by pavlo on 21/03/2018.
 */

public class UsersInfo {

    private String username;
    private Location currentLocation;

    public UsersInfo(String username, Activity act)
    {
        currentLocation = LocationService.getInstance(act).getLoc();
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }

    public Location getCurrentLocation()
    {
        return currentLocation;
    }
}
