package com.songm.dormrepair;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.wenhuaijun.easyimageloader.imageLoader.EasyImageLoader;

import uk.co.senab.photoview.PhotoView;

/**
 * 浏览图片
 */
public class LookImageActivity extends AppCompatActivity {

    private PhotoView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_image);

        photoView = (PhotoView) findViewById(R.id.img_look_image);

        // 获取图片全路径
        String imgPath = getIntent().getStringExtra("imgPath");
        // 获取图片的URL
        String imgUrl = getIntent().getStringExtra("imgUrl");

        if(imgPath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
            photoView.setImageBitmap(bitmap);
        } else if(imgUrl != null) {
            // 根据图片url给imageView加载图片，自动本地缓存、内存缓存，注意Context需使用ApplicationContext，否则会导致内存泄露
            EasyImageLoader.getInstance(getApplicationContext()).bindBitmap(imgUrl, photoView);
            /*final ProgressDialog progressDialog = new ProgressDialog(this); // 创建加载框
            progressDialog.setMessage("加载中..."); // 设置加载框内容
            OkGo.<Bitmap>get(imgUrl)
                    .tag(this)
                    .execute(new BitmapCallback() {
                        @Override
                        public void onSuccess(Response<Bitmap> response) {
                            if (response.code() == 200) {
                                // PhotoView
                                photoView.setImageBitmap(response.body());
                            }
                        }
                        @Override
                        public void onStart(Request request) {
                            progressDialog.show(); // 启动加载框
                            super.onStart(request);
                        }
                        @Override
                        public void onFinish() {
                            progressDialog.dismiss(); // 关闭加载框
                            super.onFinish();
                        }
                    });*/
        }
    }
}
