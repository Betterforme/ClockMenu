package com.example.sj.mytclockview;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import view.CircleMenuView;
import view.ItemBean;

public class MainActivity extends Activity implements CircleMenuView.StopPositionItem{

    private CircleMenuView circleMenuView;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        circleMenuView = (CircleMenuView) this.findViewById(R.id.view);
        circleMenuView.setItemChooseListner(this);
        textView = (TextView) this.findViewById(R.id.tv_type);
    }

    @Override
    public void ChooseItem(ItemBean ib) {
        textView.setText(ib.getHw_item());
    }
}
