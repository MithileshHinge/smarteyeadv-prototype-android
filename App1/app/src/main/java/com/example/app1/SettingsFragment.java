package com.example.app1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.SwitchPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static int i = 1;
    public boolean switched;
    private static int msgPort = 6676;
    private String servername;
    public SharedPreferences spref_ip;
    final byte BYTE_NORMAL_MODE = 3, BYTE_SURVELLIANCE_MODE = 1 ,BYTE_START_EMAIL = 9 ,BYTE_STOP_EMAIL = 10;

    TextView username;
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        getActivity().setTitle("Settings");

        // Load the Preferences from the XML file
        addPreferencesFromResource(R.xml.settings_preferences);

        Preference p = getPreferenceScreen().findPreference("key2");

        if (p instanceof EditTextPreference) {
            SharedPreferences spref_ip = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor edit1 = spref_ip.edit();
            EditTextPreference editTextPref = (EditTextPreference) p;
            if (p.getTitle().toString().toLowerCase().contains("Device IP Address")) {
                p.setSummary("******");
            } else {
                p.setSummary(editTextPref.getText());
                if(i == 1){
                    edit1.putString("ip_address", "192.168.43.181");

                }else {
                    edit1.putString("ip_address", editTextPref.getText());
                    edit1.commit();
                }
            }
        }
        Preference p2 = getPreferenceScreen().findPreference("key4");
        SharedPreferences spref_mode = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor edit = spref_mode.edit();
        if(p2 instanceof ListPreference){
            ListPreference listPref = (ListPreference) p2;
            if(i == 1){
                ((ListPreference) p2).setValueIndex(0);
                i++;
            }
            p2.setSummary(listPref.getEntry());
            edit.putString("mode_type",listPref.getEntry().toString());
            edit.commit();
        }

        Preference p3 = getPreferenceScreen().findPreference("keyy1");

        if (p3 instanceof EditTextPreference) {
            SharedPreferences spref_user = PreferenceManager.getDefaultSharedPreferences(getContext());
            if (p3.getTitle().toString().toLowerCase().contains("Username")) {
                p3.setSummary(spref_user.getString("username",""));
            } else {
                p3.setSummary(spref_user.getString("username",""));
            }
        }

        Preference p4 = getPreferenceScreen().findPreference("keyy2");

        if (p4 instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p4;
            if (p4.getTitle().toString().toLowerCase().contains("Password")) {
                p4.setSummary("******");
            } else {
                p4.setSummary(editTextPref.getText());
            }
        }

        Preference p5 = getPreferenceScreen().findPreference("keyy3");

        if (p5 instanceof EditTextPreference) {
            SharedPreferences spref_email = PreferenceManager.getDefaultSharedPreferences(getContext());
            if (p5.getTitle().toString().toLowerCase().contains("Email Id")) {
                p5.setSummary(spref_email.getString("email_id",""));
            } else {
                p5.setSummary(spref_email.getString("email_id",""));
            }
        }


    }
    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);

        if (pref instanceof ListPreference) {
            SharedPreferences spref_mode = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor edit = spref_mode.edit();
            final ListPreference listPref = (ListPreference) pref;
            pref.setSummary(listPref.getEntry());
            edit.putString("mode_type",listPref.getEntry().toString());
            edit.commit();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        spref_ip = PreferenceManager.getDefaultSharedPreferences(getContext());
                        servername = spref_ip.getString("ip_address", "");
                        //System.out.println("Servername received    "+ servername);
                        Socket socket = new Socket(servername, msgPort);
                        OutputStream out = socket.getOutputStream();
                        InputStream in = socket.getInputStream();
                        if(listPref.getEntry().toString().equals("Normal Mode")) {
                            out.write(BYTE_NORMAL_MODE);
                        } else out.write(BYTE_SURVELLIANCE_MODE);
                        in.read();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        if (pref instanceof EditTextPreference) {
            SharedPreferences spref_ip = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor edit1 = spref_ip.edit();
            EditTextPreference editTextPref = (EditTextPreference) pref;
            if (pref.getTitle().toString().toLowerCase().contains("Device IP Address")) {
                pref.setSummary("******");
            } else {
                pref.setSummary(editTextPref.getText());
                edit1.putString("ip_address",editTextPref.getText());
                edit1.commit();
                if(switched){
                    Intent intent = new Intent();
                    intent.setAction(NotifyService.ACTION);
                    intent.putExtra("RQS", NotifyService.RQS_STOP_SERVICE);
                    getActivity().sendBroadcast(intent);
                    //Toast.makeText(getContext(), "Switch onnnn", Toast.LENGTH_SHORT).show();
                    getActivity().startService(new Intent(getActivity(),NotifyService.class));
                }
            }
        }
        /*if(key.equals("keyy1")) {
            System.out.println("...............username input");
            Preference p3 = getPreferenceScreen().findPreference("keyy1");
            SharedPreferences spref_user = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor edit2 = spref_user.edit();
            if (p3 instanceof EditTextPreference) {
                EditTextPreference editTextPref = (EditTextPreference) p3;
                if (p3.getTitle().toString().toLowerCase().contains("Username")) {
                    p3.setSummary("******");
                } else {
                    p3.setSummary(editTextPref.getText());
                    edit2.putString("username",editTextPref.getText());
                    edit2.commit();
                }
            }
        }

        if(key.equals("keyy2")) {
            Preference p4 = getPreferenceScreen().findPreference("keyy2");

            if (p4 instanceof EditTextPreference) {
                EditTextPreference editTextPref = (EditTextPreference) p4;
                if (p4.getTitle().toString().toLowerCase().contains("Password")) {
                    p4.setSummary("******");
                } else {
                    p4.setSummary(editTextPref.getText());
                }
            }
        }
        // To DO: send email change request to device and on mail
        if(key.equals("keyy3")) {
            Preference p5 = getPreferenceScreen().findPreference("keyy3");

            if (p5 instanceof EditTextPreference) {
                SharedPreferences spref_email = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor edit3 = spref_email.edit();
                EditTextPreference editTextPref = (EditTextPreference) p5;
                if (p5.getTitle().toString().toLowerCase().contains("Email Id")) {
                    p5.setSummary("******");
                } else {
                    p5.setSummary(editTextPref.getText());
                    edit3.putString("email_id",editTextPref.getText());
                    edit3.commit();
                }
            }

        }*/

        if(key.equals("key1")) {
            Preference p6 = getPreferenceScreen().findPreference("key1");

            if (p6 instanceof SwitchPreferenceCompat) {

                SwitchPreferenceCompat switchPreferenceCompat = (SwitchPreferenceCompat)p6;
                switched = (switchPreferenceCompat).isChecked();
                if(switched){
                    //Toast.makeText(getContext(), "Switch onnnn", Toast.LENGTH_SHORT).show();
                    getActivity().startService(new Intent(getActivity(),NotifyService.class));
                }else{
                    //Toast.makeText(getContext(), "Switch off", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setAction(NotifyService.ACTION);
                    intent.putExtra("RQS", NotifyService.RQS_STOP_SERVICE);
                    getActivity().sendBroadcast(intent);
                }
            }

        }

        if(key.equals("key5")){
            Preference p7 = getPreferenceScreen().findPreference("key5");

            if(p7 instanceof SwitchPreferenceCompat){

                SwitchPreferenceCompat switchPreferenceCompat = (SwitchPreferenceCompat) p7;
                switched = (switchPreferenceCompat).isChecked();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            spref_ip = PreferenceManager.getDefaultSharedPreferences(getContext());
                            servername = spref_ip.getString("ip_address", "");
                            System.out.println("Servername received    "+ servername);
                            Socket socket = new Socket(servername, msgPort);
                            OutputStream out = socket.getOutputStream();
                            InputStream in = socket.getInputStream();
                            if(switched){
                                out.write(BYTE_START_EMAIL);
                                //Toast.makeText(getContext(),"Email Notification On" , Toast.LENGTH_SHORT).show();
                            }else{
                                out.write(BYTE_STOP_EMAIL);
                                //Toast.makeText(getContext(),"Email Notification Off" , Toast.LENGTH_SHORT).show();
                            }
                            in.read();
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                if(switched)
                    Toast.makeText(getContext(),"Email Notification On" , Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(),"Email Notification Off", Toast.LENGTH_SHORT).show();
            }

        }


    }

}