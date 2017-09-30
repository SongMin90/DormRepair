package com.songm.dormrepair.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okgo.request.base.Request;
import com.nanchen.compresshelper.CompressHelper;
import com.songm.dormrepair.LookImageActivity;
import com.songm.dormrepair.R;
import com.songm.dormrepair.utils.SnackbarUtil;
import com.songm.dormrepair.utils.UrlUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.zhouzhuo.zzimagebox.ZzImageBox;

import static android.app.Activity.RESULT_OK;

/**
 * Created by SongM on 2017/9/19.
 * 新增报修单
 */

public class NewOrderFragment extends Fragment {

    private ZzImageBox imageBox;
    private Button btn_neworder_submit;
    private EditText et_neworder_orderContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_neworder, null);

        // 添加图片
        imageBox = (ZzImageBox) view.findViewById(R.id.zz_image_box);
        imageBox.setOnImageClickListener(new ZzImageBox.OnImageClickListener() {
            @Override
            public void onImageClick(int position, String filePath, ImageView iv) {
                //Log.d("ZzImageBox", "image clicked:" + position + "," + filePath);
                Intent intent = new Intent(getContext(), LookImageActivity.class);
                intent.putExtra("imgPath", filePath);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(int position, String filePath) {
                imageBox.removeImage(position);
                //Log.d("ZzImageBox", "delete clicked:" + position + "," + filePath);
                //Log.d("ZzImageBox", "all images\n"+ imageBox.getAllImages().toString());
            }

            @Override
            public void onAddClick() {
                Intent intent = new Intent();
				/* 开启Pictures画面Type设定为image */
                intent.setType("image/*");
				/* 使用Intent.ACTION_GET_CONTENT这个Action */
                intent.setAction(Intent.ACTION_PICK);
				/* 取得相片后返回本画面 */
                startActivityForResult(intent, 1);

                //imageBox.addImage(null);
                //Log.d("ZzImageBox", "add clicked");
                //Log.d("ZzImageBox", "all images\n"+ imageBox.getAllImages().toString());
            }
        });

        // 提交报修单
        btn_neworder_submit = (Button) view.findViewById(R.id.btn_neworder_submit);
        btn_neworder_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 取到报修内容，并验证
                et_neworder_orderContent = (EditText) view.findViewById(R.id.et_neworder_orderContent);
                String orderContent = et_neworder_orderContent.getText().toString().trim();
                if(TextUtils.isEmpty(orderContent)) {
                    et_neworder_orderContent.setError("此字段不能为空！");
                    et_neworder_orderContent.requestFocus();
                    return;
                }
                // 取到提交的用户ID和所在的楼号寝室
                SharedPreferences sp = getContext().getSharedPreferences("info", getContext().MODE_PRIVATE);
                boolean stuOrHmr = sp.getBoolean("stuOrHmr", false);
                final String id = sp.getString("id", null);
                final String orderRoom = sp.getString("room", null);
                // OkGo
                PostRequest<String> tag = OkGo.<String>post(UrlUtils.addOrder).tag(getContext());
                // 设置请求参数
                if(stuOrHmr) {
                    tag.params("stuId", id);
                } else {
                    tag.params("hmrId", id);
                }
                tag.params("orderInfo", orderContent);
                tag.params("orderRoom", orderRoom);
                // 网络请求
                final ProgressDialog dialog = new ProgressDialog(getContext()); // 加载框
                dialog.setTitle("提交中...");
                tag.execute(new StringCallback() {
                    @Override
                    public void onStart(Request<String, ? extends Request> request) {
                        super.onStart(request);
                        dialog.show();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        dialog.dismiss();
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            String add_state = jsonObject.getString("add");
                            if (add_state.equals("error")) {
                                SnackbarUtil.ShortSnackbar(btn_neworder_submit, jsonObject.getString("reason"), SnackbarUtil.Alert).setActionTextColor(Color.WHITE).show();
                            } else {
                                SnackbarUtil.ShortSnackbar(btn_neworder_submit, "订单已提交！", SnackbarUtil.Confirm).setActionTextColor(Color.WHITE).show();
                                // 清空历史报修内容
                                et_neworder_orderContent.setText(null);
                                // 上传图片
                                uploadImg(jsonObject.getInt("orderId"));
                            }
                        } catch (Exception e) {

                        }
                    }
                });
            }
        });

        return view;
    }

    // 上传图片
    private void uploadImg(int orderId) {
        List<String> allImages = imageBox.getAllImages();
        List<File> list = new ArrayList<File>();
        for (int i=0; i<allImages.size(); i++) {
            File newFile = CompressHelper.getDefault(getContext()).compressToFile(new File(allImages.get(i))); // 压缩原图片
            list.add(newFile);
        }
        final ProgressDialog dialog = new ProgressDialog(getContext()); // 加载框
        dialog.setTitle("图片上传中...");
        OkGo.<String>post(UrlUtils.uploadImg)
                .tag(getContext())
                .params("orderId", orderId)
                .addFileParams("orderImg", list)
                .execute(new StringCallback() {
                    @Override
                    public void onStart(Request<String, ? extends Request> request) {
                        super.onStart(request);
                        dialog.show();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        dialog.dismiss();
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            String add_state = jsonObject.getString("add");
                            if(add_state.equals("error")) {
                                SnackbarUtil.ShortSnackbar(btn_neworder_submit, jsonObject.getString("reason"), SnackbarUtil.Alert).setActionTextColor(Color.WHITE).show();
                            } else {
                                SnackbarUtil.ShortSnackbar(btn_neworder_submit, "图片上传成功！", SnackbarUtil.Confirm).setActionTextColor(Color.WHITE).show();
                                // 清空历史图片
                                imageBox.removeAllImages();
                            }
                        } catch (Exception e) {

                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String path = getRealPathFromURI(uri);
            if(path != null) {
                imageBox.addImage(path); // 添加图片
            } else {
                SnackbarUtil.ShortSnackbar(btn_neworder_submit, "无法选取此图片！", SnackbarUtil.Alert).setActionTextColor(Color.WHITE).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 取出图片全路径
    private String getRealPathFromURI(Uri contentUri) { // 传入图片uri地址
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getActivity(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
