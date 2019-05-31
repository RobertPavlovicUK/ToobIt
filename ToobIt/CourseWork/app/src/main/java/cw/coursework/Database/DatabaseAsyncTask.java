package cw.coursework.Database;


import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.xml.transform.Result;

import cw.coursework.Item;
import cw.coursework.ItemsDetailsActivity;
import cw.coursework.LocationService;
import cw.coursework.LoginActivity;
import cw.coursework.R;
import cw.coursework.ScrollingActivity;
import cw.coursework.SellActivity;
import cw.coursework.UsersInfo;
import cw.coursework.itemOffers;


/**
 * Created by pavlo on 27/02/2018.
 */

public class DatabaseAsyncTask extends AsyncTask<DatabaseRequest,Void,DatabaseResponse> {

    private static ArrayList<IDatabaseEvent> _listeners = new ArrayList<IDatabaseEvent>();

    Connection con;
    DatabaseRequest req;
    DatabaseRes results;
    private ArrayList<Item> b = new ArrayList<>();
    private ArrayList<itemOffers> comments = new ArrayList<>();

    @Override
    protected DatabaseResponse doInBackground(DatabaseRequest... databaseRequests) {
        try {

            req = databaseRequests[0];
            if (req.getWindow() instanceof ScrollingActivity) {
                System.out.println("Hello???");
                this.addOnLoginListenner((ScrollingActivity) req.getWindow());
            } else {
                System.out.println("Hello??");
            }
            if (req.getWindow() instanceof LoginActivity) {
                System.out.println("Hello LogIn");
                this.addOnLoginListenner((LoginActivity) req.getWindow());
            }
            if (req.getWindow() instanceof SellActivity) {
                System.out.println("Hello LogIn");
                this.addOnLoginListenner((SellActivity) req.getWindow());
            }
            if (req.getWindow() instanceof ItemsDetailsActivity) {
                System.out.println("Hello LogIn");
                this.addOnLoginListenner((ItemsDetailsActivity) req.getWindow());
            }

            System.out.println("Hello");
            Class.forName("com.mysql.jdbc.Driver");

            con = DriverManager.getConnection("jdbc:mysql://172.20.10.6:3306/mobile?user=admin&password=admin");

            System.out.println("Connection Successful");


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {

            e.printStackTrace();
        }
        if (databaseRequests[0].getRes() == DatabaseRes.DATABASE_LOGIN) {

            DatabaseVerifyUser(databaseRequests[0].getUsername(), databaseRequests[0].getPassword());

            return null;

        }
        if (databaseRequests[0].getRes() == DatabaseRes.DATABASE_REGISTER) {

            return new DatabaseResponse(this, DatabaseRegisterUser(databaseRequests[0].getUsername(), databaseRequests[0].getPassword()));

        }
        if (databaseRequests[0].getRes() == DatabaseRes.DATABASE_SEND_ITEM) {
            System.out.println("Hello?");
            SendItem(databaseRequests[0].getItem());

            return new DatabaseResponse(null, null);

        }

        if (databaseRequests[0].getRes() == DatabaseRes.DATABASE_REGISTER) {
            DatabaseRegisterUser(databaseRequests[0].getUsername(), databaseRequests[0].getPassword());
            return null;
        }

        if (databaseRequests[0].getRes() == DatabaseRes.DATABASE_REQUEST_ITEMS) {
            getResults();
            return null;
        }
        if (databaseRequests[0].getRes() == DatabaseRes.DATABASE_MAKE_AN_OFFER) {
           MakeOffer();
            return null;
        }
        if (databaseRequests[0].getRes() == DatabaseRes.DATABASE_RECEIVE_COMMENTS) {
            PullComments();
            return null;
        }
        if (databaseRequests[0].getRes() == DatabaseRes.DATABASE_FACEBOOK_LOGIN) {
            FacebookVerif();
            return null;
        }


        return null;

    }

    private void FacebookVerif() {
        try {
            String username = req.getUsername();
            String databaseResultUsername = "Select * from users where username = ? ";


            PreparedStatement stm = con.prepareStatement(databaseResultUsername);
            stm.setString(1, username);
            String usernameDatabase = "";


            ResultSet result = stm.executeQuery();

            while (result.next()) {
                usernameDatabase = result.getNString("Username");

            }

            if(usernameDatabase == "")
            {
                RegisterFacebookUser(req.getUsername());
                results = DatabaseRes.DATABASE_FACEBOOK_LOGIN;
                return;
            }



            if (isConnectionClosed()) {
                return;
            }





            System.out.println("Accont succesfull");
            results = DatabaseRes.DATABASE_ACCOUNT_LOGIN_SUCCESSFUL;
            return;
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void RegisterFacebookUser(String username) {
        try {
            String SQLInsertQuery = "INSERT INTO users (Username, password) values(?,?)";
            PreparedStatement prep;
            prep = con.prepareStatement(SQLInsertQuery);
            prep.setString(1, username);
            prep.setString(2,"");
            prep.executeUpdate();
        } catch (Exception e)
        {}

    }

    private void PullComments() {


        try {
        String command = "SELECT * FROM items_offers where itemsID = ?";
        PreparedStatement prep = con.prepareStatement(command);
        prep.setInt(1,req.getItem().getItemID());

        ResultSet rs = prep.executeQuery();

        while(rs.next())
        {
            int offerID = rs.getInt("iditems_offers");
            int itemsID = rs.getInt("itemsID");
            String comment = rs.getNString("comment");
            double offerAmmount = rs.getDouble("offer");
            String username = rs.getNString("Usersname");
            int accepted = rs.getInt("Accepted");
            itemOffers offer = new itemOffers(offerID,itemsID,comment,offerAmmount,username,accepted);
            comments.add(offer);

        }
        results = DatabaseRes.DATABASE_RECEIVE_COMMENTS;

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void MakeOffer() {
        try {

        Item i = req.getItem();
        itemOffers o = req.getOffer();

        String command = "INSERT INTO items_offers(itemsID,comment,offer,Usersname,Accepted) values(?,?,?,?,?)";


            PreparedStatement prep = con.prepareStatement(command);
            prep.setInt(1,i.getItemID());
            prep.setString(2,o.getComment());
            prep.setDouble(3,o.getOfferAmmount());
            prep.setString(4,o.getUsername());
            prep.setInt(5,0);
            prep.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(DatabaseResponse res) {
        if (results == DatabaseRes.DATABASE_ACCOUNT_REGISTERED) {


            RegisterSuccess();
        }
        if (results == DatabaseRes.DATABASE_ACCOUNT_NOT_FOUND) {


            OnLogInFail(results);
        }
        if (results == DatabaseRes.DATABASE_ACCOUNT_ALREADY_EXISTS) {
            //    TextView v = (TextView) req.getWindow().findViewById(R.id.email);
            //  v.setError("Account Already Exists");
            OnLogInFail(results);

        }
        if (results == DatabaseRes.DATABASE_ACCOUNT_LOGIN_SUCCESSFUL) {

            OnLogInSuccess();

        }
        if (results == DatabaseRes.DATABASE_ACCOUNT_PASSWORD_INCORRECT) {

            OnLogInFail(results);
        }
        if(results == DatabaseRes.DATABASE_REQUEST_ITEMS)
        {
            OnRequestItems(b);
        }
        if(results == DatabaseRes.DATABASE_ITEM_SENT)
        {
            OnPostItem();
        }
        if(results == DatabaseRes.DATABASE_RECEIVE_COMMENTS)
        {
            OnComments(comments);
        }
        if(results == DatabaseRes.DATABASE_FACEBOOK_LOGIN)
        {
            OnLogInSuccess();
        }

    }

    private DatabaseRes DatabaseRegisterUser(String username, String password) {
        System.out.println("Registering");

        try {
            String usernameCheckQuery = "SELECT * from users where username = ?";

            String SQLInsertQuery = "INSERT INTO users (Username, password) values(?,?)";
            PreparedStatement prep = con.prepareStatement(usernameCheckQuery);

            prep.setString(1, username);


            if (isConnectionClosed()) {
                closeConnection();

                return DatabaseRes.DATABASE_CONNECTION_ERROR;
            }

            ResultSet result = prep.executeQuery();

            System.out.println(result);

            while (result.next()) {
                String usernames = result.getString("Username");
                System.out.println("Checking" + usernames + "against " + username);
                if (usernames.equals(username)) {
                    System.out.println("Closing connection returning existing user");
                    closeConnection();
                    results = DatabaseRes.DATABASE_ACCOUNT_ALREADY_EXISTS;
                    return DatabaseRes.DATABASE_ACCOUNT_ALREADY_EXISTS;
                }
            }

            prep = con.prepareStatement(SQLInsertQuery);
            prep.setString(1, username);
            prep.setString(2, password);
            prep.executeUpdate();
            closeConnection();
            results = DatabaseRes.DATABASE_ACCOUNT_REGISTERED;
            return DatabaseRes.DATABASE_ACCOUNT_REGISTERED;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DatabaseRes DatabaseVerifyUser(String username, String password) {

        try {
            String databaseResultUsername = "Select * from users where username = ? ";


            PreparedStatement stm = con.prepareStatement(databaseResultUsername);
            stm.setString(1, username);
            String usernameDatabase = "";
            String passwordDatabase = "";

            ResultSet result = stm.executeQuery();

            while (result.next()) {
                usernameDatabase = result.getNString("Username");
                passwordDatabase = result.getNString("Password");
            }


            if (isConnectionClosed()) {
                return DatabaseRes.DATABASE_CONNECTION_ERROR;
            }

            if (!usernameDatabase.equals(username)) {
                System.out.println(usernameDatabase + " " + username);
                closeConnection();
                results = DatabaseRes.DATABASE_ACCOUNT_NOT_FOUND;
                System.out.println("Accont not found");
                return DatabaseRes.DATABASE_ACCOUNT_NOT_FOUND;
            }

            if (!passwordDatabase.equals(password)) {
                closeConnection();
                results = DatabaseRes.DATABASE_ACCOUNT_PASSWORD_INCORRECT;
                ;
                System.out.println("Accont password is not correct");
                return DatabaseRes.DATABASE_ACCOUNT_PASSWORD_INCORRECT;

            }



            System.out.println("Accont succesfull");
            results = DatabaseRes.DATABASE_ACCOUNT_LOGIN_SUCCESSFUL;
            return DatabaseRes.DATABASE_ACCOUNT_LOGIN_SUCCESSFUL;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DatabaseRes SendItem(Item item) {
        try {

            ArrayList<byte[]> bytes = new ArrayList<>(4);
            bytes.add(new byte[0]);
            bytes.add(new byte[0]);
            bytes.add(new byte[0]);
            bytes.add(new byte[0]);
            ArrayList<Bitmap> image = item.getImage();
            int index = 0;
            for(Bitmap i : image)
            {
                Bitmap photo = i;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 100, bos);
                bytes.set(index,bos.toByteArray());
                index++;

            }







            String insert = "INSERT INTO items (ItemsName,ItemsComment,ItemsPrice,ItemsHighestBid,ItemsOwnerName,ItemsLocationAltitude,ItemsLocationLongtitude,ItemsLocationLatitude,ItemsLocationAddress,ItemsImage,ItemsImage1,ItemsImage2,ItemsImage3) values(?,?,?, ?,?, ?, ?,? ,?,?,?,?,?)";
            PreparedStatement prep = con.prepareStatement(insert);

            prep.setString(1, item.getItem_name());
            prep.setString(2, item.getItem_info());
            prep.setDouble(3, item.getPrice());
            prep.setDouble(4, 0);
            prep.setString(5, item.getOwnerName());
            prep.setDouble(6, item.getLocation().getAltitude());
            prep.setDouble(7, item.getLocation().getLongitude());
            prep.setDouble(8, item.getLocation().getLatitude());
            prep.setString(9, item.getAddress());


            prep.setBytes(10,bytes.get(0));
            prep.setBytes(11,bytes.get(1));
            prep.setBytes(12,bytes.get(2));
            prep.setBytes(13,bytes.get(3));
            prep.executeUpdate();
            results = DatabaseRes.DATABASE_ITEM_SENT;




        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    public void getResults()
    {
        try {

        String selectItems = "SELECT * from items;";



            Statement st = con.createStatement();

            ResultSet set = st.executeQuery(selectItems);
                while(set.next())
                {
                    ArrayList<Bitmap> images = new ArrayList<>();
                    String ItemsName = set.getNString("ItemsName");
                    String ItemsComment = set.getNString("ItemsComment");
                    Double ItemsPrice = set.getDouble("ItemsPrice");
                    Double ItemsHighestBid = set.getDouble("ItemsHighestBid");
                    String ItemsOwnerName = set.getNString("ItemsOwnerName");
                    double ItemsLocationAltitude = set.getDouble("ItemsLocationAltitude");
                    double ItemsLocationLongtitude = set.getDouble("ItemsLocationLongtitude");
                    double ItemsLocationLatitude = set.getDouble("ItemsLocationLatitude");
                    String ItemsLocationAddress = set.getNString("ItemsLocationAddress");

                    byte[] imgt = set.getBytes("ItemsImage");
                    byte[] img1 = set.getBytes("ItemsImage1");
                    byte[] img2 = set.getBytes("ItemsImage2");
                    byte[] img3 = set.getBytes("ItemsImage3");
                    int itemId = set.getInt("idItems");
                        System.out.println(imgt.length);


                   images.add(CreateBitmapFromDB(imgt));
                    images.add(CreateBitmapFromDB(img1));
                    images.add(CreateBitmapFromDB(img2));
                    images.add(CreateBitmapFromDB(img3));

                    Item item = new Item(ItemsOwnerName,ItemsLocationLongtitude,ItemsLocationAltitude,ItemsLocationLatitude,
                          ItemsName,ItemsComment,ItemsPrice,ItemsLocationAddress,images,itemId);
                    b.add(item);

                }
                results = DatabaseRes.DATABASE_REQUEST_ITEMS;


        } catch (SQLException e) {
            e.printStackTrace();
        }




    }

    private Bitmap CreateBitmapFromDB(byte[] imgt) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            return BitmapFactory.decodeByteArray(imgt, 0, imgt.length, options);


    }


    private void closeConnection()
    {   try {
        con.close();
    }catch (SQLException e)
    {
        e.printStackTrace();
    }
    }
    private boolean isConnectionClosed()
    {
        try {
            if (con.isClosed()) {
                return true;

            } else {
                return false;
            }
        }catch(SQLException e)
        {
            e.printStackTrace();
        }
        return false;

    }

    public void addOnLoginListenner(IDatabaseEvent l)
    {
            _listeners.add(l);
    }

    private void OnLogInSuccess(){

        String username = req.getUsername();
        Activity act = req.getWindow();
        for(IDatabaseEvent e : _listeners)
        {
            e.OnLoggedIn(new UsersInfo(username,act));
        }
    }

    private void RegisterSuccess(){

        for(IDatabaseEvent e : _listeners)
        {
            e.OnRegistered();
        }
    }

    private void OnLogInFail(DatabaseRes res){

        for(IDatabaseEvent e : _listeners)
        {
           e.OnFailLogIn(res);
        }
    }

    private void OnRequestItems(ArrayList<Item> b)
    {
        for(IDatabaseEvent e : _listeners)
        {
            e.OnRequestItems(b);
        }
    }
    private void OnPostItem()
    {
        for(IDatabaseEvent e : _listeners)
        {
            e.onPostItemSuccess();
        }
    }

    private void OnComments(ArrayList<itemOffers> a)
    {
        for(IDatabaseEvent e : _listeners)
        {
            e.onCommentsRequests(a);
        }
    }

}

