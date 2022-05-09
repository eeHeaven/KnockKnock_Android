package org.techtown.knockknock.post.postlist;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.knockknock.ItemClickListener;
import org.techtown.knockknock.MainActivity;
import org.techtown.knockknock.R;
import org.techtown.knockknock.post.postdetail.PostdetailFragment;

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
            //detail post로 이동하기 위해 post id를 postdetail fragment로 넘기고 실행
            Long postId = postlist.get(position).getId();
            Bundle bundle = new Bundle(); // 번들에 post id 담기
            bundle.putLong("postId",postId);

            //넘어갈 postdetail fragment
            PostdetailFragment postdetailFragment = new PostdetailFragment();
            postdetailFragment.setArguments(bundle); // 번들과 같이 넘기기
            ((MainActivity) c).replaceFragment(postdetailFragment);

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

            title = (TextView)itemView.findViewById(R.id.tv_chatroomlist_partner);
            content = (TextView)itemView.findViewById(R.id.tv_chatroomlist_message);
            date = (TextView)itemView.findViewById(R.id.tv_chatroomlist_timestamp);

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v){
            this.itemClickListener.onItemClickListener(v,getLayoutPosition());
        }
    }

    //검색할 때 조건 해당하면 해당하는 게시글 목록만 보여주기
    public void filterList(List<PostData> filteredList){
    postlist = filteredList;
    notifyDataSetChanged();
    }

}
