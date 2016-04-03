package com.carro.carrocrmapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by LENOVO Y500 on 23/3/2016.
 */
public class CallHistory extends RealmObject implements Parcelable {
    public static final String kTABLE = "callhistory";
    public static final String kCOL_PHONE_NUM = "phoneNumber";
    public static final String kCOL_DATE_TIME = "dateTime";
    public static final String kCOL_FILE_PATH = "filePath";

    String phoneNumber;
    Long dateTime;
    String filePath;

    public CallHistory() {
        // Normal actions performed by class, since this is still a normal object!
    }

    public int describeContents() {
        return 0;
    }

    /** save object in parcel */
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(phoneNumber);
        out.writeLong(dateTime);
        out.writeString(filePath);
    }

    public static final Parcelable.Creator<CallHistory> CREATOR
            = new Parcelable.Creator<CallHistory>() {
        public CallHistory createFromParcel(Parcel in) {
            return new CallHistory(in);
        }

        public CallHistory[] newArray(int size) {
            return new CallHistory[size];
        }
    };

    /** recreate object from parcel */
    private CallHistory(Parcel in) {
        phoneNumber = in.readString();
        dateTime = in.readLong();
        filePath = in.readString();
    }

    public CallHistory(JSONObject in) {
        try {
            phoneNumber = in.getString(kCOL_PHONE_NUM);
            dateTime = in.getLong(kCOL_DATE_TIME);
            filePath = in.getString(kCOL_FILE_PATH);
        } catch (JSONException e) {
            Log.d("myDebug", e.getMessage());
        }
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
