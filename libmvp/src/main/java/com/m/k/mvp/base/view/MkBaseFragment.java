package com.m.k.mvp.base.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.ContentFrameLayout;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.m.k.mvp.R;
import com.m.k.mvp.widget.MkLoadingView;
import com.trello.rxlifecycle2.components.support.RxFragment;

public abstract class MkBaseFragment extends RxFragment implements BaseView {

    private MkLoadingView loadingView;



    @Nullable
    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        doOnPreCreateView(inflater,container,savedInstanceState);

        View view;

        int id = getLayoutId();
        if(id <= 0){
            return null;
        }

        View root = inflater.inflate(id,container,false);

        String rootLayoutName = root.getClass().getSimpleName();

        if (rootLayoutName.equals(FrameLayout.class.getSimpleName()) ||
                rootLayoutName .equals( RelativeLayout.class.getSimpleName()) ||
                rootLayoutName .equals( ConstraintLayout.class.getSimpleName()) ||
                rootLayoutName .equals( ContentFrameLayout.class.getSimpleName())){

            view = root;
        }else{

            FrameLayout frameLayout = new FrameLayout(getContext());

            frameLayout.setLayoutParams(root.getLayoutParams());

            frameLayout.addView(root);

            view = frameLayout;

        }


        bindingView(view);
        return view;


    }



    protected void doOnPreCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

    }

    protected void bindingView(View view){

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();

    }



    protected  void initView(){

    }



    @Override
    public MkLoadingView getLoadingView() {
        return loadingView;
    }

    @Override
    public void setLoadingView(MkLoadingView loadingView) {
        this.loadingView = loadingView;
    }

    @Override
    public <T extends View> T findViewById(int id) {
        View v =  getView().findViewById(id);

        if(v == null){
            return getActivity().findViewById(id);
        }
        return (T) v;

    }



    protected void showToast(@StringRes int id) {
        Toast.makeText(getContext(), id, Toast.LENGTH_SHORT).show();
    }

    protected void showToast( String content) {
        Toast.makeText(getContext(), content, Toast.LENGTH_SHORT).show();

    }


    public int getRootViewId() {
        return getView().getId();
    }

    public boolean isAddBackStack(){
        return true;
    }

    public boolean isNeedAnimation(){
        return false;
    }

    public Action getAction(){
        return Action.Hide;
    }


    // 显示一个新fragment 进入时的动画
    public int getEnter() {
        return R.anim.common_page_right_in;
    }

    // 显示一个新fragment 时，上一个fragment 出去的动画
    public int getExit() {
        return R.anim.common_page_left_out;
    }

    // 按返回键时，当前fragment 退出的动画
    public int popExit() {
        return R.anim.common_page_right_out;
    }

    // 按返回键时，另外一个fragment 进入时的动画
    public int popEnter() {
        return R.anim.common_page_left_in;
    }

    // 显示一个fragment 时，对上一个fragment 做的处理
    public enum Action {
        Remove, Detach, Hide, None
    }


}
