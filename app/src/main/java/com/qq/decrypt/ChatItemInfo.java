package com.qq.decrypt;

/**
 * Created by yanchen on 17-11-28.
 */

public class ChatItemInfo {
    public int isSend;
    public  String talker;
    public  long chatTime;
    public String chatContent;

    @Override
    public String toString() {
        return "talker:"+talker+",chatContent:"+chatContent+",isSend:"+chatContent+",chatTime:"+chatTime;
    }
}
