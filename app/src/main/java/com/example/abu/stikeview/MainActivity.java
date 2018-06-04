package com.example.abu.stikeview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    //Y移动最大值
    private static final float TOUCH_MOVE_MAX_Y=600;
    private float mTouchMoveStartY;
    private TouchPullView pullView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pullView=findViewById(R.id.touch_pull);

        findViewById(R.id.activity_main).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //得到意图
                int action=motionEvent.getActionMasked();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        mTouchMoveStartY=motionEvent.getY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float y=motionEvent.getY();
                        if (y>=mTouchMoveStartY){
                            float moveSize=y-mTouchMoveStartY;
                            float progress=moveSize>=TOUCH_MOVE_MAX_Y?1:moveSize/TOUCH_MOVE_MAX_Y;
                            pullView.setProgress(progress);
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        pullView.setProgress(0);
                        return true;
                        default:

                            break;
                }
                return false;
            }
        });
    }

}
