package com.indream.lazystu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConnectivityReceiver extends BroadcastReceiver {
    private SharedPreferences sharedPref;

    @Override
    public void onReceive( Context context, Intent intent ) {
        handleWiFi(context);
    }

    private void setStatus ( Context context, String state ){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(R.string.connecting), state);
        editor.apply();
    }

    private int getWiFiStatus ( Context context ) {
        String url = "http://202.40.221.20/generate_204";
        HttpClient httpclient = MySSLSocketFactory.getNewHttpClient();
        HttpGet httpget = new HttpGet(url);
        int statusCode = 0;
        try {
            HttpResponse response = httpclient.execute(httpget);
            statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 204) {
                Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
            }
        } catch (ClientProtocolException e) {
            if(e.getMessage() != null) {
                Toast.makeText(context, "Protocol Err: [" + e.getMessage() + "]", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            if(e.getMessage() != null) {
                Toast.makeText(context, "IO Err: [" + e.getMessage() + "]", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            if(e.getMessage() != null) {
                Toast.makeText(context, "HttpPost Err: [" + e.getMessage()+"]", Toast.LENGTH_LONG).show();
            }
        }
        return statusCode;
    }

    public void handleWiFi( Context context ) {
        String pref = context.getString(R.string.ergwave_pref);
        sharedPref = context.getSharedPreferences(pref, Context.MODE_PRIVATE);

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo info = wifiManager.getConnectionInfo();
            if (info != null) {
                String ssid = info.getSSID();
                String ergwave = context.getString(R.string.ssid_ergwave);
                String cuhk = context.getString(R.string.ssid_cuhk);
                String wifihk = context.getString(R.string.ssid_wifihk);
                Boolean isERGWAVE = ssid.equals("\"" + ergwave + "\"");
                Boolean isCUHK = ssid.equals("\"" + cuhk + "\"");
                Boolean isWiFiHK = ssid.startsWith("\"" + wifihk);
//                Toast.makeText(context, ssid + " " + ergwave + " " + isERGWAVE, Toast.LENGTH_SHORT).show();

//                String connecting = sharedPref.getString(context.getString(R.string.connecting), "no");
                if (isERGWAVE) {
                    if (getWiFiStatus(context) == 204) {
                        return;
                    }
                    Toast.makeText(context, "Connecting ERGWAVE", Toast.LENGTH_SHORT).show();
//                    setStatus(context, "yes");
                    connectERGWAVE(context);
                } else if (isCUHK) {
                    if (getWiFiStatus(context) == 204) {
                        return;
                    }
                    Toast.makeText(context, "Connecting CUHK", Toast.LENGTH_SHORT).show();
                    connectCUHK(context);
                } else if (isWiFiHK) {
                    if (getWiFiStatus(context) == 204) {
                        return;
                    }
                    Toast.makeText(context, "Connecting Wi-Fi.HK", Toast.LENGTH_SHORT).show();
                    connectWiFiHK(context);
                }
            } else {
                setStatus(context, "no");
            }
        }
    }

    public void connectERGWAVE (Context context) {
        String username = sharedPref.getString(context.getString(R.string.username), "");
        String password = sharedPref.getString(context.getString(R.string.password), "");
        String fqdn = sharedPref.getString(context.getString(R.string.fqdn), "");
//        Toast.makeText(context, username + " " + password + " " + fqdn, Toast.LENGTH_LONG).show();
//        try {
//            username = SimpleCrypto.decrypt(username);
//            password = SimpleCrypto.decrypt(password);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        HttpClient httpclient = MySSLSocketFactory.getNewHttpClient();
        HttpPost httppost = new HttpPost("https://securelogin.arubanetworks.com/cgi-bin/login");

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
            nameValuePairs.add(new BasicNameValuePair("user", username));
            nameValuePairs.add(new BasicNameValuePair("password", password));
            nameValuePairs.add(new BasicNameValuePair("fqdn", fqdn));
            nameValuePairs.add(new BasicNameValuePair("cmd", "authenticate"));
            nameValuePairs.add(new BasicNameValuePair("Login", "Log In"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpclient.execute(httppost);
            int statusCode = response.getStatusLine().getStatusCode();
            Toast.makeText(context, "status: " + statusCode, Toast.LENGTH_LONG).show();
            if(statusCode == 200){
                setStatus(context, "connected");
            }
            Log.d("lazystu", "connectERGWAVE: " + statusCode);
//            HttpEntity entity = response.getEntity();

        } catch (ClientProtocolException e) {
            if(e.getMessage() != null) {
                Toast.makeText(context, "Protocol Err: [" + e.getMessage() + "]", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            if(e.getMessage() != null) {
                Toast.makeText(context, "IO Err: [" + e.getMessage() + "]", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            if(e.getMessage() != null) {
                Toast.makeText(context, "HttpPost Err: [" + e.getMessage()+"]", Toast.LENGTH_LONG).show();
            }
        }
        setStatus(context, "no");
    }

    public void connectCUHK (Context context) {
        String username = "s" + sharedPref.getString(context.getString(R.string.sid), "");
        String password = sharedPref.getString(context.getString(R.string.cwem), "");

        HttpClient httpclient = MySSLSocketFactory.getNewHttpClient();
        HttpPost httppost = new HttpPost("https://securelogin.wlan.cuhk.edu.hk/cgi-bin/login");

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
            nameValuePairs.add(new BasicNameValuePair("user", username));
            nameValuePairs.add(new BasicNameValuePair("password", password));
            nameValuePairs.add(new BasicNameValuePair("cmd", "authenticate"));
            nameValuePairs.add(new BasicNameValuePair("Login", "Log In"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpclient.execute(httppost);
            int statusCode = response.getStatusLine().getStatusCode();
            Toast.makeText(context, "status: " + statusCode, Toast.LENGTH_LONG).show();
            if(statusCode == 200){
                setStatus(context, "connected");
            }
            Log.d("lazystu", "connectCUHK: " + statusCode);

        } catch (ClientProtocolException e) {
            if(e.getMessage() != null) {
                Toast.makeText(context, "Protocol Err: [" + e.getMessage() + "]", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            if(e.getMessage() != null) {
                Toast.makeText(context, "IO Err: [" + e.getMessage() + "]", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            if(e.getMessage() != null) {
                Toast.makeText(context, "HttpPost Err: [" + e.getMessage()+"]", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void connectWiFiHK (Context context) {
        HttpClient httpclient = MySSLSocketFactory.getNewHttpClient();
        HttpPost httppost = new HttpPost("https://wi-fi.cuhk.edu.hk/guest/wifihklogin.php?cmd=login");

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("username", "anonymous"));
            nameValuePairs.add(new BasicNameValuePair("password", "anonymous"));
            nameValuePairs.add(new BasicNameValuePair("submit", "Accept and Connect"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpclient.execute(httppost);
            int statusCode = response.getStatusLine().getStatusCode();
            Toast.makeText(context, "status: " + statusCode, Toast.LENGTH_LONG).show();
            if(statusCode == 200){
                setStatus(context, "connected");
            }
            Log.d("lazystu", "connect Wi-Fi.HK: " + statusCode);

        } catch (ClientProtocolException e) {
            if(e.getMessage() != null) {
                Toast.makeText(context, "Protocol Err: [" + e.getMessage() + "]", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            if(e.getMessage() != null) {
                Toast.makeText(context, "IO Err: [" + e.getMessage() + "]", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            if(e.getMessage() != null) {
                Toast.makeText(context, "HttpPost Err: [" + e.getMessage()+"]", Toast.LENGTH_LONG).show();
            }
        }
    }
}

