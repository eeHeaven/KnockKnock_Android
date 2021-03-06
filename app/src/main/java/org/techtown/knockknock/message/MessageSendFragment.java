package org.techtown.knockknock.message;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.techtown.knockknock.MainActivity;
import org.techtown.knockknock.R;
import org.techtown.knockknock.RetrofitClient;
import org.techtown.knockknock.location.GeoQuery;
import org.techtown.knockknock.location.LocationChangeFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageSendFragment extends Fragment {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    static String id;

    Button setlocation;
    private Double targetlatitude;
    private Double targetlongitude;

    TextView messageinfo;
    TextView mylocation;
    EditText messagecontent;
    ImageView sendMessage;

    Geocoder geocoder;
    Context context;

    public static MessageSendFragment newInstance() {

        Bundle args = new Bundle();

        MessageSendFragment fragment = new MessageSendFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //????????? ?????????
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = container.getContext();
        View mView = inflater.inflate(R.layout.fragment_message_send, null);
        geocoder = new Geocoder(this.getActivity());
        setlocation = (Button) mView.findViewById(R.id.btn_messagesend_locationchange);
        messageinfo = (TextView) mView.findViewById(R.id.messagesend_tv_messageinfo);
        mylocation = (TextView) mView.findViewById(R.id.messagesend_tv_mylocation);
        sendMessage = (ImageView) mView.findViewById(R.id.btn_messagesend_send);
        messagecontent = (EditText) mView.findViewById(R.id.tv_messagesend_content);

        //?????? ?????? ????????????
        if (getArguments() != null) { //?????? ???????????? ?????? ?????? ????????? ?????????
            targetlatitude = getArguments().getDouble("latitude");
            targetlongitude = getArguments().getDouble("longitude");
            messageinfo.setText("????????? ?????? ?????? 1km ?????? ??????????????? ???????????? ???????????????.");
        } else {
            //sharedPreference?????? ????????? ?????? ????????????
            SharedPreferences locationpreferences = this.getActivity().getSharedPreferences("Location", MODE_PRIVATE);
            targetlatitude = Double.valueOf(locationpreferences.getFloat("latitude", 0));
            targetlongitude = Double.valueOf(locationpreferences.getFloat("longitude", 0));
            Log.d("MessageSend", "lat : " + targetlatitude + " lon : " + targetlongitude);
        }

        //sharedPreference?????? ????????? ???????????? ????????? ????????????
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        String nickname = sharedPreferences.getString("nickname", "");
        id = sharedPreferences.getString("userId", "");

        //Geocoder??? ??? ?????? ????????? ??????
        String location = "???????????? ???????????? ?????? ????????? ????????????.";
        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(targetlatitude, targetlongitude, 10);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MypageFragment", "Geocoder ????????? ??????");
        }
        if (list != null) {
            if (list.size() == 0) location = "???????????? ???????????? ?????? ????????? ????????????.";
            else location = list.get(0).getAddressLine(0);
        }
        mylocation.setText(location);

        //?????? ????????????
        setlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                //home fragment???????????? ???????????? ??????
                bundle.putChar("from", 'm');

                LocationChangeFragment fragment = new LocationChangeFragment();
                fragment.setArguments(bundle);
                ((MainActivity) getActivity()).replaceFragment(fragment);
            }
        });
        //????????? ?????????
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messagecontent.getText().toString();
                if (message.length() == 0)
                    Toast.makeText(context, "????????? ???????????? ????????????.", Toast.LENGTH_LONG).show();
                else {
                    try {
                        GeoQuery.findUserIdsNear(targetlatitude, targetlongitude, new GeoQuery.FindUserCallback() {
                            @Override
                            public void onSuccess(List<String> userIds) {
                                userIds.remove(id);

                                if (userIds.size() == 0) {
                                    Toast.makeText(context, "???????????? ?????? ?????? ?????? ?????? ????????? ????????????.", Toast.LENGTH_LONG).show();
                                } else {
                                    sendDialog(userIds,message);
                               }
                            }

                            @Override
                            public void onFail() {

                            }
                        });
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                       e.printStackTrace();
                    }
                }
            }


        });

        return mView;

    }

    private void sendDialog(List<String> targetUsers,String messageContext) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("????????? ????????????");
        builder.setMessage("?????? ?????? 1km ????????? ?????? " + targetUsers.size() + "????????? ???????????? ??????????????????????");
        builder.setNegativeButton("???", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendMessageAPIcall(targetUsers,messageContext);
            }
        });
        builder.setPositiveButton("?????????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "????????? ????????? ??????????????????.", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    private void sendMessageAPIcall(List<String> targetUsers, String message){
        MessageAPI messageAPI = RetrofitClient.getInstance().create(MessageAPI.class);
        for(String targetUser: targetUsers){
            MessageSendRequest request = new MessageSendRequest(id,targetUser,message);
            Call<MessageResponse> call = messageAPI.sendMessage(request);
            call.enqueue(new Callback<MessageResponse>() {
                @Override
                public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                    Log.d("sendMessageAPIcall",targetUser+" ?????? ????????? ?????? ??????");
                }

                @Override
                public void onFailure(Call<MessageResponse> call, Throwable t) {
                    Log.d("sendMessageAPIcall",t.getMessage());
                }
            });
        }
        Toast.makeText(context,targetUsers.size()+"?????? ???????????? ????????? ????????? ?????????????????????.",Toast.LENGTH_LONG).show();

    }


}