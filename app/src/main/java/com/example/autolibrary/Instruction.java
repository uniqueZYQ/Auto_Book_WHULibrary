package com.example.autolibrary;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;

public class Instruction {
    private Dialog dialog;

    public void initInstruction(Context context){
        FrameLayout root=(FrameLayout) LayoutInflater.from(context).inflate(R.layout.instruction,null);
        //初始化视图
        dialog=new Dialog(context,R.style.dialog);
        dialog.setContentView(root);
        Window dialogWindow=dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        Button close=root.findViewById(R.id.button_close);
        close.setOnClickListener(v-> dialog.cancel());
        dialog.show();
    }
}
