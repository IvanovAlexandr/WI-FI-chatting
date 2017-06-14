package com.example.ivanovalexander.finalproject;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



public class FragmentChat extends Fragment implements View.OnClickListener {

    View rootView;
    EditText mEditMessage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        mEditMessage = (EditText) rootView.findViewById(R.id.editMessage);
        Button buttonSend = (Button) rootView.findViewById(R.id.button_send);
        buttonSend.setOnClickListener(this);

        return rootView;
    }

    private void createMessageView(String name, String msg, String id){
        LinearLayout chatLayout = (LinearLayout) rootView.findViewById(R.id.chat);

        LinearLayout allMsgLayout = new LinearLayout(getActivity());
        allMsgLayout.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams allMsgLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        allMsgLayout.setLayoutParams(allMsgLayoutParams);
        allMsgLayout.setBackgroundResource(R.drawable.msg);
        allMsgLayout.setPadding(10,10,10,10);

        TextView nameView = new TextView(getActivity());
        nameView.setText(name);
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        nameView.setPadding(10,0,0,0);
        ViewGroup.LayoutParams textViewLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        nameView.setLayoutParams(textViewLayoutParams);

        allMsgLayout.addView(nameView);

        LinearLayout messageLayout = new LinearLayout(getActivity());
        ViewGroup.LayoutParams messageLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        messageLayout.setLayoutParams(messageLayoutParams);


        ImageView iconView = new ImageView(getActivity());
        iconView.setImageResource(choiceIcon(id));
        LinearLayout.LayoutParams iconViewLayoutParams = new LinearLayout.LayoutParams(0, 76, 0.2f );
        iconView.setLayoutParams(iconViewLayoutParams);

        messageLayout.addView(iconView);

        TextView messageTextView = new TextView(getActivity());
        messageTextView.setText(msg);
        messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        LinearLayout.LayoutParams messageTextViewLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.8f);
        messageTextView.setLayoutParams(messageTextViewLayoutParams);

        messageLayout.addView(messageTextView);

        allMsgLayout.addView(messageLayout);

        chatLayout.addView(allMsgLayout);
    }

    public void displayMessage(byte[] bytes){
        String msg = new String(bytes);
        String iconId = msg.substring(msg.lastIndexOf(" ")+1);
        msg = msg.substring(0,msg.lastIndexOf(" "));
        String name = msg.substring(msg.lastIndexOf(" ")+1);
        msg = msg.substring(0,msg.lastIndexOf(" "));
        createMessageView(name, msg, iconId);

    }

    public String sendMessage(){
        String msg = mEditMessage.getText().toString();
        mEditMessage.setText(null);
        return msg;

    }

    public interface OnSelectedButtonListener {
        void onButtonSend();
    }

    @Override
    public void onClick(View v) {
        OnSelectedButtonListener listener = (OnSelectedButtonListener) getActivity();
        listener.onButtonSend();
    }

    private int choiceIcon(String id) {
        switch (id) {
            case "1":
                return R.drawable.icon1;
            case "2":
                return R.drawable.icon2;
            case "3":
                return R.drawable.icon3;
            case "4":
                return R.drawable.icon4;
            case "5":
                return R.drawable.icon5;
            case "6":
                return R.drawable.icon6;
            case "7":
                return R.drawable.icon7;
            case "8":
                return R.drawable.icon8;
            case "9":
                return R.drawable.icon9;
            case "10":
                return R.drawable.icon10;
            case "11":
                return R.drawable.icon11;
            case "12":
                return R.drawable.icon12;
            case "13":
                return R.drawable.icon13;
            case "14":
                return R.drawable.icon14;
            case "15":
                return R.drawable.icon15;
            case "16":
                return R.drawable.icon16;
            case "17":
                return R.drawable.icon17;
            case "18":
                return R.drawable.icon18;
            case "19":
                return R.drawable.icon19;
            case "20":
                return R.drawable.icon20;
            case "21":
                return R.drawable.icon21;
            case "22":
                return R.drawable.icon22;
            case "23":
                return R.drawable.icon23;
            case "24":
                return R.drawable.icon24;
            case "25":
                return R.drawable.icon25;
            case "26":
                return R.drawable.icon26;

        }
        return R.drawable.icon1;
    }


}
