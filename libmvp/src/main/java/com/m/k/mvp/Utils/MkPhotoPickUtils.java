package com.m.k.mvp.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.m.k.mvp.provider.MkPhotoFileProvider;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 一个用于打开手机相机拍照，或者从系统相册选择一个图片的工具栏
 *
 *
 *
 *
 * 用法：
 *
 *
 * 第一步： new 一个实例对象,传入相应的参数
 * 第二部： 调用 openCamera 打开相机，或者 调用 openGallery 打开相册
 *
 * 第三部： 在 你自己的 Fragment 或者 Activity 中重写 onActivityResult ，并在其调用 PhotoPickUtils.onActivityResult
 *
 */
public class MkPhotoPickUtils {


    private static final String USER_HEAD_IMG_PATH = "mvp_photo_pick";
    private static final String USER_HEAD_IMG_NAME = "pick.jpg";

    private static final int CAMERA = 0X100;
    private static final int GALLERY = 0X101;
    private static final int CROP = 0X102;

    private FragmentActivity mActivity;

    private Fragment mFragment;
    private OnPhotoBack mPhotoBack;

    private int mCropWidth;
    private int mCropHeight;
    private boolean mIsCrop;


    /**
     * 在 activity 中使用这个构造方法
     * @param fragmentActivity
     * @param isCrop ，是否需要对拍照或者相册的图片进行剪切，true 表示剪切，否则false，默认剪切图片的大小是300 * 300
     * @param photoBack ,回调接口，返回 拍照或者相册的图片路径
     */

    public MkPhotoPickUtils(FragmentActivity fragmentActivity, boolean isCrop, OnPhotoBack photoBack) {
        this.mActivity = fragmentActivity;
        this.mPhotoBack = photoBack;
        mIsCrop = isCrop;
        mCropWidth = 300;
        mCropHeight = 300;
    }

    /**
     * 在 fragment 中使用使用这个构造方法
     * @param fragment
     * @param isCrop 是否需要对拍照或者相册的图片进行剪切，true 表示剪切，否则false，默认剪切图片的大小是300 * 300
     * @param photoBack 回调接口，返回 拍照或者相册的图片路径
     */
    public MkPhotoPickUtils(Fragment fragment, boolean isCrop, OnPhotoBack photoBack) {
        this(fragment.getActivity(), isCrop, photoBack);
        mFragment = fragment;
    }


    /**
     * 剪切图片的宽高比，必须是整数
     * @param cropWidth
     * @param cropHeight
     */
    public void setCropXY(int cropWidth,int cropHeight) {
        this.mCropWidth = cropWidth;
        this.mCropHeight = cropHeight;
    }

    public void openCamera() {


        MkPermissionUtils permissionUtils = new MkPermissionUtils(mActivity);
        String permiss[] = new String[]{Manifest.permission.CAMERA};
        permissionUtils.checkPermission(mActivity, new MkPermissionUtils.OnPermissionCallBack() {
            @Override
            public void onAllMustAccept() {
                openSystemCamera();
            }

            @Override
            public void shouldShowRationale(MkPermissionUtils.PermissionCall call) {
                Toast.makeText(mActivity, "需要相机权限才能打开相机进行拍照", Toast.LENGTH_SHORT).show();
                call.requestPermission();
            }


            @Override
            public void shouldShowPermissionSetting() {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", mActivity.getPackageName(), null));
                mActivity.startActivity(intent);
                Toast.makeText(mActivity, "需要相机权限才能打开相机进行拍照", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied() {
                Toast.makeText(mActivity, "需要相机权限才能打开相机进行拍照", Toast.LENGTH_SHORT).show();
            }
        }, permiss, null);

    }

    public void openGallery() {

        MkPermissionUtils permissionUtils = new MkPermissionUtils(mActivity);
        String permiss[] = new String[]{Manifest.permission.CAMERA};
        permissionUtils.checkPermission(mActivity, new MkPermissionUtils.OnPermissionCallBack() {
            @Override
            public void onAllMustAccept() {
                openSystemGallery();
            }

            @Override
            public void shouldShowRationale(MkPermissionUtils.PermissionCall call) {
                Toast.makeText(mActivity, "需要相册权限才能获取照片", Toast.LENGTH_SHORT).show();
                call.requestPermission();
            }


            @Override
            public void shouldShowPermissionSetting() {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", mActivity.getPackageName(), null));

                mActivity.startActivity(intent);
                Toast.makeText(mActivity, "需要相册权限才能获取照片", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied() {
                Toast.makeText(mActivity, "需要相册权限才能获取照片", Toast.LENGTH_SHORT).show();
            }
        }, permiss, null);

    }


    private void openSystemCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  //跳转到 ACTION_IMAGE_CAPTURE
        //判断内存卡是否可用，可用的话就进行存储
        //putExtra：取值，Uri.fromFile：传一个拍照所得到的文件，fileImg.jpg：文件名

        File path = SystemFacade.getExternalCacheDir(mActivity, USER_HEAD_IMG_PATH);

        if (!path.exists()) {
            path.mkdir();
        }

        File file = new File(path, USER_HEAD_IMG_NAME);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, getUriFromFile(file));
        startActivityForResult(intent, CAMERA);
    }


    private void openSystemGallery() {
        Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(picture, GALLERY);
    }

    private void cropPic(Uri uri) {

        File path = SystemFacade.getExternalCacheDir(mActivity, USER_HEAD_IMG_PATH);

        if (!path.exists()) {
            path.mkdir();
        }

        File file = new File(path, USER_HEAD_IMG_NAME);


        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);

       int x;
       int y;


       if(mCropWidth > mCropHeight){
           y = 1;
           x = mCropWidth / mCropHeight;

       }else if(mCropHeight > mCropWidth){

           x = 1;

           y = mCropHeight / mCropWidth;
       }else{
           x = 1;
           y = 1;
       }


        intent.putExtra("aspectX", x);
        intent.putExtra("aspectY", y);


        intent.putExtra("outputX", mCropWidth);
        intent.putExtra("outputY", mCropHeight);
        intent.putExtra("circleCrop", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        intent.putExtra("return-data", false);

        startActivityForResult(intent, CROP);

    }

    public void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        if (resultCode != Activity.RESULT_CANCELED) {

            if (requestCode == CAMERA) {
                File file = SystemFacade.getExternalCacheDir(mActivity, USER_HEAD_IMG_PATH + File.separator + USER_HEAD_IMG_NAME);

                if (file != null && file.exists()) {
                    if (mIsCrop) {
                        cropPic(getUriFromFile(file));
                    } else {
                        mPhotoBack.onBack(file.getAbsolutePath());
                    }

                } else {
                    mPhotoBack.onBack(null);
                }


            } else if (requestCode == GALLERY) {

                Uri uri = data.getData();

                if (uri != null) {
                    if (mIsCrop) {
                        cropPic(uri);
                    } else {

                        File path = SystemFacade.getExternalCacheDir(mActivity, USER_HEAD_IMG_PATH);

                        if (!path.exists()) {
                            path.mkdir();
                        }

                        File file = new File(path, USER_HEAD_IMG_NAME);


                        savePhotoToFile(uri, file.getAbsolutePath());
                    }

                }
            } else {
                File file = SystemFacade.getExternalCacheDir(mActivity, USER_HEAD_IMG_PATH + File.separator + USER_HEAD_IMG_NAME);

                if (file != null && file.exists()) {
                    mPhotoBack.onBack(file.getAbsolutePath());
                } else {
                    mPhotoBack.onBack(null);
                }

            }
        }

    }

    private void savePhotoToFile(Uri uri, String file) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                ParcelFileDescriptor parcelFileDescriptor = null;
                try {
                    parcelFileDescriptor = mActivity.getContentResolver().openFileDescriptor(uri, "r");
                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                    Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    image.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);


                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPhotoBack.onBack(file);
                        }
                    });


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPhotoBack.onBack(null);
                        }
                    });


                } finally {
                    if (parcelFileDescriptor != null) {
                        try {
                            parcelFileDescriptor.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }


            }
        }).start();

    }


    private Uri getUriFromFile(File file) {

        Uri uri = null;

        if (SystemFacade.hasN()) {
            uri = MkPhotoFileProvider.getUriForFile(mActivity, mActivity.getPackageName() + ".userhead.HeadProvider", file);
        } else {
            uri = Uri.fromFile(file);
        }

        return uri;
    }

    private void startActivityForResult(Intent intent, int code) {

        if (mFragment != null) {
            mFragment.startActivityForResult(intent, code);
        } else {
            mActivity.startActivityForResult(intent, code);
        }
    }


    public static interface OnPhotoBack {

        void onBack(String filePath);
    }
}
