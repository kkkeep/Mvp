package com.m.k.mvp.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.appcompat.widget.ContentFrameLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.cunoraz.gifview.library.GifView;
import com.m.k.mvp.R;
import com.m.k.mvp.Utils.SystemFacade;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/*
 * created by Cherry on 2019-12-23
 **/
public class MkLoadingView extends ConstraintLayout {

    public static final int MODE_TRANSPARENT_BACKGROUND = 0x100;
    public static final int MODE_WHITE_BACKGROUND = 0x101;
    public static final int MODE_ERROR = 0x102;
    private static final String TAG = "LoadingView";

    private ViewGroup mParent;


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MODE_TRANSPARENT_BACKGROUND, MODE_WHITE_BACKGROUND})
    public @interface Mode {
    }


    private OnRetryListener mRetryListener;

    public ImageView mGifLayout; // gif 动画的半透明背景
    public GifView mGifView; // gif 动画view

    private TextView mErrorMsg;
    private ImageView mErrorIcon;

    private Button mRetry;


    private int mCurrentMode;


    public MkLoadingView(Context context) {
        super(context);
    }

    public MkLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);


    }

    public MkLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mGifView = findViewById(R.id.mvp_loading_gif_view);
        mGifLayout = findViewById(R.id.mvp_loading_loading_view_container);

        mErrorMsg = findViewById(R.id.mvp_loading_tv_error_msg);
        mErrorIcon = findViewById(R.id.mvp_loading_iv_error_icon);

        mRetry = findViewById(R.id.mvp_loading_btn_retry);



        mRetry.setOnClickListener(v -> {
            // 重新请求网络
            if(mRetryListener != null){
                mRetryListener.retry();
            }
        });

    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);

    }

    /**
     * 循环遍历parent 里面的所有子控件，如果发现loadingview 已经是 parent 这个 viewGroup 的孩子，
     * 那么就直接把loading view  return 出去，如果找遍了所有子控件，没有loading view 那么就
     * 通过 LayoutInflater  inflate 一个出来，然后保存期 parent 到自己属性里面，并返回
     *
     * @param parent
     * @return
     */

    public static MkLoadingView inject(ViewGroup parent) {
        String parentName = parent.getClass().getSimpleName();

        if (parentName.equals(FrameLayout.class.getSimpleName()) ||
                parentName.equals(RelativeLayout.class.getSimpleName()) ||
                parentName.equals(ConstraintLayout.class.getSimpleName()) ||
                parentName.equals(ContentFrameLayout.class.getSimpleName())) {

            View child;
            for (int i = parent.getChildCount()-1; i >= 0; i--) {
                child = parent.getChildAt(i);
                if (child instanceof MkLoadingView) {
                    return (MkLoadingView) child;
                }
            }

            MkLoadingView loadingView = (MkLoadingView) LayoutInflater.from(parent.getContext()).inflate(R.layout.mvp_layout_loading, parent, false);

            loadingView.mParent = parent;

            return loadingView;
        } else {
            throw new IllegalArgumentException("Loading view 必须被添加到 FrameLayout,RelativeLayout 或者 ConstraintLayout 上");
        }
    }


    public void showMode(@Mode int mode) {

        if (this.getParent() == null) {
            String parentName = mParent.getClass().getSimpleName();
            if (parentName.equals(FrameLayout.class.getSimpleName()) || parentName.equals(RelativeLayout.class.getSimpleName()) || parentName.equals(ContentFrameLayout.class.getSimpleName())) {
                mParent.addView(this);
            } else {
                mParent.addView(this);
                ConstraintSet constraintSet = new ConstraintSet();
                this.setId(100000);
                constraintSet.clone((ConstraintLayout) mParent);
                constraintSet.connect(this.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                constraintSet.connect(this.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
                constraintSet.connect(this.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
                constraintSet.connect(this.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
                constraintSet.constrainWidth(this.getId(), ConstraintSet.MATCH_CONSTRAINT);
                constraintSet.constrainHeight(this.getId(), ConstraintSet.MATCH_CONSTRAINT);
                constraintSet.applyTo((ConstraintLayout) mParent);
            }
        }


        if(mCurrentMode == mode){
            return;
        }

        mCurrentMode = mode;

        hideOrShowErrorLayout();
        if (mode == MODE_WHITE_BACKGROUND) { //  白色背景
            setBackgroundColor(Color.WHITE);
            mGifLayout.setVisibility(View.GONE);
        } else { // 背景透明
            setBackgroundColor(Color.TRANSPARENT);
            mGifLayout.setVisibility(View.VISIBLE);

        }
        resetGifViewSize(mode);
        mGifView.setVisibility(View.VISIBLE);
        mGifView.play();

        mIsShow = true;

    }

    private boolean mIsShow;
    protected boolean isShow(){
        return mIsShow;
    }



    private void hideOrShowErrorLayout(){

        int visible = VISIBLE;
        if(mErrorIcon.getVisibility() == VISIBLE){
            visible = INVISIBLE;
        }

        mErrorIcon.setVisibility(visible);
        mErrorMsg.setVisibility(visible);
        mRetry.setVisibility(visible);
    }

    public void onError() {
        mCurrentMode = MODE_ERROR;
        setBackgroundColor(Color.WHITE);
        mGifView.setVisibility(View.GONE);
        mGifLayout.setVisibility(View.GONE);
        hideOrShowErrorLayout();
        mRetry.setVisibility(INVISIBLE);

    }

    public void onError(OnRetryListener retryListener) {
        mCurrentMode = MODE_ERROR;
        setBackgroundColor(Color.WHITE);
        mGifView.setVisibility(View.GONE);
        mGifLayout.setVisibility(View.GONE);
        hideOrShowErrorLayout();
        if(retryListener == null){
            mRetry.setVisibility(INVISIBLE);
        }else{
            mRetry.setVisibility(VISIBLE);
        }

        mRetryListener = retryListener;

    }

    public void onError(String msg, @DrawableRes int errorIconId,OnRetryListener retryListener) {
        mCurrentMode = MODE_ERROR;
        setBackgroundColor(Color.WHITE);
        mGifView.setVisibility(View.GONE);
        mGifLayout.setVisibility(View.GONE);
        hideOrShowErrorLayout();

        mErrorMsg.setText(msg);

        mErrorIcon.setBackgroundResource(errorIconId);
        if(retryListener == null){
            mRetry.setVisibility(INVISIBLE);
        }else{
            mRetry.setVisibility(VISIBLE);
        }

        mRetryListener = retryListener;

    }

    public void onError(String msg,OnRetryListener retryListener) {
        mCurrentMode = MODE_ERROR;
        setBackgroundColor(Color.WHITE);
        mGifView.setVisibility(View.GONE);
        mGifLayout.setVisibility(View.GONE);
        hideOrShowErrorLayout();
        mErrorMsg.setText(msg);


        if(retryListener == null){
            mRetry.setVisibility(INVISIBLE);
        }else{
            mRetry.setVisibility(VISIBLE);
        }

        mRetryListener = retryListener;
    }



    public void close() {
        if(this.getParent() != null){
            ((ViewGroup) getParent()).removeView(this);
            mIsShow = false;
        }
    }


    private void resetGifViewSize(int mode) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this);

        int width = 0;
        if (mode == MODE_TRANSPARENT_BACKGROUND) {
            width = SystemFacade.dp2px(getContext(), 90);
        } else {
            width = SystemFacade.dp2px(getContext(), 133);
        }

        constraintSet.constrainWidth(R.id.mvp_loading_gif_view, width);
        constraintSet.constrainHeight(R.id.mvp_loading_gif_view, width);

        constraintSet.applyTo(this);

    }


    public interface OnRetryListener {

        void retry();
    }


}
