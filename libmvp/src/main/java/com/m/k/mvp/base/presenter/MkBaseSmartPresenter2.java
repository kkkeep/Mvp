package com.m.k.mvp.base.presenter;

import com.m.k.mvp.base.callback.MkIBaseCallBack;
import com.m.k.mvp.base.model.MkBaseMvpModel;
import com.m.k.mvp.base.view.MkIBaseSmartView2;
import com.m.k.mvp.exception.MkResultException;
import com.m.k.mvp.net.MkDataRequest;
import com.m.k.mvp.net.MkDataResponse;

import java.lang.reflect.Type;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MkBaseSmartPresenter2<D1,D2 ,V extends MkIBaseSmartView2<D1,D2,?>> extends MkBaseSmartPresenter<D1,V> implements MkIBaseSmartPresenter2<V> {

     private Type mType2;

    @Override
    public void setDataType2(Type dataType) {
        mType2 = dataType;
    }


    public MkBaseSmartPresenter2() {
        super();
    }

    public MkBaseSmartPresenter2(MkBaseMvpModel mModel) {
        super(mModel);
    }

    @Override
    public void doRequest2(MkDataRequest dataRequest) {
        setDataTypeToRequest(dataRequest,mType2);

        mModel.doRequest(getProvider(), dataRequest, new MkIBaseCallBack<D2>() {
            @Override
            public void onMemorySuccess(D2 d) {
                handOnData2Success(dataRequest.getRequestType(), MkDataResponse.ResponseType.MEMORY, d);
            }

            @Override
            public void onSdcardSuccess(D2 d) {
                handOnData2Success(dataRequest.getRequestType(), MkDataResponse.ResponseType.DISK, d);
            }

            @Override
            public void onStart(Disposable disposable) {
                if(dataRequest.isEnableCancel()){

                   handOnStartRequest2(disposable);
                }
            }

            @Override
            public void onServerSuccess(D2 data) {
                handOnData2Success(dataRequest.getRequestType(), MkDataResponse.ResponseType.SERVER, data);
            }

            @Override
            public void onFail(MkResultException exception) {
                handOnData2Fail(dataRequest.getRequestType(), exception);


            }
        });
    }


    protected void handOnStartRequest2(Disposable disposable) {

        if (mDisposable == null) {
            mDisposable = new CompositeDisposable();
        }
        mDisposable.add(disposable);

    }

    protected void handOnData2Success(MkDataRequest.RequestType requestType, MkDataResponse.ResponseType responseType, D2 data) {
        if (mView != null) {
            mView.onResult2(new MkDataResponse<>(responseType,requestType, data));
        }
    }

    protected void handOnData2Fail(MkDataRequest.RequestType requestType, MkResultException exception) {
        if (mView != null) {
            mView.onResult2(new MkDataResponse<>(requestType, exception.getMessage()));
        }
    }
}
