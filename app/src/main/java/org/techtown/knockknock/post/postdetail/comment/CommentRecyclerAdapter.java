package org.techtown.knockknock.post.postdetail.comment;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.techtown.knockknock.ErrorBody;
import org.techtown.knockknock.R;
import org.techtown.knockknock.RetrofitClient;
import org.techtown.knockknock.post.postdetail.PostdetailFragment;
import org.techtown.knockknock.post.PostAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.MyViewHolder2>{

    private Context c;
    private List<CommentData> commentlist;
    private PostdetailFragment fragment;
    private static String userId;

    public CommentRecyclerAdapter(Context c, List<CommentData> commentlist,PostdetailFragment fragment){
        this.c = c;
        this.commentlist = commentlist;
        this.fragment = fragment;
    }



    @NonNull
    @Override
    public CommentRecyclerAdapter.MyViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType){

        View view = LayoutInflater.from(c).inflate(R.layout.listview_comment,parent,false);
        return new MyViewHolder2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentRecyclerAdapter.MyViewHolder2 holder, int position){

        holder.writer.setText(commentlist.get(position).getCommentWriterNickname());
        holder.content.setText(commentlist.get(position).getCommentContent());
        holder.timestamp.setText(commentlist.get(position).getCommentedTime());
        Log.d("CommentAdpater",position+"번째 댓글 "+commentlist.get(position).getCommentContent() +" load 완료");

        //로그인 계정과 댓글 작성자가 일치하는지 확인 후 일치하면 삭제 버튼 활성화
        if(commentlist.get(position).getCommentWriterId().equals(userId)){
            holder.btn_deletecomment.setVisibility(View.VISIBLE);
        holder.btn_deletecomment.setTag(commentlist.get(position).getCommentId());
        holder.btn_deletecomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                Long commentid = (Long) view.getTag();
                PostAPI postAPI = RetrofitClient.getInstance().create(PostAPI.class);
                Call<Void> call = postAPI.deleteComment(commentid);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(c.getApplicationContext(), "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            commentlist.remove(pos);
                            notifyItemRemoved(pos);
                        }
                        else{
                            Toast.makeText(c.getApplicationContext(), "댓글 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            ErrorBody error = new Gson().fromJson(response.errorBody().charStream(),ErrorBody.class);
                            Log.d("CommentRecyclerAdapter",error.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(c.getApplicationContext(), "댓글 삭제 연결 실패", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }}
    @Override
    public int getItemCount(){
        int size = commentlist.size();
        return size;
    }

    public class MyViewHolder2 extends RecyclerView.ViewHolder{
        TextView writer;
        TextView timestamp;
        TextView content;
        Button btn_deletecomment;

        public MyViewHolder2(@NonNull View itemView){
            super(itemView);

            writer = (TextView) itemView.findViewById(R.id.tv_comment_nickname);
            timestamp = (TextView) itemView.findViewById(R.id.tv_comment_timestamp);
            content = (TextView)itemView.findViewById(R.id.tv_comment_content);
            btn_deletecomment = (Button)itemView.findViewById(R.id.btn_deletecomment);

            //현재 로그인되어있는 user의 id 갖고 오기
            SharedPreferences sharedPreferences = itemView.getContext().getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
            userId = sharedPreferences.getString("userId","");
        }
    }
}
