package cw.coursework;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;

import java.lang.reflect.Array;
import java.util.ArrayList;

import cw.coursework.Database.DatabaseAsyncTask;
import cw.coursework.Database.DatabaseRequest;
import cw.coursework.Database.DatabaseRes;
import cw.coursework.Database.IDatabaseEvent;

public class ScrollingActivity extends AppCompatActivity implements View.OnClickListener, IDatabaseEvent, View.OnTouchListener {
    ImageButton login_button;
    ImageButton sell_button;
    private String loggedInAs;
    private Location loc;
    private ArrayList<Item> recentItems;
    private ProgressBar mProgressView;
    private GridLayout cont;
    private ScrollView scroll;

    public static Item clickedArray;
    private float lastPosition =0;
    public static UsersInfo usersInfo;
    private boolean pressedDown= false;
    public  static boolean loggedIn = false;
    private boolean shouldOpen = true;
    private Profile profile;
    public  static ArrayList<Bitmap> image = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        LocationService.getInstance(this).askForLocationPermision();
        new DatabaseAsyncTask().execute(new DatabaseRequest(ScrollingActivity.this,DatabaseRes.DATABASE_REQUEST_ITEMS,null));
        profile = Profile.getCurrentProfile();

        login_button  = (ImageButton) findViewById(R.id.login_button);
        mProgressView = findViewById(R.id.login_progress2);
        cont = findViewById(R.id.containerGrid);
        sell_button = findViewById(R.id.sell_button);

        ProfileTracker fbProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
              System.out.println("Logged");
            }
        };


        if (profile != null) {
            new DatabaseAsyncTask().execute(
                    new DatabaseRequest(DatabaseRes.DATABASE_FACEBOOK_LOGIN, profile.getFirstName()+" "+profile.getLastName(),ScrollingActivity.this));
        }

        login_button.setOnClickListener(this);
        sell_button.setOnClickListener(this);
        scroll = findViewById(R.id.conLay);
        scroll.setOnTouchListener(ScrollingActivity.this);



        showProgress(true);



    }


    @Override
    public void onClick(View v) {


        if(v.getId() == R.id.login_button)
        {



                if(loggedIn)
                {
                    if(profile != null)
                    {
                        System.out.print("logout from FB");
                        LoginManager.getInstance().logOut();
                        login_button.setImageResource(R.mipmap.login);
                        loggedIn= false;
                        return;
                    }

                    login_button.setImageResource(R.mipmap.login);
                    loggedIn = false;
                    return;
                }
                Intent intent = new Intent(ScrollingActivity.this, LoginActivity.class);
                ScrollingActivity.this.startActivity(intent);



        }
        if(v.getId() == R.id.sell_button)
        {   System.out.println("hello");
            if(loggedIn) {
                Intent intent = new Intent(ScrollingActivity.this, SellActivity.class);
                ScrollingActivity.this.startActivity(intent);
            }else
            {
                System.out.println("Inside else");
                Toast.makeText(ScrollingActivity.this,"Please log in first",Toast.LENGTH_LONG).show();}
        }



    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            cont.setVisibility(show ? View.GONE : View.VISIBLE);
            cont.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    cont.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            cont.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        System.out.println("sad"+requestCode);
        switch (requestCode) {
            case 123: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    System.out.println("Granted");
                    LocationService.getInstance(this).getLastKnowLocation();



                } else {


                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void OnLoggedIn(UsersInfo info) {
        login_button.setImageResource(R.mipmap.logout);
        loggedIn= true;

        usersInfo = info;
        loggedInAs =info.getUsername();
        loc = info.getCurrentLocation();
        LocationService.getInstance(this).getLastKnowLocation();
        LocationService.getInstance(this).askForLocationPermision();
        System.out.println(""+loggedInAs);
        System.out.println(LocationService.getInstance(this).GetAddress());

    }

    @Override
    public void OnRegistered() {

    }

    @Override
    public void OnFailLogIn(DatabaseRes res) {

    }

    @Override
    public void onPostItemSuccess() {

    }

    @Override
    public void onCommentsRequests(ArrayList<itemOffers> comments) {

    }

    @Override
    public void OnRequestItems(ArrayList<Item> images) {
        System.out.println("Got called");
        cont.removeAllViews();
        for(Item i : images)
        {   System.out.println(i.getItem_name());
            CreateCardView(i);


        }
        showProgress(false);


    }

    public void CreateCardView(final Item i)

    {
        CardView.LayoutParams params = new CardView.LayoutParams(
                CardView.LayoutParams.WRAP_CONTENT,
                CardView.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(10,10,10,20);
        params.width = 500;

         cont = findViewById(R.id.containerGrid);
        CardView card = new CardView(this);
        card.setLayoutParams(params);
        card.setRadius(9);

        card.setCardElevation(9);
        LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        LinearLayout lin = new LinearLayout(this);
        lin.setOrientation(LinearLayout.VERTICAL);
        lin.setLayoutParams(par);

      final  ImageButton Name = new ImageButton(this);

        Name.setAdjustViewBounds(true);
        Name.setScaleType(ImageView.ScaleType.CENTER);
cont.bringToFront();
      //  Name.setBackground(bdrawable);
       Name.setAdjustViewBounds(true);

        int nh = (int) ( i.getImage().get(0).getHeight() * (512 /i.getImage().get(0).getWidth()) );
     //  Bitmap scaled = Bitmap.createScaledBitmap(i.getImage().get(0), 500, nh, true);

        BitmapDrawable bdrawable = new BitmapDrawable(this.getResources(),i.getImage().get(0));

        Name.setLayoutParams(new LinearLayout.LayoutParams(500,nh));
        //Name.setImageBitmap(i.getImage().get(0));
      Name.setBackground(bdrawable);
        lin.bringToFront();
        cont.addView(card);
        card.addView(lin);
      //  scroll.bringToFront();

        //Name.setImageBitmap( scaled);
        TextView productName = new TextView(this);
        productName.setTypeface(null, Typeface.BOLD);

        TextView price = new TextView(this);
        productName.setText(i.getItem_name());
        price.setText("£"+i.getPrice());
        LinearLayout.LayoutParams pa = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pa.setMargins(0,0,0,0);
        price.setTextColor(Color.parseColor("blue"));
        price.setGravity(Gravity.RIGHT);
        price.setLayoutParams(pa);
        lin.addView(Name);
        lin.addView(productName);
        lin.addView(price);
        Name.setOnTouchListener(this);

        final Item temp = i;
        card.setElevation(15);
        System.out.println("Hello?");

      Name.setOnClickListener(new View.OnClickListener() {
          @Override
        public void onClick(View v) {
              System.out.println("ButtonPressed");
              if (shouldOpen) {
                  Intent intent = new Intent(ScrollingActivity.this, ItemsDetailsActivity.class);
                  clickedArray = temp;


                  ScrollingActivity.this.startActivity(intent);
              }

           }
       });






    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {





        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            shouldOpen= true;
            lastPosition = event.getY();
           Debug.printToScreen("From down");

            }

    if (event.getAction() == MotionEvent.ACTION_MOVE) {
        Debug.printToScreen("From move");
            if(!pressedDown)
        {
                shouldOpen = false;
            lastPosition = event.getY();
            pressedDown= true;
        }

        if(v.getScrollY() == 0 && event.getY() - lastPosition >= 400 )
        {



            return true;


        }

        if (v.getScrollY() == 0 && lastPosition< event.getY() ) {

            if (event.getY() - lastPosition >= 400)
            {


        }


            return true;

        }

        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            pressedDown = false;
            Debug.printToScreen("Last pos: " +lastPosition);
            Debug.printToScreen("Current pos: "+event.getY());
            if(v.getScrollY() == 0 && event.getY() - lastPosition > 400)
            {

                Refresh();


            }

            lastPosition =0;
        }

        return false;
    }

    private void Refresh() {

        System.out.println(cont.getColumnCount() + "," + cont.getRowCount());

        showProgress(true);
        System.out.println("Refreshing");

        new DatabaseAsyncTask().execute(new DatabaseRequest(ScrollingActivity.this,DatabaseRes.DATABASE_REQUEST_ITEMS,null));


    }
}
