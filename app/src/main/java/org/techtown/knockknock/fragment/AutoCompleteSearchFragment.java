package org.techtown.knockknock.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.techtown.knockknock.ErrorBody;
import org.techtown.knockknock.MainActivity;
import org.techtown.knockknock.R;
import org.techtown.knockknock.RetrofitClient;
import org.techtown.knockknock.adapter.AutoSuggestAdapter;
import org.techtown.knockknock.post.hashtag.HashTagAPI;
import org.techtown.knockknock.post.hashtag.HashTagData;
import org.techtown.knockknock.post.hashtag.HashTagListData;
import org.techtown.knockknock.post.postlist.PostListSearchByHashTagFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AutoCompleteSearchFragment extends Fragment {
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    private AutoSuggestAdapter autoSuggestAdapter;
    HashTagListData hashtaglist;
    private Button searchButton;

    public static AutoCompleteSearchFragment newInstance() {

        Bundle args = new Bundle();

        AutoCompleteSearchFragment fragment = new AutoCompleteSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.autocompletelayout, null);
        final AppCompatAutoCompleteTextView autoCompleteTextView = mView.findViewById(R.id.auto_complete_edit_text);
        searchButton = mView.findViewById(R.id.autocomplete_search_btn);
       // final TextView selectedText = mView.findViewById(R.id.selected_item);

        //adapter 세팅하기
        autoSuggestAdapter = new AutoSuggestAdapter(getContext(), android.R.layout.simple_dropdown_item_1line);
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

        //검색 버튼 기능 활성화
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(autoCompleteTextView.getText())){
                    String input = autoCompleteTextView.getText().toString();
                    Bundle bundle = new Bundle();
                    bundle.putString("hashtag",input);
                    Log.d("AutoCompleteSearchInput","searching for :"+input);
                    //넘어갈 fragment
                    PostListSearchByHashTagFragment postlisthashtagfragment = new PostListSearchByHashTagFragment();
                    postlisthashtagfragment.setArguments(bundle);
                    ((MainActivity)getActivity()).replaceFragment(postlisthashtagfragment);
                }
                else Toast.makeText(getContext(),"검색어를 입력해주세요", Toast.LENGTH_LONG);
            }
        });
        return mView;
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
    private List<String> hashTagDataListToStringList(List<HashTagData> hashTagDataList) {
        List<String> result = new ArrayList<>();
        for (HashTagData hashtag : hashTagDataList) {
            result.add(hashtag.getTag());
        }
        return result;
    }

    }
  
