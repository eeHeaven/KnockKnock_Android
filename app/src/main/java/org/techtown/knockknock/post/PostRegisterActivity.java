package org.techtown.knockknock.post;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.techtown.knockknock.ErrorBody;
import org.techtown.knockknock.MainActivity;
import org.techtown.knockknock.R;
import org.techtown.knockknock.RetrofitClient;
import org.techtown.knockknock.initial.MemberBasicInfo;
import org.techtown.knockknock.post.postdetail.PostDetailData;
import org.techtown.knockknock.user.MemberAPI;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

public class PostRegisterActivity extends AppCompatActivity {
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;

    private Uri mImageCaptureUri;
    private File destFile;
    private String imagePath; // ????????? ????????????

    static int point;
    private static ActivityResultLauncher<Intent> resultLauncher; // startActivityforResult ?????? ??? ??????
    private static int CallType;

    EditText title;
    EditText hashtag;
    EditText content;
    Button writePost;
    ImageView img;


    //??????
    private Float lat;
    private Float lon;
    Geocoder geocoder;


    SharedPreferences preferences;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    // ????????? ????????? ?????? ????????? ????????? doTakePhotoAction  ?????????
    public void doTakePhotoAction(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // ????????? ????????? ????????? ????????? ??????
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        File tempFile = new File(Environment.getExternalStorageDirectory(), url);
        mImageCaptureUri = Uri.fromFile(tempFile);
        imagePath = tempFile.getAbsolutePath();
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

        CallType = PICK_FROM_CAMERA;
        resultLauncher.launch(intent);
    }
    // ???????????? ????????? ????????? doTakeAlbumAction ?????????
    public void doTakeAlbumAction(){
        //?????? ??????
        verifyStoragePermissions(this);
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        CallType = PICK_FROM_ALBUM;
        resultLauncher.launch(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_register);

        geocoder = new Geocoder(this);

        title = findViewById(R.id.tv_postRegister_title);
        hashtag = findViewById(R.id.tv_postRegister_hashtag);
        content = findViewById(R.id.tv_postRegister_content);
        writePost = findViewById(R.id.reg_button);
        img = findViewById(R.id.img_postregister);

        SharedPreferences locationPreferences = this.getSharedPreferences("Location",MODE_PRIVATE);
        lat = locationPreferences.getFloat("latitude",0);
        lon = locationPreferences.getFloat("longitude",0);


        // ????????? ????????? ??????
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doTakePhotoAction();
                    }
                };
                DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doTakeAlbumAction();
                    }
                };
                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };
                new AlertDialog.Builder(PostRegisterActivity.this)
                        .setTitle("???????????? ????????? ??????")
                        .setPositiveButton("????????????", cameraListener)
                        .setNeutralButton("????????????", albumListener)
                        .setNegativeButton("??????", cancelListener)
                        .show();
            }
            });

        writePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                preferences = getSharedPreferences("UserInfo",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                // TextView?????? ?????? ?????? ????????????
                String posttitle = title.getText().toString();
                String posthashtag = hashtag.getText().toString();
                String postcontent = content.getText().toString();

                //????????? ????????????
                String location = "???????????? ?????? ?????? ??????";
                List<Address> list = null;
                try{
                    list = geocoder.getFromLocation(lat,lon,10);
                }catch(IOException e){
                    e.printStackTrace();
                    Log.e("MypageFragment","Geocoder ????????? ??????");
                }
                if(list != null){
                    if(list.size() == 0) location = "???????????? ???????????? ?????? ????????? ????????????.";
                    else location = list.get(0).getAddressLine(0);
                }

                PostSaveRequest request = new PostSaveRequest(posttitle,posthashtag,postcontent,lat,lon,location);

                SharedPreferences sharedPreferences = getSharedPreferences("UserInfo",MODE_PRIVATE);
                String id = sharedPreferences.getString("userId","");

                PostAPI postAPI = RetrofitClient.getInstance().create(PostAPI.class);
                MemberAPI memberAPI = RetrofitClient.getInstance().create(MemberAPI.class);

                MultipartBody.Part Bmp = FileToMultipart();
                //request ????????? json?????? ??????
                String json = new Gson().toJson(request);
                RequestBody jsonBody = RequestBody.create(MediaType.parse("application/json"),json);

                Call<PostDetailData> call = postAPI.writePost(id,jsonBody,Bmp);
                call.enqueue(new Callback<PostDetailData>() {
                    @Override
                    public void onResponse(Call<PostDetailData> call, Response<PostDetailData> response1) {
                        if(response1.isSuccessful()){
                            Log.d("PostRegisterActivity","????????? ?????? ??????");
                            Toast.makeText(getApplicationContext(),"????????? ????????? ?????????????????????.",Toast.LENGTH_LONG).show();
                            // ????????? ????????? sharePoint +5??? ??????
                            Call<MemberBasicInfo> postsaveCall = memberAPI.sharePoint(id, 5);
                            postsaveCall.enqueue(new Callback<MemberBasicInfo>() {
                                @Override
                                public void onResponse(Call<MemberBasicInfo> call, Response<MemberBasicInfo> response2) {
                                    if(response2.isSuccessful()){
                                    }
                                    else{
                                        ErrorBody error = new Gson().fromJson(response2.errorBody().charStream(),ErrorBody.class);
                                        Log.d("PostRegisterActivity",error.getMessage());
                                    }
                                }

                                @Override
                                public void onFailure(Call<MemberBasicInfo> call, Throwable t) {
                                    Log.d("PostRegisterActivity",t.getMessage());
                                }
                            });
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            ErrorBody error = new Gson().fromJson(response1.errorBody().charStream(),ErrorBody.class);
                            Log.d("PostRegisterActivity",error.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<PostDetailData> call, Throwable t) {
                        Log.d("PostRegisterActivity",t.getMessage());
                    }
                });

            }
        });

        // ???????????? ?????? ??????
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                        if(result.getResultCode() == RESULT_OK){
                            Intent intent = result.getData();
                            switch (CallType){
                                case PICK_FROM_ALBUM:
                                    mImageCaptureUri = intent.getData();
                                    Glide.with(this).load(mImageCaptureUri).into(img);
                                    imagePath = getRealPathFromURI(mImageCaptureUri);
                                    destFile = new File(imagePath);

                                    Log.d("PostRegister","?????? ????????? ??????");
                                    Log.d("PostRegister",mImageCaptureUri.getPath().toString());
                                    break;
                                case PICK_FROM_CAMERA:
                                    Glide.with(this).load(mImageCaptureUri).into(img);
                                    destFile = new File(imagePath);
                                    break;

                            }
                        }

                }
        );


    }

    private String getRealPathFromURI(Uri contentUri){
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri,proj,null,null,null);
        cursor.moveToNext();

        @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
        Uri uri = Uri.fromFile(new File(path));
        cursor.close();
        return path;
    }
    //????????? ?????? multipart ????????? ?????????
    private MultipartBody.Part FileToMultipart(){
        if(destFile == null) return null;
        RequestBody requestBmp = RequestBody.create(MediaType.parse("multipart/form-data"), destFile);
        MultipartBody.Part Bmp = MultipartBody.Part.createFormData("image", destFile.getName(), requestBmp);
        return Bmp;
    }
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                okhttp3.MultipartBody.FORM, descriptionString);
    }

}