package org.techtown.knockknock.post.postdetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.knockknock.ItemClickListener;
import org.techtown.knockknock.MainActivity;
import org.techtown.knockknock.R;

import java.util.List;

public class HashTagRecyclerAdapter extends RecyclerView.Adapter<HashTagRecyclerAdapter.MyHashTagViewHolder>{

    private Context c;
    private List<String> hashtaglist;
    private PostdetailFragment fragment;

    public HashTagRecyclerAdapter(Context c, List<String> hashtaglist, PostdetailFragment fragment){
        this.c = c;
        this.hashtaglist = hashtaglist;
        this.fragment = fragment;
    }

    //페이지 새로 고침 
    public void refresh(){
      fragment.refresh();
    }

    @NonNull
    @Override
    public HashTagRecyclerAdapter.MyHashTagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){

        c = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view =inflater.inflate(R.layout.listview_posthashtag,parent,false);
        return new HashTagRecyclerAdapter.MyHashTagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HashTagRecyclerAdapter.MyHashTagViewHolder holder, int position){

        String tag  = hashtaglist.get(position);
        holder.hashtag.setText(tag);
       holder.itemClickListener = new ItemClickListener(){

           @Override
           public void onItemClickListener(View v, int position) {
               Log.d("HashTagRecyclerAdapter",hashtaglist.get(position)+" 클릭");
               //hashtag 값을 postlistSearchByhashtag fragment로 넘기기
               String tag = hashtaglist.get(position);
               Bundle bundle = new Bundle();
               bundle.putString("hashtag",tag);

               //넘어갈 fragment
               PostListSearchByHashTagFragment postlisthashtagfragment = new PostListSearchByHashTagFragment();
               postlisthashtagfragment.setArguments(bundle);
               ((MainActivity)c).replaceFragment(postlisthashtagfragment);
           }
       };

     }

    @Override
    public int getItemCount(){
        int size = hashtaglist.size();
        return size;
    }

    public class MyHashTagViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView hashtag;
        ItemClickListener itemClickListener;

        public MyHashTagViewHolder(@NonNull View itemView){
            super(itemView);

            hashtag = (TextView) itemView.findViewById(R.id.hashtag);
            itemView.setOnClickListener(this);}

        @Override
        public void onClick(View v){
            this.itemClickListener.onItemClickListener(v,getLayoutPosition());

        }
    }
}
