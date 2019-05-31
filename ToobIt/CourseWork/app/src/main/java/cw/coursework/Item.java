package cw.coursework;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Parcelable;
import android.support.design.internal.ParcelableSparseArray;

import java.util.ArrayList;

/**
 * Created by pavlo on 14/03/2018.
 */

public class Item {

    private String owner_Name;
    private Location location;
    private String item_name;
    private String item_info;
    private double price;
    private double highestOffer;
    private String address;
    private ArrayList<Bitmap> image ;
    private double longt,alt,lat;
    private int itemID;

    public Item()
    {}

    public Item(String owner_Name,Location l, String item_name,String item_info,double price,String address, ArrayList<Bitmap> image,int itemID)
    {
        this.owner_Name=owner_Name;
        this.location = l;
        this.item_name = item_name;
        this.item_info = item_info;
        this.price = price;
        this.address = address;
        this.image = image;
        this.itemID = itemID;

    }
    public Item(String owner_Name,double longt, double alt, double lat, String item_name,String item_info,double price,String address, ArrayList<Bitmap> image,int itemID)
    {
        this.itemID = itemID;
        this.owner_Name=owner_Name;

        this.item_name = item_name;
        this.item_info = item_info;
        this.price = price;
        this.address = address;
        this.image=image;
        this.longt = longt;
        this.alt = alt;
        this.lat=lat;

    }
    public void AddImage(Bitmap b)
    {
        image.add(b);
    }
    public String getOwnerName()
    {
        return owner_Name;
    }

    public Location getLocation() {
        return location;
    }

    public String getItem_name() {
        return item_name;
    }

    public String getItem_info() {
        return item_info;
    }

    public double getPrice() {
        return price;
    }

    public double getHighestOffer() {
        return highestOffer;
    }
    public double getLongt() {
        return longt;
    }
    public double getAlt() {
        return alt;
    }
    public double getLat() {
        return lat;
    }

    public String getAddress() {
        return address;
    }

    public ArrayList<Bitmap> getImage(){return image;}
    public int getItemID(){return itemID;}
}
