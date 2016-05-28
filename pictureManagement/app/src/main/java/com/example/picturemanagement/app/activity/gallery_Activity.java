package com.example.picturemanagement.app.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import com.example.picturemanagement.app.R;
import com.example.picturemanagement.app.adapter.imageAdapter;
import com.example.picturemanagement.app.widget.galleryView;

import java.util.ArrayList;

public class gallery_Activity extends AppCompatActivity {

    private int number;
    private ArrayList<String> S;
    private imageAdapter ia;
    private  int width;
    private  int height;
    private View vi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery__main);
        galleryView g = (galleryView) findViewById(R.id.ga);
        number = getIntent().getExtras().getInt("position");
        S = getIntent().getStringArrayListExtra("array");
        ia = new imageAdapter(this,S);
        g.setAdapter(ia);
        g.setOnItemSelectedListener(new onItemSelectedListener());
        g.setSelection(number);
        g.setOnItemClickListener(new onItemClickListener(this));
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels ;
    }


    public class onItemClickListener implements AdapterView.OnItemClickListener
    {
        private Context context;
        public onItemClickListener(Context con)
        {
            this.context = con;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ImageView image = (ImageView)view;
            Matrix max = new Matrix();
            max.setRotate(90);
            Bitmap bm =((BitmapDrawable) ((ImageView) image).getDrawable()).getBitmap();
            Bitmap map = bm.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), max, true);
            image.setImageBitmap(map);
        }
    }


    public class onItemSelectedListener implements AdapterView.OnItemSelectedListener
    {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            vi = view;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }


    public boolean onTouchEvent(MotionEvent event)
    {
        ImageView image = (ImageView)vi;
        if(event.getAction() == MotionEvent.ACTION_MOVE && event.getPointerCount() == 2)
        {
            float n = event.getX(0);
            float m = event.getY(0);
            float n_1 = event.getX(1);
            float m_1 = event.getY(1);
            float num = math(n, m, n_1, m_1);
            Matrix max = new Matrix();
            Bitmap bm =((BitmapDrawable) ((ImageView) image).getDrawable()).getBitmap();
            float w = bm.getWidth();
            float dx = (num + w)/w;
            max.postScale(dx,dx);
            Bitmap map = bm.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), max, true);
            if(map.getWidth() <= width && map.getHeight() <= height)
            {
                image.setImageBitmap(map);
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gallery__main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public float math(float x1,float y1,float x2,float y2)
    {
        float num = (float) (Math.pow(Math.abs(x2 - x1),2) + Math.pow(Math.abs(y2 - y1),2));
        return (float)Math.sqrt(num);
    }
}
