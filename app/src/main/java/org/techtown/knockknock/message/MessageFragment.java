package org.techtown.knockknock.message;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.techtown.knockknock.MainActivity;
import org.techtown.knockknock.R;

public class MessageFragment extends Fragment {

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            //백버튼 숨기기
            ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            return inflater.inflate(R.layout.fragment_message, container, false);

        }

}