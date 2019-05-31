package cw.coursework.Database;

import android.app.Activity;
import android.view.View;

import cw.coursework.Item;
import cw.coursework.UsersInfo;
import cw.coursework.itemOffers;

/**
 * Created by pavlo on 27/02/2018.
 */

public class DatabaseRequest {
    DatabaseRes res;
    String username;
    String password;
    View id ;
    Activity window;
    private Item item;

    public itemOffers getOffer() {
        return offer;
    }

    private itemOffers offer;


    private UsersInfo user;



    public DatabaseRequest(DatabaseRes res, String username, Activity act)
    {
        this.res = res;
        this.username = username;
        this.window = act;

    }

    public DatabaseRequest(Activity window,DatabaseRes res,Item item)
    {
        this.item= item;
        this.res= res;
        this.window = window;

    }

    public DatabaseRequest(DatabaseRes res, String username, String password, Activity window)
    {
        this.res = res;
        this.username = username;
        this.password= password;
        this.id= id;
        this.window = window;
    }

    public DatabaseRequest(DatabaseRes res,Item i, UsersInfo s,itemOffers offer, Activity window)
    {
        this.offer = offer;
        this.window = window;
        this.res =res;
        this.item = i;
        this.user = s;
    }

    public DatabaseRes getRes()
    {
        return res;
    }
    public String getUsername()
    {
        return username;
    }

    public  String getPassword()
    {
        return password;
    }
    public View getID(){return id;}
    public Activity getWindow()
    {
        return window;
    }
    public UsersInfo getUser() {
        return user;
    }
    public Item getItem() {
        return item;
    }
}
