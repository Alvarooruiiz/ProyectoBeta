package com.example.proyectobeta.Carousel;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.proyectobeta.R;
import com.example.proyectobeta.Usuario.UsuarioProvider;

import java.util.ArrayList;
import java.util.List;

public class Carousel extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;
    private ArrayList<Bitmap> imageBitmaps;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carousel_style_layout);

        userId = getIntent().getIntExtra("userId", -1);

        imageBitmaps = obtenerListaDeBitmapsDeImagenes(userId);


        viewPager = (ViewPager) findViewById(R.id.viewPager);

        sliderDotspanel = (LinearLayout) findViewById(R.id.SliderDots);


        CarouselViewPagerAdapter viewPagerAdapter = new CarouselViewPagerAdapter(this,imageBitmaps);

        viewPager.setAdapter(viewPagerAdapter);

        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        for(int i = 0; i < dotscount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                for(int i = 0; i < dotscount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private ArrayList<Bitmap> obtenerListaDeBitmapsDeImagenes(int userId) {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        Cursor cursor = getContentResolver().query(
                UsuarioProvider.CONTENT_URI_IMAGENES,
                new String[]{UsuarioProvider.Imagenes.COL_IMAGE_URL},
                UsuarioProvider.Imagenes.COL_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") byte[] imageData = cursor.getBlob(cursor.getColumnIndex(UsuarioProvider.Imagenes.COL_IMAGE_URL));
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                bitmaps.add(bitmap);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return bitmaps;
    }

}
