package com.example.app1;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Sibhali on 7/30/2017.
 */
public class Database {

    int _id;
    public static String _name;
    //String _phone_number;
    public static String _date;

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    public static Date date = new Date();

    // Empty constructor
    public Database(){

    }
    // constructor
    public Database(int id, String name, String _date){
        this._id = id;
        this._name = name;
        this._date = _date;
        //this._phone_number = _phone_number;
    }

    // constructor
    public Database(String name, String _date){
        this._name = name;
        //this._phone_number = _phone_number;
        this._date= _date;
    }
    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    // getting name
    public String getName(){
        return this._name;
    }

    // setting name
    public void setName(String name){
        this._name = name;
    }

    // getting phone number
    /*public String getPhoneNumber(){
        return this._phone_number;
    }*/

    // setting phone number
   /* public void setPhoneNumber(String phone_number){
        this._phone_number = phone_number;
    }*/

    public String getDateTime() {
        return _date;
    }

    public void setDateTime(String dateTime){
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
       // Date date = new Date();
        this._date = dateTime;}
}
