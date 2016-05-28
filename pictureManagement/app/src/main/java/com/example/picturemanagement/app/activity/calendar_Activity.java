package com.example.picturemanagement.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.picturemanagement.app.R;
import com.example.picturemanagement.app.widget.calendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class calendar_Activity extends Activity {

    private calendarView calendar;//日历控件
    private ImageButton calendarLeft;//向左移动月份的按钮
    private TextView calendarCenter;
    private ImageButton calendarRight;//向右移动月份的按钮
    private SimpleDateFormat format;
    private ArrayList<String> array;//获得所有图片的URL
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar__main);
        array = getIntent().getStringArrayListExtra("array");
        format = new SimpleDateFormat("yyyy-MM-dd");
        //获取日历控件对象
        calendar = (calendarView)findViewById(R.id.calendar);
        calendar.setSelectMore(false); //单选

        calendarLeft = (ImageButton)findViewById(R.id.calendarLeft);
        calendarCenter = (TextView)findViewById(R.id.calendarCenter);
        calendarRight = (ImageButton)findViewById(R.id.calendarRight);
        try {
            //设置日历日期
            Date date = format.parse("2015-01-01");
            calendar.setCalendarData(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //获取日历中年月 ya[0]为年，ya[1]为月
        String[] ya = calendar.getYearAndmonth().split("-");
        calendarCenter.setText(ya[0]+"年"+ya[1]+"月");
        calendarLeft.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //点击上一月 同样返回年月
                String leftYearAndmonth = calendar.clickLeftMonth();
                String[] ya = leftYearAndmonth.split("-");
                calendarCenter.setText(ya[0]+"年"+ya[1]+"月");
            }
        });

        calendarRight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //点击下一月
                String rightYearAndmonth = calendar.clickRightMonth();
                String[] ya = rightYearAndmonth.split("-");
                calendarCenter.setText(ya[0]+"年"+ya[1]+"月");
            }
        });

        //设置控件监听，可以监听到点击的每一天
        calendar.setOnItemClickListener(new calendarView.OnItemClickListener() {
            @Override
            public void OnItemClick(Date selectedStartDate,
                                    Date selectedEndDate, Date downDate) {
                calendar.setSelectMore(true);//设置为多选
                if(calendar.isSelectMore()) {
                    Intent intent = new Intent();
                    intent.setClass(calendar_Activity.this, selectImage_Activity.class);
                    intent.putExtra("getstartdate", change(format.format(selectedStartDate)));//获得选定的开始时间
                    intent.putExtra("getenddate", change(format.format(selectedEndDate)));//获得选定的结束时间
                    intent.putStringArrayListExtra("array", array);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 把时间的格式从例如2015:08:08转化为20150808
     * @param s
     * @return
     */
    public String change(String s)
    {
        String time = s.substring(0,4)+s.substring(5,7)+s.substring(8,10);
        return time;
    }
}
