package com.example.autolibrary;

public class Seat2ID {
    public String getSeatID(int roomID,int seat){
        if(roomID==0)//null
            return "";
        else if(roomID==1) {//E3
            if (seat <= 2)
                return String.valueOf(73824 + seat);
            else
                return String.valueOf(73826 + seat);
        }
        else if(roomID==2){//E4
            switch (seat%6){
                case 0:
                    return String.valueOf(54076+seat/6+(seat/6-1)/2);
                default:
                    return String.valueOf(53892+seat/6+seat/12+(seat%6-1)*19);
            }
        }
        else if(roomID==3) {//E5
            switch (seat % 6) {
                case 0:
                    return String.valueOf(54230 + seat / 6 + (seat / 6 - 1) / 2);
                default:
                    return String.valueOf(54136 + seat / 6 + seat / 12 + (seat % 6 - 1) * 19);
            }
        }
        else{//E6
            switch (seat % 6) {
                case 0:
                    return String.valueOf(53942 + seat / 6 + (seat / 6 - 1) / 2);
                default:
                    return String.valueOf(53848 + seat / 6 + seat / 12 + (seat % 6 - 1) * 19);
            }
        }
    }
}
