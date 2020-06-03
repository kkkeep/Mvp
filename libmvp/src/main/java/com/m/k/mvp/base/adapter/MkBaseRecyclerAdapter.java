package com.m.k.mvp.base.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.List;

@SuppressWarnings("ALL")
/**
 *
 */
public abstract class MkBaseRecyclerAdapter<D> extends RecyclerView.Adapter<MkBaseRecyclerAdapter.BaseAdapterHolder<D>> {




    protected List<D> mDataList;


    // 数据第一次加载回来调用该方法对adapter 设置数据
    public void setData(List<D> dataList){
        mDataList = dataList;
        notifyDataSetChanged();
    }




    // 对 recycler view 刷新时调用该方法
    public void refresh(List<D> dataList){
        setData(dataList);
    }


    // 加载更多的时候调用
    public void loadMore(List<D> dataList){
        int start = mDataList.size();
        mDataList.addAll(dataList);

        notifyItemRangeInserted(start,dataList.size());

    }



    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }



    public List<D> getDataList(){
        return  mDataList;
    }


    public D getDataByPosition(int position){

        return mDataList.get(position);
    }

    protected void onDataItemClick(D data, int postion){

    }
    public abstract int getItemLayoutId(int viewType);


    public abstract BaseAdapterHolder createHolder(View itemView,int viewType);

    @NonNull
    @Override
    public BaseAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(getItemLayoutId(viewType),parent,false);
        return createHolder(itemView,viewType);
    }


    @Override
    public void onBindViewHolder(@NonNull BaseAdapterHolder holder, int position) {
        holder.setAdapter(this);
        holder.bindData(mDataList.get(position));
    }


    public  abstract static class BaseAdapterHolder<D> extends RecyclerView.ViewHolder {

        private WeakReference<MkBaseRecyclerAdapter<D>> reference;

        public BaseAdapterHolder(@NonNull View itemView) {
            super(itemView);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();


                    MkBaseRecyclerAdapter<D> adapter;

                    if(reference != null && (adapter = reference.get()) != null){
                        adapter.onDataItemClick(adapter.getDataByPosition(position),position);
                    }else{
                        throw new NullPointerException("实例化 holder 后请调用 setAdapter 为holder 绑定adapter");
                    }

                }
            });

        }

        private void setAdapter(MkBaseRecyclerAdapter<D> adapter) {

            this.reference = new WeakReference<>(adapter);
        }

        public abstract void bindData(D data);


        public <V extends View> V  findViewById(@IdRes int id){

            return itemView.findViewById(id);
        }


    }
}
