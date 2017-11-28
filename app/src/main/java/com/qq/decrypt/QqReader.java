package com.qq.decrypt;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.qq.decrypt.lib.QqDecryptUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yanchen on 17-11-28.
 */

public class QqReader {
    private static final String TAG = "QqReader";
    public static int TYPE_FRIEND = 1;
    public static int TYPE_GROUP = 2;


    public void readAllChatHistory() {
        String qqDbFile = "/sdcard/qq.db";
        SQLiteDatabase db = null;

        try {
            db = SQLiteDatabase.openOrCreateDatabase(qqDbFile, null);
            //读取所有群组聊天内容
            ArrayList<ChatItemInfo> groupItems = queryDbChatHistoryByType(db, TYPE_GROUP);
            if (groupItems != null && groupItems.size() > 0) {
                for (ChatItemInfo info : groupItems) {
                    Log.e(TAG, info.toString());
                }
            }

            //读取所有好友聊天内容
            ArrayList<ChatItemInfo> friendItems = queryDbChatHistoryByType(db, TYPE_FRIEND);
            if (friendItems != null && friendItems.size() > 0) {
                for (ChatItemInfo info : friendItems) {
                    Log.e(TAG, info.toString());
                }
            }

        } catch (Throwable e) {
            Log.e(TAG, "readAllChatHistory exceptiopn:" + e);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }


    private ArrayList<ChatItemInfo> queryDbChatHistoryByType(SQLiteDatabase db, int type) {
        ArrayList<ChatItemInfo> allChatItems = new ArrayList<>();
        List<String> tables = getAllTables(db, type);
        if (tables == null || tables.size() <= 0) {
            return allChatItems;
        }

        for (String tableName : tables) {
            ArrayList<ChatItemInfo> chatItems = queryTableChatHistory(db, tableName, type);
            if (chatItems == null || chatItems.size() <= 0) {
                continue;
            }
            allChatItems.addAll(chatItems);
        }
        return allChatItems;

    }


    private ArrayList<ChatItemInfo> queryTableChatHistory(SQLiteDatabase db, String tableName, int type) {
        ArrayList<ChatItemInfo> chatItems = new ArrayList<>();
        try {
            String sql = null;
            if (type == TYPE_FRIEND) {
                sql = "select friendTable._id,friendTable.time," +
                        "friendTable.issend,friendTable.msgData," +
                        "Friends.remark,Friends.name,Friends.alias" +
                        " from " + tableName + " friendTable join Friends on friendTable.senderuin = Friends.uin where friendTable.msgtype=-1000";
            } else {
                sql = "select groupTable._id,groupTable.time,groupTable.issend,groupTable.msgData " +
                        ",groupTable.issend,TroopMemberInfo.friendnick,TroopMemberInfo.autoremark,TroopMemberInfo.troopnick,TroopMemberInfo.alias " +
                        " from " + tableName + " groupTable INNER join TroopMemberInfo  on TroopMemberInfo.memberuin = groupTable.senderuin " +
                        " and  TroopMemberInfo.troopuin = groupTable.frienduin where groupTable.msgtype=-1000";
            }

            Cursor cursor = db.rawQuery(sql, null);


            while (cursor.moveToNext()) {
                String content = QqDecryptUtil.decryptBytesToStr(cursor.getBlob(cursor.getColumnIndex("msgData")));
                int isSend = cursor.getInt(cursor.getColumnIndex("issend"));
                long chatTime = cursor.getLong(cursor.getColumnIndex("time"));

                String talker = null;


                if (type == TYPE_FRIEND) {
                    talker = QqDecryptUtil.decryptString(cursor.getString(cursor.getColumnIndex("remark")));
                    if (TextUtils.isEmpty(talker)) {
                        talker = QqDecryptUtil.decryptString(cursor.getString(cursor.getColumnIndex("name")));
                    }
                    if (TextUtils.isEmpty(talker)) {
                        talker = QqDecryptUtil.decryptBytesToStr(cursor.getBlob(cursor.getColumnIndex("alias")));
                    }

                    if (TextUtils.isEmpty(talker) || TextUtils.isEmpty(content)) {
                        continue;
                    }
                } else {
                    talker = QqDecryptUtil.decryptString(cursor.getString(cursor.getColumnIndex("troopnick")));
                    if (TextUtils.isEmpty(talker)) {
                        talker = QqDecryptUtil.decryptString(cursor.getString(cursor.getColumnIndex("autoremark")));
                    }
                    if (TextUtils.isEmpty(talker)) {
                        talker = QqDecryptUtil.decryptString(cursor.getString(cursor.getColumnIndex("friendnick")));
                    }
                    if (TextUtils.isEmpty(talker)) {
                        talker = QqDecryptUtil.decryptString(cursor.getString(cursor.getColumnIndex("alias")));
                    }


                    if (TextUtils.isEmpty(talker) || TextUtils.isEmpty(content)) {
                        continue;
                    }
                }


                ChatItemInfo info = new ChatItemInfo();
                info.chatContent = content;
                info.chatTime = chatTime;
                info.talker = talker;
                info.isSend = isSend;

                chatItems.add(info);
            }

            if (cursor != null) {
                cursor.close();
            }

        } catch (Exception e) {
            Log.d(TAG, "queryTableChatHistory exceptiopn:" + e);
            e.printStackTrace();
        }
        return chatItems;
    }

    private List<String> getAllTables(SQLiteDatabase db, int type) {
        String sql = null;
        if (type == TYPE_FRIEND) {
            sql = "select * from sqlite_master where name like 'mr_friend_%New'";
        } else {
            sql = "select * from sqlite_master where name like 'mr_troop_%New'";
        }
        List<String> list = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor == null) {
            return list;
        }
        while (cursor.moveToNext()) {
            String table = cursor.getString(cursor.getColumnIndex("name"));
            if (!TextUtils.isEmpty(table)) {
                list.add(table);
            }
        }
        cursor.close();
        return list;
    }
}
