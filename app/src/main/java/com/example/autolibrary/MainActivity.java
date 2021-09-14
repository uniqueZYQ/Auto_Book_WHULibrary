package com.example.autolibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.autolibrary.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity{
    private String t_id="",t_pw="",t_seat="",t_seat_origin="",t_start="",t_end="",t_date="",cookie="",BookID="";
    private int room=0,start=0,end=0,hasPreference=0,init=0,date=0;
    private ActivityMainBinding binding;
    private HttpUtils httpUtils=new HttpUtils();

    private void initParam(){
        List<UserPreference> userPreferenceList = DataSupport.order("id desc").find(UserPreference.class);
        if(userPreferenceList.size()!=0){
            binding.editId.setText(userPreferenceList.get(0).getSub_id());
            binding.editPw.setText(userPreferenceList.get(0).getPw());
            binding.editSeat.setText(String.valueOf(userPreferenceList.get(0).getSeat()));
            hasPreference=1;
        }

        binding.spinStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                if(hasPreference==1&&init<4) {
                    parent.setSelection(userPreferenceList.get(0).getStart());
                    init++;
                }
                start=position;
                if(position==1)
                    t_start="now";
                else if(position==0)
                    t_start="";
                else
                    t_start=String.valueOf(420+30*position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){}
        });

        binding.spinEnd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                if(hasPreference==1&&init<4) {
                    parent.setSelection(userPreferenceList.get(0).getEnd());
                    init++;
                }
                end=position;
                if(position==0)
                    t_end="";
                else
                    t_end=String.valueOf(450+30*position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){}
        });

        binding.spinDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                if(hasPreference==1&&init<4) {
                    parent.setSelection(userPreferenceList.get(0).getDate());
                    init++;
                }
                date=position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){}
        });

        binding.spinRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                if(hasPreference==1&&init<4) {
                    parent.setSelection(userPreferenceList.get(0).getRoom());
                    init++;
                }
                room=position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){}
        });
    }

    private void initClick(){
        binding.buttonDoItNow.setOnClickListener(v->{
            AboutTime aboutTime=new AboutTime();
            Seat2ID seat2ID=new Seat2ID();
            t_id=binding.editId.getText().toString();
            t_pw=binding.editPw.getText().toString();
            t_seat=binding.editSeat.getText().toString();
            if(t_seat.equals("")){
                Toast.makeText(MainActivity.this, "请完善信息!", Toast.LENGTH_SHORT).show();
            }
            else {
                t_seat = seat2ID.getSeatID(room, Integer.decode(binding.editSeat.getText().toString()));
                if ((!t_id.equals("")) && (!t_pw.equals("")) && (!t_start.equals("")) && (!t_end.equals("")) && (!t_seat.equals("")) && (room != 0)&&(date!=0)) {
                    httpUtils.sendLoginRequest(t_id, t_pw, new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, "系统错误:step 1 work failed", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result = response.body().string();
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                if (jsonObject.getBoolean("status") == true) {
                                    cookie = response.headers().get("Set-Cookie").substring(11, 43);
                                    if(date==1)
                                        t_date=aboutTime.getToday();
                                    else
                                        t_date=aboutTime.getTomorrow();
                                    httpUtils.saveSeats(t_seat, t_date, t_start, t_end, cookie, new okhttp3.Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            e.printStackTrace();
                                            Looper.prepare();
                                            Toast.makeText(MainActivity.this, "step 2 work failed", Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            String result = response.body().string().replace("\\\"", "\"").substring(1);
                                            try {
                                                JSONObject jsonObject1 = new JSONObject(result);
                                                if (jsonObject1.getString("status").equals("success")) {
                                                    JSONObject jsonObject2 = new JSONObject(jsonObject1.getString("data"));
                                                    Looper.prepare();
                                                    Toast.makeText(MainActivity.this, "预约成功," + jsonObject2.getString("location") + " " + jsonObject2.getString("onDate") + jsonObject2.getString("begin") + "~" + jsonObject2.getString("end"), Toast.LENGTH_LONG).show();
                                                    httpUtils.getCurrentBook(cookie, new okhttp3.Callback() {
                                                        @Override
                                                        public void onFailure(Call call, IOException e) {
                                                            e.printStackTrace();
                                                            Looper.prepare();
                                                            Toast.makeText(MainActivity.this, "系统错误:step 3 work failed", Toast.LENGTH_SHORT).show();
                                                            Looper.loop();
                                                        }

                                                        @Override
                                                        public void onResponse(Call call, Response response) throws IOException {
                                                            String result = response.body().string();
                                                            if (result.indexOf("\"actualBegin\"") != -1) {
                                                                String currentBook = result.substring(result.indexOf("\"id\":"), result.indexOf("<input id =\"user\""));
                                                                BookID = currentBook.substring(5, 13);
                                                                String date = currentBook.substring(currentBook.indexOf("\"onDate\":\"") + 10, currentBook.indexOf("\",\"seatId\":"));
                                                                String location = currentBook.substring(currentBook.indexOf("\"location\":\"") + 12, currentBook.indexOf("\",\"begin\""));
                                                                String start_time = currentBook.substring(currentBook.indexOf("\"begin\":\"") + 9, currentBook.indexOf("\",\"end\""));
                                                                String end_time = currentBook.substring(currentBook.indexOf("\"end\":") + 7, currentBook.indexOf("\",\"actualBegin\""));
                                                                LoadCurrentBook(location, date, start_time, end_time);
                                                            } else {
                                                                LoadCurrentBook("", "", "", "");
                                                            }
                                                        }
                                                    });
                                                    Looper.loop();
                                                } else {
                                                    Looper.prepare();
                                                    Toast.makeText(MainActivity.this, jsonObject1.getString("message") + "!", Toast.LENGTH_SHORT).show();
                                                    httpUtils.getCurrentBook(cookie, new okhttp3.Callback() {
                                                        @Override
                                                        public void onFailure(Call call, IOException e) {
                                                            e.printStackTrace();
                                                            Looper.prepare();
                                                            Toast.makeText(MainActivity.this, "系统错误:step 3 work failed", Toast.LENGTH_SHORT).show();
                                                            Looper.loop();
                                                        }

                                                        @Override
                                                        public void onResponse(Call call, Response response) throws IOException {
                                                            String result = response.body().string();
                                                            if (result.indexOf("actualBegin") != -1) {
                                                                String currentBook = result.substring(result.indexOf("\"id\":"), result.indexOf("<input id =\"user\""));
                                                                BookID = currentBook.substring(5, 13);
                                                                String date = currentBook.substring(currentBook.indexOf("\"onDate\":\"") + 10, currentBook.indexOf("\",\"seatId\":"));
                                                                String location = currentBook.substring(currentBook.indexOf("\"location\":\"") + 12, currentBook.indexOf("\",\"begin\""));
                                                                String start_time = currentBook.substring(currentBook.indexOf("\"begin\":\"") + 9, currentBook.indexOf("\",\"end\""));
                                                                String end_time = currentBook.substring(currentBook.indexOf("\"end\":") + 7, currentBook.indexOf("\",\"actualBegin\""));
                                                                LoadCurrentBook(location, date, start_time, end_time);
                                                            } else {
                                                                LoadCurrentBook("", "", "", "");
                                                            }
                                                        }
                                                    });
                                                    Looper.loop();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                Looper.prepare();
                                                Toast.makeText(MainActivity.this, "系统错误:step 2 json parse failed", Toast.LENGTH_SHORT).show();
                                                Looper.loop();
                                            }
                                        }
                                    });
                                } else {
                                    Looper.prepare();
                                    Toast.makeText(MainActivity.this, jsonObject.getString("content"), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Looper.prepare();
                                Toast.makeText(MainActivity.this, "系统错误:step 1 json parse failed", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        }
                    });
                } else
                    Toast.makeText(MainActivity.this, "请完善信息!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.buttonDoItPlanned.setOnClickListener(v->{

        });

        binding.buttonCancelBook.setOnClickListener(v->{
            if(BookID.equals("")){
                Toast.makeText(MainActivity.this, "请先查询预约情况,再释放座位", Toast.LENGTH_SHORT).show();
            }
            else{
                httpUtils.CancelBook(BookID,cookie, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        Looper.prepare();
                        Toast.makeText(MainActivity.this, "系统错误:step 4 work failed", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string().replace("\\\"","\"").substring(1);
                        try {
                            JSONObject jsonObject1=new JSONObject(result);
                            if (jsonObject1.getString("status").equals("success")) {
                                JSONObject jsonObject2=new JSONObject(jsonObject1.getString("data"));
                                Looper.prepare();
                                Toast.makeText(MainActivity.this, "释放成功!", Toast.LENGTH_LONG).show();
                                httpUtils.getCurrentBook(cookie, new okhttp3.Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        e.printStackTrace();
                                        Looper.prepare();
                                        Toast.makeText(MainActivity.this, "系统错误:step 3 work failed", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        String result=response.body().string();
                                        if(result.indexOf("\"actualBegin\"")!=-1) {
                                            String currentBook = result.substring(result.indexOf("\"id\":"), result.indexOf("<input id =\"user\""));
                                            BookID = currentBook.substring(5, 13);
                                            String date = currentBook.substring(currentBook.indexOf("\"onDate\":\"") + 10, currentBook.indexOf("\",\"seatId\":"));
                                            String location = currentBook.substring(currentBook.indexOf("\"location\":\"") + 12, currentBook.indexOf("\",\"begin\""));
                                            String start_time = currentBook.substring(currentBook.indexOf("\"begin\":\"") + 9, currentBook.indexOf("\",\"end\""));
                                            String end_time = currentBook.substring(currentBook.indexOf("\"end\":") + 7, currentBook.indexOf("\",\"actualBegin\""));
                                            LoadCurrentBook(location, date, start_time, end_time);
                                        }
                                        else{
                                            LoadCurrentBook("","","","");
                                        }
                                    }
                                });
                                Looper.loop();
                            }
                            else{
                                Looper.prepare();
                                Toast.makeText(MainActivity.this, jsonObject1.getString("message")+"!", Toast.LENGTH_SHORT).show();
                                httpUtils.getCurrentBook(cookie, new okhttp3.Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        e.printStackTrace();
                                        Looper.prepare();
                                        Toast.makeText(MainActivity.this, "系统错误:step 3 work failed", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        String result=response.body().string();
                                        if(result.indexOf("\"actualBegin\"")!=-1) {
                                            String currentBook = result.substring(result.indexOf("\"id\":"), result.indexOf("<input id =\"user\""));
                                            BookID = currentBook.substring(5, 13);
                                            String date = currentBook.substring(currentBook.indexOf("\"onDate\":\"") + 10, currentBook.indexOf("\",\"seatId\":"));
                                            String location = currentBook.substring(currentBook.indexOf("\"location\":\"") + 12, currentBook.indexOf("\",\"begin\""));
                                            String start_time = currentBook.substring(currentBook.indexOf("\"begin\":\"") + 9, currentBook.indexOf("\",\"end\""));
                                            String end_time = currentBook.substring(currentBook.indexOf("\"end\":") + 7, currentBook.indexOf("\",\"actualBegin\""));
                                            LoadCurrentBook(location, date, start_time, end_time);
                                        }
                                        else{
                                            LoadCurrentBook("","","","");
                                        }
                                    }
                                });
                                Looper.loop();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, "系统错误:step 4 json parse failed", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                });
            }
        });

        binding.buttonSaveChoice.setOnClickListener(v->{
            t_id=binding.editId.getText().toString();
            t_pw=binding.editPw.getText().toString();
            t_seat_origin=binding.editSeat.getText().toString();
            if((!t_id.equals(""))&&(!t_pw.equals(""))&&(!t_start.equals(""))&&(!t_end.equals(""))&&(!t_seat_origin.equals(""))&&(room!=0)&&(date!=0)) {
                UserPreference userPreference = new UserPreference();
                userPreference.Save(t_id, t_pw, room, Integer.decode(t_seat_origin), start, end,date,MainActivity.this);
            }
            else
                Toast.makeText(MainActivity.this,"请完善信息!",Toast.LENGTH_SHORT).show();
        });
    }

    private void LoadCurrentBook(String location,String date,String start,String end){
        /*runOnUiThread(() ->{
            if(location.equals(""))
                binding.textCurrentBook.setText("地点:"+location+"\n日期:"+date+" 开始时间:"+start+" 结束时间:"+end));
        }*/
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!location.equals(""))
                    binding.textCurrentBook.setText("地点:"+location+"\n日期:"+date+" 开始时间:"+start+" 结束时间:"+end);
                else{
                    binding.textCurrentBook.setText("暂无成功的预约,请点击[现在抢]进行预约");
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding=com.example.autolibrary.databinding.ActivityMainBinding.inflate(getLayoutInflater());

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            Window window=getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS|WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        setContentView(binding.getRoot());

        initParam();

        initClick();
    }
}