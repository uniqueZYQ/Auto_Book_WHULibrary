package com.example.autolibrary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Spinner;

@SuppressLint("AppCompatCustomView")
public class NewSpinner extends Spinner{
    private int lastPosition=0;

    public NewSpinner(Context context) {
        super(context);
    }

    @Override
    public void setSelection(int position, boolean animate){
        super.setSelection(position,animate);
        if(position==lastPosition){
            if(getOnItemSelectedListener()!=null){
                getOnItemSelectedListener().onItemSelected(this,getChildAt(position),position,0);
            }
        }
        lastPosition=position;
    }

    @Override
    public void setSelection(int position){
        super.setSelection(position);
        if(position==lastPosition){
            if(getOnItemSelectedListener()!=null){
                getOnItemSelectedListener().onItemSelected(this,getChildAt(position),position,0);
            }
        }
        lastPosition=position;
    }
}
