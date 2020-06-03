package com.m.k.mvp.base.presenter;

import com.m.k.mvp.base.callback.MkIBaseCallBack;
import com.m.k.mvp.base.model.MkBaseMvpModel;
import com.m.k.mvp.base.view.MkIBaseSmartView3;
import com.m.k.mvp.exception.MkResultException;
import com.m.k.mvp.net.MkDataRequest;
import com.m.k.mvp.net.MkDataResponse;

import java.lang.reflect.Type;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MkBaseSmartPresenter3<D1,D2,D3 ,V extends MkIBaseSmartView3<D1,D2,D3,?>> extends MkBaseSmartPresenter2<D1,D2,V> implements MkIBaseSmartPresenter3<V> {

     private Type mType3;


    @Override
    public void setDataType3(Type dataType) {
            mType3 = dataType;
    }


    public MkBaseSmartPresenter3() {
        super();
    }

    public MkBaseSmartPresenter3(MkBaseMvpModel mModel) {
        super(mModel);
    }

    @Override
    public void doRequest3(MkDataRequest dataRequest) {
        setDataTypeToRequest(dataRequest,mType3);
        mModel.doRequest(getProvider(), dataRequest, new MkIBaseCallBack<D3>() {

            @Override
            public void onMemorySuccess(D3 d) {
                handOnData3Success(dataRequest.getRequestType(), MkDataResponse.ResponseType.MEMORY, d);
            }

            @Override
            public void onSdcardSuccess(D3 d) {
                handOnData3Success(dataRequest.getRequestType(), MkDataResponse.ResponseType.DISK, d);
            }


            @Override
            public void onStart(Disposable disposable) {
                if(dataRequest.isEnableCancel()){
                    handOnStartRequest3(disposable);
                }
            }

            @Override
            public void onServerSuccess(D3 data) {
               handOnData3Success(dataRequest.getRequestType(), MkDataResponse.ResponseType.SERVER,data);
            }

            @Override
            public void onFail(MkResultException exception) {

               handOnData3Fail(dataRequest.getRequestType(),exception);
            }
        });
    }


    protected void handOnStartRequest3(Disposable disposable) {

        if (mDisposable == null) {
            mDisposable = new CompositeDisposable();
        }
        mDisposable.add(disposable);

    }

    protected void handOnData3Success(MkDataRequest.RequestType requestType, MkDataResponse.ResponseType responseType, D3 data) {
        if (mView != null) {
            mView.onResult3(new MkDataResponse<>(responseType,requestType, data));
        }
    }

    protected void handOnData3Fail(MkDataRequest.RequestType requestType, MkResultException exception) {
        if (mView != null) {
            mView.onResult3(new MkDataResponse<>(requestType, exception.getMessage()));
        }
    }
}
