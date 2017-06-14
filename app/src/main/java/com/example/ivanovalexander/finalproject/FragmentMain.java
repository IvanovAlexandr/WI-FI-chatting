package com.example.ivanovalexander.finalproject;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class FragmentMain extends Fragment implements View.OnClickListener {

    Button buttonAdvertising;
    Button buttonDiscovering;
    Button buttonChat;
    Button buttonSettings;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        buttonAdvertising = (Button) rootView.findViewById(R.id.button_advertising);
        buttonDiscovering = (Button) rootView.findViewById(R.id.button_discovering);
        buttonChat = (Button) rootView.findViewById(R.id.button_chat);
        buttonSettings = (Button) rootView.findViewById(R.id.button_settings);
        View.OnTouchListener onTouchListener =  new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int buttonIndex = translateIdToIndex(v.getId());

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundResource(R.drawable.button_pressed);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.setBackgroundResource(R.drawable.button);
                    OnSelectedButtonListener listener = (OnSelectedButtonListener) getActivity();
                    listener.onButtonSelected(buttonIndex);
                    return true;
                }

                return false;
            }
        };


        buttonAdvertising.setOnTouchListener(onTouchListener);
        buttonDiscovering.setOnTouchListener(onTouchListener);
        buttonChat.setOnTouchListener(onTouchListener);
        buttonSettings.setOnTouchListener(onTouchListener);

        return rootView;
    }

    public interface OnSelectedButtonListener {
        void onButtonSelected(int buttonIndex);
    }

    @Override
    public void onClick(View v) {
        int buttonIndex = translateIdToIndex(v.getId());
        OnSelectedButtonListener listener = (OnSelectedButtonListener) getActivity();
        listener.onButtonSelected(buttonIndex);
    }

    int translateIdToIndex(int id) {
        int index = -1;
        switch (id) {
            case R.id.button_advertising:
                index = 1;
                break;
            case R.id.button_discovering:
                index = 2;
                break;
            case R.id.button_chat:
                index = 3;
                break;
            case R.id.button_settings:
                index = 4;
                break;
        }
        return index;
    }




}
