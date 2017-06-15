package com.lanqix.myflowlayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.TextView;

import com.lanqix.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FlowLayout f1;
    private FlowLayout f2;
    private FlowLayout f3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        List<String> list=new ArrayList<>();
        for (int i=0;i<20;i++){
            String text="adapter"+i;
            int round=i%5;
            if (i==4){
                round=3%5;
            }
            for (int j=0;j<round;j++){
                text+="-1";
            }
            list.add(text);
        }
        MyFlowAdapter adapter=new MyFlowAdapter(this,list);
        f1.setAdapter(adapter);
        f2.setAdapter(adapter);
        TextView textView;
        for (int i=0;i<20;i++){
            textView = new TextView(this);
            textView.setBackgroundResource(R.drawable.selector_radius);
            textView.setTextColor(getResources().getColorStateList(R.color.selector_c));
            textView.setGravity(Gravity.CENTER);
            textView.setText("addView"+i);
            textView.setClickable(true);
            f3.addView(textView);
        }
    }

    private void initView() {
        f1 = (FlowLayout) findViewById(R.id.f1);
        f2 = (FlowLayout) findViewById(R.id.f2);
        f3 = (FlowLayout) findViewById(R.id.f3);
    }
}
