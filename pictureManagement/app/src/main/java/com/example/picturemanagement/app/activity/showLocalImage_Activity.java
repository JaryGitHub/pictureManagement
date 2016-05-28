package com.example.picturemanagement.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.picturemanagement.app.R;
import com.example.picturemanagement.app.adapter.gridviewAdapter;

import java.io.File;
import java.util.ArrayList;

public class showLocalImage_Activity extends AppCompatActivity {

    private GridView grid;
    private TextView text;
    private ArrayList<String> array;//保存选定文件夹中的图片
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_local_image_main);
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))//检测SD卡的存在性
        {
            String s = getIntent().getExtras().getString("address");//获得文件夹名字
            array = readlist(s);//获得所有图片的URL
            gridviewAdapter my = new gridviewAdapter(this,array);
            grid = (GridView)findViewById(R.id.gridview);
            text = (TextView)findViewById(R.id.textview);
            grid.setAdapter(my);//添加适配器
            grid.setOnItemClickListener(new OnItemClick(this));
            text.setText(String.valueOf(array.size())+"张照片");
        }
        else
        {
            Toast.makeText(showLocalImage_Activity.this, "对不起没有SD卡", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * 对griview中的每一个项设置监听，点击就会进入画廊模式
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
            intent.setClass(showLocalImage_Activity.this, gallery_Activity.class);
            intent.putExtra("position", position);
            intent.putStringArrayListExtra("array",array);
            showLocalImage_Activity.this.startActivity(intent);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_local_image_, menu);
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

    /**
     * 获取指定文件夹下所有的图片
     * @param path
     * @return
     */
    private ArrayList<String> readlist(String path)
    {
        ArrayList<String> list = new ArrayList<String>();
        File file = new File(path);
        File []files = file.listFiles();
        if(files != null)
        {
            for(File f : files)
            {
                String filename = f.getName();
                if(filename.lastIndexOf(".") > 0 && filename.substring(filename.lastIndexOf(".")+1,filename.length()).equals("JPG"))//jpg形式的照片
                {
                    list.add(f.getPath());
                }
                else if(filename.lastIndexOf(".") > 0 && filename.substring(filename.lastIndexOf(".")+1,filename.length()).equals("PNG"))//png形式的照片
                {
                    list.add(f.getPath());
                }
                else if(filename.lastIndexOf(".") > 0 && filename.substring(filename.lastIndexOf(".")+1,filename.length()).equals("BMP"))//png形式的照片
                {
                    list.add(f.getPath());
                }
                else if(filename.lastIndexOf(".") > 0 && filename.substring(filename.lastIndexOf(".")+1,filename.length()).equals("jpg"))//png形式的照片
                {
                    list.add(f.getPath());
                }
                else if(filename.lastIndexOf(".") > 0 && filename.substring(filename.lastIndexOf(".")+1,filename.length()).equals("png"))//png形式的照片
                {
                    list.add(f.getPath());
                }
                else if(filename.lastIndexOf(".") > 0 && filename.substring(filename.lastIndexOf(".")+1,filename.length()).equals("bmp"))//png形式的照片
                {
                    list.add(f.getPath());
                }
                else if(filename.lastIndexOf(".") > 0 && filename.substring(filename.lastIndexOf(".")+1,filename.length()).equals("jpeg"))//png形式的照片
                {
                    list.add(f.getPath());
                }
                else if(filename.lastIndexOf(".") > 0 && filename.substring(filename.lastIndexOf(".")+1,filename.length()).equals("JPEG"))//png形式的照片
                {
                    list.add(f.getPath());
                }
            }
        }
        return list;
    }
}
