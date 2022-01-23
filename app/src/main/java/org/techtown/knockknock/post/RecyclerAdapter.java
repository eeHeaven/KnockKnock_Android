package org.techtown.knockknock.post;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.knockknock.R;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//Adapter : listview를 만들어서 recyclerview와 연결해줌 , Holder에서 만들어 준 listView를 inflater를 이용해 객체화 시키고 실제 데이터를 담아줌
//즉, Holder가 listview 그릇을 만들면 Adapter가 실제 데이터를 담은 listView를 만들어주는 것.
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

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
    View view = LayoutInflater.from(c).inflate(R.layout.listview_post,parent,false);
    return new MyViewHolder(view);
}

@Override
public void onBindViewHolder(@NonNull RecyclerAdapter.MyViewHolder holder, int position){
    holder.title.setText(postlist.get(position).getTitle());
    holder.date.setText(postlist.get(position).getDate());
    holder.content.setText(postlist.get(position).getContent());
}

    @Override
    public int getItemCount() {
    int size = postlist.size();
        return size;
    }

    //Holder: 레이아웃과 연결해서 listView를 만들어주는 역할 (단순 연결)
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView content;
        TextView date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = (TextView)itemView.findViewById(R.id.tv_postlist_title);
            content = (TextView)itemView.findViewById(R.id.tv_postlist_content);
            date = (TextView)itemView.findViewById(R.id.tv_postlist_timestamp);

        }
    }

}
