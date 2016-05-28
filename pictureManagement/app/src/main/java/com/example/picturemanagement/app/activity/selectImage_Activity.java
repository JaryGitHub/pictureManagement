package com.example.picturemanagement.app.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import com.example.picturemanagement.app.R;
import com.example.picturemanagement.app.adapter.gridviewAdapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class selectImage_Activity extends AppCompatActivity {

    private ArrayList<String> arrayList;//获得所有图片的URL
    private ArrayList<String> array;//对于特定时间点选择的图片的URL
    private GridView grid;
    private TextView text;
    private String getenddate;
    private String getstartdate;
    private String finalenddate;
    private String finalstartdate;
    private int startpostion = Integer.MAX_VALUE;
    private int endpostion = Integer.MAX_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//设置不需要标题
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image_main);
        array = new ArrayList<String>();
        getstartdate = getIntent().getExtras().getString("getstartdate").trim();//获得从calendar中传来的字符串和容器
        getenddate = getIntent().getExtras().getString("getenddate").trim();
        arrayList = getIntent().getStringArrayListExtra("array");
        finalstartdate = getMax(getstartdate,getenddate);
        finalenddate = getMin(getstartdate,getenddate);
        new task(this).execute("String");
    }

    public void initdata()
    {
        for(int i = 0;i < arrayList.size();i++)
        {
            Date date = new Date(new File(arrayList.get(i)).lastModified());
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            if(df.format(date).equals(finalstartdate))
            {
                startpostion = i;
            }
            if(df.format(date).equals(finalenddate))
            {
                endpostion = i;
            }
        }
        if(startpostion == Integer.MAX_VALUE && endpostion != Integer.MAX_VALUE)
        {
            startpostion = 0;
        }
        if(startpostion != Integer.MAX_VALUE && endpostion == Integer.MAX_VALUE)
        {
            endpostion = arrayList.size() - 1;
        }
        if(startpostion != Integer.MAX_VALUE && endpostion != Integer.MAX_VALUE)
        {
            for(int i = startpostion;i <= endpostion;i++)
            {
                array.add(arrayList.get(i));
            }
        }
    }

    public class task extends AsyncTask<String,Integer,Integer>
    {
        private Context context;
        public ProgressDialog pd;
        public task(Context c)
        {
            context = c;
        }

        protected void onPreExecute()
        {
            pd = ProgressDialog.show(context,"initialization","Searching",true);
        }

        protected void onPostExecute(Integer result)
        {
            gridviewAdapter my = new gridviewAdapter(selectImage_Activity.this,array);
            grid = (GridView)findViewById(R.id.gridview);
            text = (TextView)findViewById(R.id.textview);
            grid.setAdapter(my);
            grid.setOnItemClickListener(new OnItemClick(selectImage_Activity.this));
            text.setText(String.valueOf(array.size()) + "张照片");
            pd.cancel();
        }

        @Override
        protected Integer doInBackground(String... params) {
            initdata();
            return 1;
        }
    }

    /**
     * 设置监听按钮进行跳转到GalleryActivity
     */
    public class OnItemClick implements AdapterView.OnItemClickListener
    {
        private Context context;
        public OnItemClick(Context con)
        {
            this.context = con;
        }
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent();
            intent.setClass(selectImage_Activity.this, gallery_Activity.class);
            intent.putExtra("position", position);
            intent.putStringArrayListExtra("array",array);
            selectImage_Activity.this.startActivity(intent);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_image_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String getMax(String getstartdate,String getenddate)
    {
        int n = Math.max(Integer.parseInt(getstartdate), Integer.parseInt(getenddate));
        return String.valueOf(n);
    }

    private String getMin(String getstartdate,String getenddate)
    {
        int n = Math.min(Integer.parseInt(getstartdate), Integer.parseInt(getenddate));
        return String.valueOf(n);
    }
}
