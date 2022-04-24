package org.techtown.knockknock.message;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.techtown.knockknock.HomeFragment;
import org.techtown.knockknock.MainActivity;
import org.techtown.knockknock.R;
import org.techtown.knockknock.location.LocationChangeFragment;

public class MessageFragment extends Fragment {

    ImageView sendBtn;

    public static MessageFragment newInstance() {

        Bundle args = new Bundle();

        MessageFragment fragment = new MessageFragment();
        fragment.setArguments(args);
        return fragment;
    }
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            //백버튼 숨기기
            ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            View mView = inflater.inflate(R.layout.fragment_message, null);

            sendBtn = (ImageView) mView.findViewById(R.id.btn_message_send);
            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) getActivity()).replaceFragment(new MessageSendFragment());
                }
            });
            return mView;


        }

}