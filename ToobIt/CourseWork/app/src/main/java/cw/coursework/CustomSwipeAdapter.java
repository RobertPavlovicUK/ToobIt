package cw.coursework;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cw.coursework.R;

/**
 * Created by Werokk on 26/03/2018.
 */

public class CustomSwipeAdapter extends PagerAdapter{
    private ArrayList<Bitmap> image_resources;
    private Context ctx;
    private LayoutInflater layoutInflater;
    public CustomSwipeAdapter(Context ctx){
        Debug.printToScreen("Created Addapter");
        this.ctx =ctx;
        this.image_resources = ScrollingActivity.clickedArray.getImage();
    }
    @Override
    public int getCount() {
        int count = 0;
        for(Bitmap i : image_resources)
        {
           if(i != null)
           {
               count++;
           }
        }
        return count;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return (view ==o);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position){


            layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View item_view = layoutInflater.inflate(R.layout.swipe_layout, container, false);
            ImageView imageView = (ImageView) item_view.findViewById(R.id.image_view);

        int nh = (int) ( image_resources.get(position).getHeight() * (512 /image_resources.get(position).getWidth()) );
        BitmapDrawable bdrawable = new BitmapDrawable(ctx.getResources(),image_resources.get(position));

        imageView.setLayoutParams(new LinearLayout.LayoutParams(500+250,nh+250));
        //Name.setImageBitmap(i.getImage().get(0));
            imageView.setBackground(bdrawable);


            container.addView(item_view);
            return item_view;


    }
    @Override
    public void destroyItem (ViewGroup container, int position, Object object){
        container.removeView( (ConstraintLayout)object );
    }

    public void setImageResources(ArrayList<Bitmap> b)
    {
        image_resources = b;
    }
}
