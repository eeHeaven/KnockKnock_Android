package org.techtown.knockknock.post;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.knockknock.ItemClickListener;
import org.techtown.knockknock.R;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//Adapter : listview를 만들어서 recyclerview와 연결해줌 , Holder에서 만들어 준 listView를 inflater를 이용해 객체화 시키고 실제 데이터를 담아줌
//즉, Holder가 listview 그릇을 만들면 Adapter가 실제 데이터를 담은 listView를 만들어주는 것.
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>{

private Context c;
private List<PostData> postlist;

public RecyclerAdapter(Context c, List<PostData> postlist){
    this.c = c;
    this.postlist = postlist;
}

@NonNull
@Override
public RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){

    //inflater: xml을 객체화
    c = parent.getContext();
    LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.listview_post,parent,false);
    return new MyViewHolder(view);
//    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//    View itemView = inflater.inflate(R.layout.listview_post,parent,false);
//
//    return new RecyclerAdapter.MyViewHolder(itemView);
}

@Override
public void onBindViewHolder(@NonNull RecyclerAdapter.MyViewHolder holder, int position){
    holder.title.setText(postlist.get(position).getTitle());
    holder.date.setText(postlist.get(position).getDate());
    holder.content.setText(postlist.get(position).getContent());

    holder.itemClickListener = new ItemClickListener() {
        @Override
        public void onItemClickListener(View v, int position) {

            Long postId = postlist.get(position).getId();
            Intent intent = new Intent(v.getContext(),PostdetailActivity.class);
            intent.putExtra("postid",postId);
            v.getContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    };
}

    @Override
    public int getItemCount() {
    int size = postlist.size();
        return size;
    }

    //Holder: 레이아웃과 연결해서 listView를 만들어주는 역할 (단순 연결)
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title;
        TextView content;
        TextView date;

        ItemClickListener itemClickListener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = (TextView)itemView.findViewById(R.id.tv_postlist_title);
            content = (TextView)itemView.findViewById(R.id.tv_postlist_content);
            date = (TextView)itemView.findViewById(R.id.tv_postlist_timestamp);

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v){
            this.itemClickListener.onItemClickListener(v,getLayoutPosition());
        }
    }

}
