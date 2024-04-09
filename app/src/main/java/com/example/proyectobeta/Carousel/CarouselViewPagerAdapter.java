package com.example.proyectobeta.Carousel;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.proyectobeta.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CarouselViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<Bitmap> imageBitmaps;

    public CarouselViewPagerAdapter(Context context, List<Bitmap> imageBitmaps) {
        this.context = context;
        this.imageBitmaps = imageBitmaps;
    }

    @Override
    public int getCount() {
        return imageBitmaps.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.carousel_view_layout, container, false);
        ImageView imageView = view.findViewById(R.id.imageView);

        imageView.setImageBitmap(imageBitmaps.get(position));

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}