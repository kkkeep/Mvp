package com.m.k.mvp.base.presenter;

import com.m.k.mvp.base.callback.MkIBaseCallBack;
import com.m.k.mvp.base.model.MkBaseMvpModel;
import com.m.k.mvp.base.view.MkIBaseSmartView;
import com.m.k.mvp.exception.MkResultException;
import com.m.k.mvp.net.MkDataRequest;
import com.m.k.mvp.net.MkDataResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MkBaseSmartPresenter<D, V extends MkIBaseSmartView<D, ?>> extends MkBasePresenter<V> implements MkIBaseSmartPresenter<V> {

    protected MkBaseMvpModel mModel;

    private Type mType;


    public MkBaseSmartPresenter() {

        mModel = new MkBaseMvpModel();
    }

    public MkBaseSmartPresenter(MkBaseMvpModel mModel) {
        this.mModel = mModel;
    }

    @Override
    public void setDataType(Type dataType) {
        mType = dataType;
    }

    protected Type getDataType() {
        return mType;
    }

    @Override
    public void doRequest(MkDataRequest dataRequest) {

        setDataTypeToRequest(dataRequest, mType);

        mModel.doRequest(getProvider(), dataRequest, new MkIBaseCallBack<D>() {

            @Override
            public void onMemorySuccess(D d) {
                handOnDataSuccess(dataRequest.getRequestType(), MkDataResponse.ResponseType.MEMORY, d);
            }

            @Override
            public void onSdcardSuccess(D d) {
                handOnDataSuccess(dataRequest.getRequestType(), MkDataResponse.ResponseType.DISK, d);
            }

            @Override
            public void onStart(Disposable disposable) {
                if (dataRequest.isEnableCancel()) {
                    handOnStartRequest(disposable);
                }
            }

            @Override
            public void onServerSuccess(D data) {
                handOnDataSuccess(dataRequest.getRequestType(), MkDataResponse.ResponseType.SERVER, data);
            }

            @Override
            public void onFail(MkResultException exception) {

                handOnDataFail(dataRequest.getRequestType(), MkDataResponse.ResponseType.SERVER, exception);

            }
        });

    }




    protected void handOnStartRequest(Disposable disposable) {

        if (mDisposable == null) {
            mDisposable = new CompositeDisposable();
        }
        mDisposable.add(disposable);

    }

    protected void handOnDataSuccess(MkDataRequest.RequestType requestType, MkDataResponse.ResponseType responseType, D data) {
        if (mView != null) {
            mView.onResult(new MkDataResponse<>(responseType,requestType, data));
        }
    }

    protected void handOnDataFail(MkDataRequest.RequestType requestType, MkDataResponse.ResponseType responseType, MkResultException exception) {
        if (mView != null) {
            mView.onResult(new MkDataResponse<>(responseType,requestType, exception.getMessage()));
        }
    }


    protected void setDataTypeToRequest(MkDataRequest dataRequest, Type type) {

        try {
            Method method = dataRequest.getClass().getSuperclass().getDeclaredMethod("setDataType", Type.class);
            method.setAccessible(true);
            method.invoke(dataRequest, type);


        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }


}
