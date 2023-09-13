package com.example.p7;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.shape.ShapeAppearanceModel;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;

public class TestSegment extends ConstraintLayout {
    private AttributeSet set;
    private MaterialButton[] btnArr ;
    private TextView[] textArr ;
    private Context context;
    private MaterialCardView cardView;
    private int currentIndex = 0;

    private int selectTextColor;

    private int unSelectTextColor;

    private int selectItemColor;

    private int unSelectItemColor;

    private float textSize = 35f;

    private OnIndexChangeListener indexChangeListener;

//    final String xmlns="http://schemas.android.com/apk/res/android";
    final String app="http://schemas.android.com/apk/res-auto";
    public TestSegment(@NonNull Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public TestSegment(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        set = attrs;
        this.context = context;
        initView();
    }

    public TestSegment(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        set = attrs;
        this.context = context;
        initView();
    }



    private void initView(){
        if (set == null) {
            context.getTheme().obtainStyledAttributes(set, R.styleable.TestSegment, 0, 0);
        }
        int n = set.getAttributeIntValue(app, "segNum", 2);
        int[] idArr = new int[n];
        btnArr = new MaterialButton[n];
        textArr = new TextView[n];
        TypedArray a = context.obtainStyledAttributes(set,R.styleable.TestSegment);
        selectItemColor = a.getColor(R.styleable.TestSegment_selectItemColor, Color.WHITE);
        unSelectItemColor = a.getColor(R.styleable.TestSegment_unSelectItemColor, Color.parseColor("#FF3700B3"));
        selectTextColor = a.getColor(R.styleable.TestSegment_selectTextColor,Color.parseColor("#FF3700B3") );
        unSelectTextColor = a.getColor(R.styleable.TestSegment_unSelectTextColor, Color.WHITE);
        a.recycle();
        cardView = new MaterialCardView(context);
        cardView.setRadius(20f);
        cardView.setStrokeWidth(0);
        cardView.setId(MaterialCardView.generateViewId());
        cardView.setCardBackgroundColor(selectItemColor);
        ConstraintSet set = new ConstraintSet();
        set.clone(this);
        for(int i = 0 ; i < n; i++) {

            MaterialButton button = new MaterialButton(context, null ,com.google.android.material.R.style.Widget_Material3_Button_TextButton);
            button.setId(MaterialButton.generateViewId());
            button.setCornerRadius(0);
            button.setBackgroundColor(unSelectItemColor);
            if (i == 0) {
                ShapeAppearanceModel model = ShapeAppearanceModel.builder()
                        .setBottomLeftCornerSize(20f)
                        .setTopLeftCornerSize(20f)
                        .build();
                button.setShapeAppearanceModel(model);

            }else if (i == n-1) {
                ShapeAppearanceModel model = ShapeAppearanceModel.builder()
                        .setBottomRightCornerSize(20f)
                        .setTopRightCornerSize(20f)
                        .build();
                button.setShapeAppearanceModel(model);
            }

            button.setOnClickListener(v-> {
                ViewPropertyAnimator animate = cardView.animate();
                int m = findIndex(idArr, v.getId());
                if (m != currentIndex) {
                    float moveX = v.getMeasuredWidth()*m;
                    animate.translationX(moveX);
                    animate.setInterpolator(new OvershootInterpolator());
                    animate.setDuration(500);
                    animate.start();
                    textArr[m].setTextColor(selectTextColor);
                    textArr[currentIndex].setTextColor(unSelectTextColor);
                    currentIndex = m;

                    if (indexChangeListener != null){
                        indexChangeListener.onChange(currentIndex);
                    }
                }
            });

            addView(button);
            idArr[i] = button.getId();
            btnArr[i] = button;

        }
        addView(cardView);
        set.createHorizontalChain(
                ConstraintSet.PARENT_ID,
                ConstraintSet.LEFT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.RIGHT,
                idArr,
                null,
                ConstraintSet.CHAIN_SPREAD
            );

        for(int i = 0 ; i < n; i++) {
            set.connect(btnArr[i].getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            set.connect(btnArr[i].getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID,  ConstraintSet.BOTTOM);
            set.constrainWidth(btnArr[i].getId(), ConstraintSet.MATCH_CONSTRAINT_SPREAD);
            set.constrainHeight(btnArr[i].getId(), ConstraintSet.MATCH_CONSTRAINT);

            TextView textView = new TextView(context);
            textView.setId(TextView.generateViewId());
            textView.setText(i+"");
            textView.setTextSize(textSize);
            if (i == currentIndex)
                textView.setTextColor(selectTextColor);
            else
                textView.setTextColor(unSelectTextColor);
            textView.setGravity(Gravity.CENTER);
            addView(textView);
            textArr[i] = textView;
            set.connect(textView.getId(), ConstraintSet.TOP, btnArr[i].getId(), ConstraintSet.TOP, 10);
            set.connect(textView.getId(), ConstraintSet.BOTTOM, btnArr[i].getId(),  ConstraintSet.BOTTOM, 10);
            set.connect(textView.getId(), ConstraintSet.START, btnArr[i].getId(), ConstraintSet.START, 10);
            set.connect(textView.getId(), ConstraintSet.END, btnArr[i].getId(),  ConstraintSet.END, 10);
            set.constrainWidth(textView.getId(), ConstraintSet.MATCH_CONSTRAINT);
            set.constrainHeight(textView.getId(), ConstraintSet.MATCH_CONSTRAINT);

        }

        set.connect(cardView.getId(), ConstraintSet.TOP, btnArr[0].getId(), ConstraintSet.TOP, 25);
        set.connect(cardView.getId(), ConstraintSet.BOTTOM, btnArr[0].getId(),  ConstraintSet.BOTTOM, 25);
        set.connect(cardView.getId(), ConstraintSet.START, btnArr[0].getId(), ConstraintSet.START, 10);
        set.connect(cardView.getId(), ConstraintSet.END, btnArr[0].getId(),  ConstraintSet.END, 10);
        set.constrainWidth(cardView.getId(), ConstraintSet.MATCH_CONSTRAINT);
        set.constrainHeight(cardView.getId(), ConstraintSet.MATCH_CONSTRAINT);
        set.applyTo(this);

    }


    public void setIndexChangeListener(OnIndexChangeListener indexChangeListener){
        this.indexChangeListener = indexChangeListener;
    }

    public void setSelectItemColor(int selectItemColor) {
        this.selectItemColor = selectItemColor;
        refreshView();
    }

    public void setSelectTextColor(int selectTextColor) {
        this.selectTextColor = selectTextColor;
        refreshView();
    }

    public void setUnSelectItemColor(int unSelectItemColor) {
        this.unSelectItemColor = unSelectItemColor;
        refreshView();
    }

    public void setUnSelectTextColor(int unSelectTextColor) {
        this.unSelectTextColor = unSelectTextColor;
        refreshView();
    }

    public void setCurrentIndex(int currentIndex) {
        btnArr[currentIndex].callOnClick();
        refreshView();
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        refreshView();
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    private int findIndex(int[] arr, int target){
        for (int n = 0;n< arr.length;n++){
            if(arr[n] == target)
                return n;
        }
        return -1;
    }

    private void refreshView(){
        cardView.setCardBackgroundColor(selectItemColor);
        for(int i = 0; i< btnArr.length; i++){
            btnArr[i].setBackgroundColor(unSelectItemColor);
            if (i == currentIndex)
                textArr[i].setTextColor(selectTextColor);
            else
                textArr[i].setTextColor(unSelectTextColor);
            textArr[i].setTextSize(textSize);
        }
    }

}
