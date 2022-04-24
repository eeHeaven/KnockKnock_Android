package org.techtown.knockknock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.techtown.knockknock.adapter.AutoSuggestAdapter;
import org.techtown.knockknock.post.PostAPI;
import org.techtown.knockknock.post.hashtag.HashTagAPI;
import org.techtown.knockknock.post.hashtag.HashTagData;
import org.techtown.knockknock.post.hashtag.HashTagListData;
import org.techtown.knockknock.post.postlist.PostListData;
import org.techtown.knockknock.post.postlist.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestActivity extends AppCompatActivity {
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    private AutoSuggestAdapter autoSuggestAdapter;
    HashTagListData hashtaglist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.autocompletelayout);
        final AppCompatAutoCompleteTextView autoCompleteTextView = findViewById(R.id.auto_complete_edit_text);
     //   final TextView selectedText = findViewById(R.id.selected_item);

        //adapter 세팅하기
        autoSuggestAdapter = new AutoSuggestAdapter(this, android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(autoSuggestAdapter);
        autoCompleteTextView.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                      //  selectedText.setText(autoSuggestAdapter.getItem(position));
                    }
                }
        );
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                if(message.what == TRIGGER_AUTO_COMPLETE){
                    if(!TextUtils.isEmpty(autoCompleteTextView.getText())){
                        makeAPICall(autoCompleteTextView.getText().toString());
                    }
                }
                return false;
            }
        });
    }
    private void makeAPICall(String text){
        HashTagAPI hashTagAPI = RetrofitClient.getInstance().create(HashTagAPI.class);
        Call<HashTagListData> call = hashTagAPI.getHashTagListDatabyAutoCompleteInput(text);
        Log.d("TestActivity: 자동완성 리스트", "input = "+text);
        call.enqueue(new Callback<HashTagListData>() {
            @Override
            public void onResponse(Call<HashTagListData> call, Response<HashTagListData> response) {
                if (response.isSuccessful()) {
                    hashtaglist = response.body();
                    Log.d("TestActivity: 자동완성 리스트", hashtaglist.toString());
                    List<HashTagData> hashtags= hashtaglist.data;
                    List<String> finalList = hashTagDataListToStringList(hashtags);
                    for(String s: finalList) Log.d("TestActivity: 자동완성 리스트","자동완성 결과 = "+s);
                    autoSuggestAdapter.setData(finalList);
                    autoSuggestAdapter.notifyDataSetChanged();
                } else {
                    ErrorBody error = new Gson().fromJson(response.errorBody().charStream(), ErrorBody.class);
                    Log.d("TestActivity", error.getMessage());
                }
            }

            @Override
            public void onFailure(Call<HashTagListData> call, Throwable t) {
                Log.d("TestActivity", t.getMessage());
            }
        });
    }
    private List<String> hashTagDataListToStringList(List<HashTagData> hashTagDataList){
        List<String> result = new ArrayList<>();
        for(HashTagData hashtag : hashTagDataList){
            result.add(hashtag.getTag());
        }
        return result;
    }
}