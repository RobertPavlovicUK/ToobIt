package cw.coursework;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothClass;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.media.Image;
import android.os.Build;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import cw.coursework.Database.DatabaseAsyncTask;
import cw.coursework.Database.DatabaseRequest;
import cw.coursework.Database.DatabaseRes;
import cw.coursework.Database.IDatabaseEvent;

public class SellActivity extends AppCompatActivity implements IDatabaseEvent {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 123;
    private Button sellItem;
    private ImageButton firstImage;
    private ImageButton lastButton;
    private TextView description;
    private ArrayList<Bitmap> images = new ArrayList<>();
    private TextView itemName;
    private TextView itemprice;
    private TextView itemDesc;
    private UsersInfo info;
    private ProgressBar bar ;
    private ConstraintLayout top;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);
        LocationService.getInstance(this).askForLocationPermision();
        itemName = findViewById(R.id.itemID);
        itemprice = findViewById(R.id.priceID);
        itemDesc = findViewById(R.id.DesID);
        bar = findViewById(R.id.proggresssel);
        top = findViewById(R.id.topLayout);
            showProgress(false);
        sellItem = (Button)findViewById(R.id.sell_item);
       firstImage = findViewById(R.id.firstImage);
       LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(256, 256);
        param.setMargins(20,0,20,0);
      firstImage.setLayoutParams(param);

       firstImage.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               System.out.println("Pressed");
               TakePicture();
               lastButton = (ImageButton)v;
           }
       });
        sellItem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(true);
                if (checkInput()) {
                    new DatabaseAsyncTask().execute(new DatabaseRequest(SellActivity.this, DatabaseRes.DATABASE_SEND_ITEM, new Item(ScrollingActivity.usersInfo.getUsername(), LocationService.getInstance(SellActivity.this).getLoc(), itemName.getText().toString(), itemDesc.getText().toString(), Double.parseDouble(itemprice.getText().toString()), LocationService.getInstance(SellActivity.this).GetAddress(),images,0)));
                }
            }
        });


    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            top.setVisibility(show ? View.GONE : View.VISIBLE);
            top.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    top.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            bar.setVisibility(show ? View.VISIBLE : View.GONE);
            bar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    bar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            bar.setVisibility(show ? View.VISIBLE : View.GONE);
            top.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private boolean checkInput() {
        TextView textview = findViewById(R.id.itemID);

        if(TextUtils.isEmpty((textview = findViewById(R.id.itemID)).getText()))
        {
            textview.setError("Required Field");
            return false;

        }
        if(TextUtils.isEmpty((textview = findViewById(R.id.DesID)).getText()))
        {
            textview.setError("Required Field");
            return false;

        }
        if(TextUtils.isEmpty((textview = findViewById(R.id.priceID)).getText()))
        {
            textview.setError("Required Field");
            return false;

        }
        if(images.size() < 1)
        {
            System.out.println("Missing images");
            Snackbar.make(findViewById(R.id.sell_item),"Please take atlease 1 picture",5000).show();
            return false;

        }

        return true;

    }

    private void TakePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            images.add(imageBitmap);

            int nh = (int) ( imageBitmap.getHeight() * (512.0 / imageBitmap.getWidth()) );
            Bitmap scaled = Bitmap.createScaledBitmap(imageBitmap, 512, nh, true);
            lastButton.setAdjustViewBounds(true);
            BitmapDrawable bdrawable = new BitmapDrawable(this.getResources(),scaled);
            lastButton.setBackground(bdrawable);

            createButton();
        }
    }
    public void checkPermission() {

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        System.out.println("sad"+requestCode);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
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


    @SuppressLint("ResourceType")
    public void createButton()
    {
            ImageButton button = new ImageButton(this);

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(256, 256);
        param.setMargins(20,0,20,0);

        button.setLayoutParams(param);
        LinearLayout container = findViewById(R.id.linLay);

        container.addView(button);

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TakePicture();
                lastButton = (ImageButton)v;


            }

        });





    }

    @Override
    public void OnLoggedIn(UsersInfo info) {
        System.out.println("loggedIn");
        this.info = info;
    }

    @Override
    public void OnRegistered() {

    }

    @Override
    public void OnFailLogIn(DatabaseRes res) {

    }

    @Override
    public void onPostItemSuccess() {
            showProgress(false);
            this.finish();
    }

    @Override
    public void onCommentsRequests(ArrayList<itemOffers> comments) {

    }

    @Override
    public void OnRequestItems(ArrayList<Item> images) {

    }
}
