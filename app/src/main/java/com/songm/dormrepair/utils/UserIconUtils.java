package com.songm.dormrepair.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ImageView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wenhuaijun.easyimageloader.imageLoader.EasyImageLoader;

import org.json.JSONObject;

/**
 * Created by SongM on 2017/9/21.
 * 设置用户头像
 */

public class UserIconUtils {

    public static void show(final Context context, final ImageView imageView) {
        // 取到用户信息
        SharedPreferences sharedPreferences = context.getSharedPreferences("info", context.MODE_PRIVATE);
        String id = sharedPreferences.getString("id", "");
        boolean stuOrHmr = sharedPreferences.getBoolean("stuOrHmr", true);
        // 设置url
        String url = UrlUtils.userIcon(id, "stu");
        if(!stuOrHmr) {
            url = UrlUtils.userIcon(id, "hmr");
        }
        OkGo.<String>get(url)
                .tag(context)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            String find_state = jsonObject.getString("find");
                            if(find_state.equals("success")) {
                                String iconUrl = UrlUtils.HOST + "/" + jsonObject.getString("iconUrl");
                                EasyImageLoader.getInstance(context).bindBitmap(iconUrl, imageView);
                            }
                        } catch (Exception e) {

                        }
                    }
                });
    }

}
