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
public abstract class MkBaseRecyclerAdapter2<D1,D2> extends RecyclerView.Adapter<MkBaseRecyclerAdapter2.BaseAdapterHolder2<D1,D2>> {



    protected List<D1> mDataList1;
    protected List<D2> mDataList2;



    // 数据第一次加载回来调用该方法对adapter 设置数据
    public void setData(List<D1> dataList,List<D2> dataList2){
        mDataList1 = dataList;
        mDataList2 = dataList2;
        notifyDataSetChanged();
    }


    // 对 recycler view 刷新时调用该方法
    public void refresh(List<D1> dataList,List<D2> dataList2){
        setData(dataList,dataList2);
    }


    // 加载更多的时候调用
    public void loadMoreData1(List<D1> dataList){
        int start = mDataList1.size();
        mDataList1.addAll(dataList);

        notifyItemRangeInserted(start,dataList.size());

    }

    // 加载更多的时候调用
    public void loadMoreData2(List<D2> dataList2){
        int start = getItemCount();
        mDataList2.addAll(dataList2);

        notifyItemRangeInserted(start,dataList2.size());

    }



    @Override
    public int getItemCount() {
        int count1  = mDataList1 == null ? 0 : mDataList1.size();

        int count2 = mDataList2 == null ? 0 : mDataList2.size();
        return  count1 + count2;
    }


    protected int getData1StartPosition(){
        return 0;
    }

    protected int getData2StartPosition(){
        return mDataList1 == null ? 0 : mDataList1.size();
    }



    public List<D1> getData1List(){
        return mDataList1;
    }
    public List<D2> getData2List(){
        return mDataList2;
    }

    public D1 getData1ByPosition(int position){

        return mDataList1.get(position);
    }

    public D2 getData2ByPosition(int position){

        return mDataList2.get(position - getData2StartPosition());
    }



    public abstract int getItemLayoutId(int viewType);


    public abstract BaseAdapterHolder2 createHolder(View itemView,int viewType);

    @NonNull
    @Override
    public BaseAdapterHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(getItemLayoutId(viewType),parent,false);
        BaseAdapterHolder2 baseAdapterHolder2 = createHolder(itemView,viewType);
        baseAdapterHolder2.setAdapter(this);
        return baseAdapterHolder2;
    }


    protected void onData1ItemClick(D1 data1, int postion){

    }


    protected void onData2ItemClick(D2 data2, int postion){

    }

    @Override
    public void onBindViewHolder(@NonNull BaseAdapterHolder2 holder, int position) {

        if(position >= getData1StartPosition() && position < getData2StartPosition()){
            holder.bindData1(getData1ByPosition(position));
        }else{
            holder.bindData2(getData2ByPosition(position));
        }


    }


    public abstract static class BaseAdapterHolder2<D1,D2> extends RecyclerView.ViewHolder {

        private WeakReference<MkBaseRecyclerAdapter2<D1,D2>> reference;

        public BaseAdapterHolder2(@NonNull View itemView) {
            super(itemView);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();
                    MkBaseRecyclerAdapter2<D1,D2> adapter2;

                    if(reference != null && (adapter2 = reference.get()) != null){
                        if(position >= adapter2.getData1StartPosition() && position < adapter2.getData2StartPosition()){
                            adapter2.onData1ItemClick(adapter2.getData1ByPosition(position),position);
                        }else{
                            adapter2.onData2ItemClick(adapter2.getData2ByPosition(position),position);
                        }
                    }else{
                        throw new NullPointerException("实例化 holder 后请调用 setAdapter 为holder 绑定adapter");
                    }



                }
            });

        }

        private void setAdapter(MkBaseRecyclerAdapter2<D1,D2> adapter) {

            this.reference = new WeakReference<>(adapter);
        }

        public abstract void bindData1(D1 data);
        public abstract void bindData2(D2 data);

        public <V extends View> V  findViewById(@IdRes int id){

            return itemView.findViewById(id);
        }


    }





}
