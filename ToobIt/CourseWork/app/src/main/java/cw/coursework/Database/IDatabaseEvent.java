package cw.coursework.Database;

import java.util.ArrayList;

import cw.coursework.Item;
import cw.coursework.UsersInfo;
import cw.coursework.itemOffers;

/**
 * Created by pavlo on 02/03/2018.
 */

public interface IDatabaseEvent {

    public void OnLoggedIn(UsersInfo info);
    public void OnRegistered();
    public void OnFailLogIn(DatabaseRes res);
    void onPostItemSuccess();
    void onCommentsRequests(ArrayList<itemOffers> comments);
    void OnRequestItems(ArrayList<Item> images);
}
