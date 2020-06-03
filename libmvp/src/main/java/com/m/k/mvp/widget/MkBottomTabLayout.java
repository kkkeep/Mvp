package com.m.k.mvp.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.m.k.mvp.R;
import com.m.k.mvp.Utils.MkLogger;
import com.m.k.mvp.Utils.SystemFacade;

import java.util.ArrayList;
import java.util.List;

public class MkBottomTabLayout extends ConstraintLayout {

    private static final String TAG = "MvpBottomTabLayout";
    public static int mId = 1000;
    private TabAdapter mTabAdapter;

    private LayoutInflater mLayoutInflater;

    private Paint paint = new Paint();

    private ArrayList<Rect> mViewRectList;
    private View mPreSelectedTab;
    private long mClickTimeOut;
    private int downX, downY;
    private int mPreSelectedPosition = -1;
    private int mDividingLineColor;
    private int mDividingLineHeight;
    private int mDividingLineMarginToTap;
    private int mDividingLineMarginToTapIndex = -1;
    private int mMarginLeft, mMarginRight, mMarginTop, mMarginBottom;
    private int mBackgroundColor;
    private Drawable mBackgroundDrawable;
    private int mCurrentSelectedPosition;
    private boolean hasDividingLine;
    private boolean isSelected = true;


    public MkBottomTabLayout(Context context) {
        this(context, null);

    }

    public MkBottomTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public MkBottomTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MkBottomTabLayout);

        hasDividingLine = typedArray.getBoolean(R.styleable.MkBottomTabLayout_dividingLine, true);

        mDividingLineColor = typedArray.getColor(R.styleable.MkBottomTabLayout_dividingLineColor, Color.GRAY);

        mDividingLineHeight = (int) typedArray.getDimension(R.styleable.MkBottomTabLayout_dividingLineHeight, SystemFacade.dp2px(getContext(), 1));

        //mBackgroundColor = typedArray.getColor(R.styleable.MvpBottomTabLayout_layoutBackground, -2);
        mBackgroundDrawable = typedArray.getDrawable(R.styleable.MkBottomTabLayout_layoutBackground);
        mMarginLeft = (int) typedArray.getDimension(R.styleable.MkBottomTabLayout_fistTabMarginLeft, SystemFacade.dp2px(getContext(), 24));
        mMarginRight = (int) typedArray.getDimension(R.styleable.MkBottomTabLayout_lastTabMarginRight, SystemFacade.dp2px(getContext(), 24));
        mMarginTop = (int) typedArray.getDimension(R.styleable.MkBottomTabLayout_tabMarginTop, SystemFacade.dp2px(getContext(), 8));
        mMarginBottom = (int) typedArray.getDimension(R.styleable.MkBottomTabLayout_tabMarginBottom, SystemFacade.dp2px(getContext(), 8));
        mDividingLineMarginToTap = (int) typedArray.getDimension(R.styleable.MkBottomTabLayout_dividingLineMarginToTap, 0);
        if (typedArray.hasValue(R.styleable.MkBottomTabLayout_dividingLineMarginToTapIndex)) {
            mDividingLineMarginToTapIndex = (int) typedArray.getIndex(R.styleable.MkBottomTabLayout_dividingLineMarginToTapIndex);
        }


        init();
    }


    private int mMinHeightTabIndex;
    private int mMaxHeightTabIndex;


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        MkLogger.d("#childCount = %s  mMaxHeight = %s", getChildCount(), mMinHeightTabIndex);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        setBackground(null);
        MkLogger.d();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        MkLogger.d();
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        MkLogger.d();
    }

    private void init() {

        setWillNotDraw(!(hasDividingLine || mBackgroundDrawable != null));
        mLayoutInflater = LayoutInflater.from(getContext());
        mChildView = new ArrayList<>();

        mViewRectList = new ArrayList<>();

        paint.setColor(mDividingLineColor);
        paint.setStrokeWidth(mDividingLineHeight);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        setPadding(mMarginLeft, 0, mMarginRight, 0);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mViewRectList.clear();
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    mViewRectList.add(new Rect(child.getLeft(), child.getTop(), child.getRight(), child.getBottom()));

                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }


    public TabAdapter getAdapter() {
        return mTabAdapter;
    }

    public void selectTab(int position) {

        mCurrentSelectedPosition = position;

        if (mTabAdapter != null) {
            isSelected = true;
            if (mPreSelectedTab == getChildAt(position)) {
                mTabAdapter.onTabRepeatSelect(getChildAt(position), position);
                return;
            }
            mTabAdapter.onTabItemSelected(getChildAt(position), position);
            if (mPreSelectedTab != null) {
                mTabAdapter.onPreTabItemUnSelected(mPreSelectedTab, mPreSelectedPosition);
            }
            mPreSelectedPosition = position;
            mPreSelectedTab = getChildAt(position);
        } else {
            isSelected = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = (int) event.getX();
                downY = (int) event.getY();
                mClickTimeOut = System.currentTimeMillis();


                if (downY < getHeight()) {
                    for (int i = 0; i < mViewRectList.size(); i++) {
                        if (downY > mViewRectList.get(i).top - mMarginTop && downX > mViewRectList.get(i).left - 8 && downX < mViewRectList.get(i).right + 8) {
                            return true;
                        }
                    }

                }


                break;
            }


            case MotionEvent.ACTION_UP: {
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (System.currentTimeMillis() - mClickTimeOut <= 1000 && (Math.abs(x - downX) < getHeight() && Math.abs(y - downY) < getHeight())) {
                    for (int i = 0; i < mViewRectList.size(); i++) {
                        if (downY > mViewRectList.get(i).top - mMarginTop && downX > mViewRectList.get(i).left - 8 && downX < mViewRectList.get(i).right + 8) {
                            selectTab(i);
                            return true;
                        }
                    }
                }
                mClickTimeOut = 0;
                break;
            }
        }

        return false;
    }




    public void setAdapter(TabAdapter adapter) {
        MkLogger.d();
        mTabAdapter = adapter;
        mTabAdapter.setBottomTabLayout(this);
        makeAndAddView();
    }

    private List<View> mChildView;

    private int[] mIds;

    private void makeAndAddView() {

        removeAllViews();


        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this);


        mIds = new int[mTabAdapter.getTabItemCount()];
        for (int i = 0; i < mTabAdapter.getTabItemCount(); i++) {
            View child = mTabAdapter.createTabView(mLayoutInflater, this, i);
            child.setId(mId++);
            mIds[i] = child.getId();
            mChildView.add(child);
            mTabAdapter.bindTabView(child, i);

            addView(child);
        }

        // 计算 每一个子 View 的宽高
        int minHeight = SystemFacade.getScreenHeight(getContext());
        int maxHeight = 0;
        View view;
        for (int i = 0; i < getChildCount(); i++) {
            view = getChildAt(i);

            int hSpec = getChildMeasureSpec(MeasureSpec.makeMeasureSpec(SystemFacade.getScreenHeight(getContext()), MeasureSpec.UNSPECIFIED), 0, ViewGroup.LayoutParams.WRAP_CONTENT);
            int wSpec = getChildMeasureSpec(MeasureSpec.makeMeasureSpec(SystemFacade.getScreenWidth(getContext()), MeasureSpec.UNSPECIFIED), 0, ViewGroup.LayoutParams.WRAP_CONTENT);

            view.measure(wSpec, hSpec);

            if (view.getMeasuredHeight() < minHeight) {
                minHeight = view.getMeasuredHeight();
                mMinHeightTabIndex = i;
            }

            if (view.getMeasuredHeight() > maxHeight) {
                maxHeight = view.getMeasuredHeight();
                mMaxHeightTabIndex = i;
            }
        }

        if (mDividingLineMarginToTapIndex == -1) {
            mDividingLineMarginToTapIndex = mMinHeightTabIndex;
        }

        MkLogger.d(" mMaxHeight = %s", mMinHeightTabIndex);
        // 计算 每一个子 View 的宽高

        int previousId = 0;

        for (int i = 0; i < mChildView.size(); i++) {
            if (i == 0) {
                constraintSet.connect(mChildView.get(i).getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, mMarginLeft);
                constraintSet.connect(mChildView.get(i).getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, mMarginBottom);

            } else if (i == mChildView.size() - 1) {
                constraintSet.connect(mChildView.get(i).getId(), ConstraintSet.LEFT, previousId, ConstraintSet.LEFT);
                constraintSet.connect(mChildView.get(i).getId(), ConstraintSet.BOTTOM, previousId, ConstraintSet.BOTTOM);
            } else {
                constraintSet.connect(mChildView.get(i).getId(), ConstraintSet.LEFT, previousId, ConstraintSet.LEFT);
                constraintSet.connect(mChildView.get(i).getId(), ConstraintSet.BOTTOM, previousId, ConstraintSet.BOTTOM);
                constraintSet.connect(mChildView.get(i).getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, mMarginRight);
            }


            if (i == mMaxHeightTabIndex) {
                constraintSet.connect(mChildView.get(i).getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, mMarginTop);
            }
            constraintSet.constrainWidth(mChildView.get(i).getId(), ConstraintSet.WRAP_CONTENT);
            constraintSet.constrainHeight(mChildView.get(i).getId(), ConstraintSet.WRAP_CONTENT);
            previousId = mChildView.get(i).getId();

        }

        constraintSet.createHorizontalChain(getId(), ConstraintSet.LEFT, getId(), ConstraintSet.RIGHT, mIds, null, ConstraintSet.CHAIN_SPREAD_INSIDE);

        constraintSet.applyTo(this);


        if (!isSelected) {
            post(new Runnable() {
                @Override
                public void run() {
                    selectTab(mCurrentSelectedPosition);
                }
            });
        }

    }


    @Override
    protected void onDraw(Canvas canvas) {

        int y = 0;
        if (mDividingLineMarginToTap == 0) {
            y = Math.max(0, getChildAt(mDividingLineMarginToTapIndex).getTop() - mMarginTop);
        } else {
            y = Math.max(0, getChildAt(mDividingLineMarginToTapIndex).getTop() - mDividingLineMarginToTap);
        }

        if (hasDividingLine) {
            paint.setColor(mDividingLineColor);
            canvas.drawLine(0, y, getWidth(), y, paint);
        }

    /*    if(mBackgroundColor != -2){
            paint.setColor(mBackgroundColor);
            canvas.drawRect(new Rect(0, y + mDividingLineHeight, getWidth(), getHeight()), paint);
        }
*/
        if (mBackgroundDrawable != null) {
            if (mBackgroundDrawable instanceof ColorDrawable || mBackgroundDrawable instanceof ShapeDrawable) {
                mBackgroundDrawable.setBounds(new Rect(0, y + mDividingLineHeight, getWidth(), getHeight()));

            } else {
                mBackgroundDrawable.setBounds(new Rect(0, 0, getWidth(), getHeight()));
            }
            mBackgroundDrawable.draw(canvas);
        }


        //super.onDraw(canvas);
        MkLogger.d();

    }

    public static abstract class TabAdapter {


        protected MkBottomTabLayout bottomTabLayout;


        public void setBottomTabLayout(MkBottomTabLayout bottomTabLayout) {
            this.bottomTabLayout = bottomTabLayout;
        }

        protected abstract int getTabItemCount();


        protected abstract View createTabView(LayoutInflater layoutInflater, ViewGroup viewGroup, int position);


        protected abstract void bindTabView(View view, int position);


        public void notifyTabChanged(int position) {

            bindTabView(bottomTabLayout.getChildAt(position), position);
        }

        protected abstract void onTabItemSelected(View tab, int position);

        protected abstract void onPreTabItemUnSelected(View tab, int position);

        protected abstract void onTabRepeatSelect(View tab, int position);

    }


    public static class ClassicalTabAdapter extends  TabAdapter{


        private String []mTabNames;
        private int []  mIcons;

        private int mTitleNormalColor;
        private int mTitleSelectedColor;

        private int count;
        private int margin;
        private int textSize;


        public ClassicalTabAdapter(int count, int [] icons, String [] tabNames) {
            this.count = count;
            if(icons.length != tabNames.length || icons.length != count){
                throw new IllegalArgumentException("icons 和 tabNames 个数必须一致");
            }
            mTabNames = tabNames;
            mIcons = icons;
        }




        public ClassicalTabAdapter setTabTextSize(int size){
            this.textSize = size;
            return this;
        }
        public ClassicalTabAdapter setTabTextColor(@ColorInt int normalColor,  @ColorInt int selectedColor){
            mTitleNormalColor = normalColor;
            mTitleSelectedColor = selectedColor;
            return this;
        }

        public ClassicalTabAdapter setTabTextColor(@ColorInt int normalColor){
            mTitleNormalColor = normalColor;
            mTitleSelectedColor = normalColor;
            return this;
        }
        public ClassicalTabAdapter setIconToTitleMargin(int iconToTitleMargin){
            margin = iconToTitleMargin;
            return this;
        }
        @Override
        protected int getTabItemCount() {
            return count;
        }
        @Override
        protected View createTabView(LayoutInflater layoutInflater, ViewGroup parent,int position) {
            return layoutInflater.inflate(R.layout.item_classic_bottom_tab,parent,false);
        }


        @Override
        protected void bindTabView(View tab,int position) {
            TextView textView = tab.findViewById(R.id.mvp_bottom_tab_value);
            CheckBox icon = tab.findViewById(R.id.mvp_bottom_tab_icon);
            icon.setBackgroundResource(mIcons[position]);
            icon.setButtonDrawable(new StateListDrawable());
            textView.setText(mTabNames[position]);
            textView.setTextColor(mTitleNormalColor == 0 ? Color.BLACK : mTitleNormalColor);
            if(textSize != 0){
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
            }

            if(margin != 0){
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(((ConstraintLayout)tab));
                constraintSet.connect(textView.getId(),ConstraintSet.TOP,icon.getId(),ConstraintSet.BOTTOM,margin);
                constraintSet.applyTo(((ConstraintLayout)tab));
            }




        }

        @Override
        protected void onTabItemSelected(View tab, int position) {
            TextView textView = tab.findViewById(R.id.mvp_bottom_tab_value);
            CheckBox icon = tab.findViewById(R.id.mvp_bottom_tab_icon);
            icon.setChecked(true);
            textView.setTextColor(mTitleSelectedColor == 0 ? Color.BLACK : mTitleSelectedColor);
        }

        @Override
        protected void onPreTabItemUnSelected(View tab, int position) {
            TextView textView = tab.findViewById(R.id.mvp_bottom_tab_value);
            CheckBox icon = tab.findViewById(R.id.mvp_bottom_tab_icon);
            icon.setChecked(false);
            textView.setTextColor(mTitleNormalColor == 0 ? Color.BLACK : mTitleNormalColor);

        }

        @Override
        protected void onTabRepeatSelect(View tab, int position) {

        }
    }

}
