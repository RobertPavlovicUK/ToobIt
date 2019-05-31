package cw.coursework;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.internal.ParcelableSparseArray;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import cw.coursework.Database.DatabaseAsyncTask;
import cw.coursework.Database.DatabaseRequest;
import cw.coursework.Database.DatabaseRes;
import cw.coursework.Database.IDatabaseEvent;

public class ItemsDetailsActivity extends AppCompatActivity implements IDatabaseEvent, OnMapReadyCallback {
    ViewPager viewPager;
   public CustomSwipeAdapter adapter;
    private TextView description;
    private TextView itemName;
    private TextView priceName;
    private MapView mapview;
    private GoogleMap map;
    private TextView offerPrice;
    private TextView comment;
    private Button b;

    private LinearLayout container;
    private LinearLayout textCont;
    private TextView commentBox;
    private TextView ammoutBox;
    final Item i= ScrollingActivity.clickedArray;
    private boolean isOwner = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_items_details );
        viewPager = findViewById( R.id.view_pager );
        mapview = findViewById(R.id.mapView);
        mapview.onCreate(savedInstanceState);
        mapview.getMapAsync(this);

        container = findViewById(R.id.commentCont);
        adapter = new CustomSwipeAdapter( this );

            Debug.printToScreen(""+i.getItemID());
        viewPager.setAdapter( adapter );

        description = findViewById(R.id.description);
        itemName = findViewById(R.id.name);
        priceName = findViewById(R.id.price);
        offerPrice = findViewById(R.id.priceBox);
        comment = findViewById(R.id.commentBox);
        b = findViewById(R.id.makeoffer);

        new DatabaseAsyncTask().execute(
                new DatabaseRequest(DatabaseRes.DATABASE_RECEIVE_COMMENTS,i, ScrollingActivity.usersInfo,null,ItemsDetailsActivity.this));
        description.setText(i.getItem_info());
        itemName.setText(itemName.getText()+" "+i.getItem_name());

        priceName.setText(priceName.getText()+" "+"£"+i.getPrice());
            checkOwnership();
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkInput())
                {
                    itemOffers offer = new itemOffers(0,i.getItemID(),comment.getText().toString(),Double.parseDouble(offerPrice.getText().toString()),ScrollingActivity.usersInfo.getUsername(),0);
                    new DatabaseAsyncTask().execute(
                            new DatabaseRequest(DatabaseRes.DATABASE_MAKE_AN_OFFER,i, ScrollingActivity.usersInfo,offer,ItemsDetailsActivity.this));
                    finish();
                    Intent intent = new Intent(ItemsDetailsActivity.this, ItemsDetailsActivity.class);



                    ItemsDetailsActivity.this.startActivity(intent);

                }

            }
        });

    }

    private void checkOwnership() {

        if(ScrollingActivity.usersInfo == null || i.getOwnerName().equals(ScrollingActivity.usersInfo.getUsername()) || !ScrollingActivity.loggedIn)
        {   isOwner = false;
        Debug.printToScreen("IS NOT MY ITEM");
            comment.setVisibility(View.INVISIBLE);
            offerPrice.setVisibility(View.INVISIBLE);
        }
        if( ScrollingActivity.usersInfo != null && i.getOwnerName().equals(ScrollingActivity.usersInfo.getUsername()) && ScrollingActivity.loggedIn)
        {
            isOwner= true;
        }

    }

    private boolean checkInput() {

        if(offerPrice.getText().length() <= 0)
        {
            offerPrice.setError("Required field");
            offerPrice.requestFocus();
            if(isOwner)
            {
                Toast.makeText(this,"Can not make offer to your own item",Toast.LENGTH_LONG).show();
            }
            return false;
        }

        return true;
    }

    @Override
    public void OnLoggedIn(UsersInfo info) {

    }

    @Override
    public void OnRegistered() {



    }

    @SuppressLint("SetTextI18n")
    private void CreateComments(itemOffers o) {
        commentBox = new TextView(this);
        ammoutBox = new TextView(this);
        ImageButton accept = new ImageButton(this);
        textCont = new LinearLayout(this);
        textCont.addView(commentBox);
        textCont.addView(accept);
        textCont.setOrientation(LinearLayout.HORIZONTAL);
        accept.setVisibility(View.INVISIBLE);

        accept.setLayoutParams(new LinearLayout.LayoutParams(100,100));
        container.addView(textCont);
        String displayPrice = " has made an offer";
        String comment = o.getComment();
      //  BitmapDrawable bdrawable = new BitmapDrawable(this.getResources(),R.mipmap.Accept);
        accept.setBackgroundResource(R.mipmap.accept);
        if(isOwner)
        {
            displayPrice = "£"+o.getOfferAmmount();
            accept.setVisibility(View.VISIBLE);

        }
        commentBox.setText(o.getUsername()+" : "+o.getComment()+" | "+displayPrice);




    }

    @Override
    public void OnFailLogIn(DatabaseRes res) {

    }

    @Override
    public void onPostItemSuccess() {

    }

    @Override
    public void onCommentsRequests(ArrayList<itemOffers> comments) {
        Debug.printToScreen("RECEIVED COMMENTS"+ comments.size());
        for (itemOffers o : comments) {
            CreateComments(o);
        }
    }

    @Override
    public void OnRequestItems(ArrayList<Item> images) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println("From map");
        map = googleMap;

        LatLng loc = new LatLng(i.getLat(), i.getLongt());

            String []address = i.getAddress().split(",");
        map.addMarker(new MarkerOptions().position(loc).title(address[1])).showInfoWindow();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 13));



    }

    @Override
    protected void onResume() {
        mapview.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mapview.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapview.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mapview.onLowMemory();
        super.onLowMemory();
    }
}
