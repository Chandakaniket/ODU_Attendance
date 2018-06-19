package edu.its.oduattendance;

import android.util.Base64;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by achandak on 3/12/2018.
 */

public class LoginServiceCaller {
    public static String user,midas_id,midas_password;
    public static String oduCall(String resourcepath,String id,String password){
        HttpURLConnection connection=null;
        BufferedReader reader=null;
        StringBuffer buffer=new StringBuffer("");
        try
        {
            user=id+":"+password;
            midas_id=id;
            midas_password=password;
            URL url = new URL(resourcepath);
            connection=(HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "text/html;application/vnd.paos+xml");
            connection.setRequestProperty("PAOS", "ver='urn:liberty:paos:2003-08';'urn:oasis:names:tc:SAML:2.0:profiles:SSO:ecp'");
            int responseCode = connection.getResponseCode();
            System.out.println("response code"+responseCode);
            if(responseCode==200) {
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    public static String shibbolethCall(String resourcepath, String envelope)
    {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer("");
        int responseCode=0;
        try {
            // @ac
            byte[] data=user.getBytes("UTF-8");
            String base64= Base64.encodeToString(data,Base64.DEFAULT);

            URL url = new URL(resourcepath);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "text/xml");
            connection.setRequestProperty("Authorization", "Basic "+base64);

            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(envelope);
            wr.flush();
            wr.close();

            responseCode = connection.getResponseCode();
            System.out.println("response code" + responseCode);

            InputStream stream=connection.getInputStream();
            reader=new BufferedReader(new InputStreamReader(stream));

            String line="";
            while ((line= reader.readLine())!=null)
            {
                buffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Integer.toString(responseCode);
    }

    public static String csCall(String resourcepath,String midas_id)
    {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer("");
        int responseCode=0;
        try
        {
            URL url = new URL(resourcepath);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            List<NameValuePair> params=new ArrayList<>();
            params.add(new BasicNameValuePair("id",midas_id));
            System.out.println(getQuery(params));
            wr.writeBytes(getQuery(params));
            wr.flush();
            wr.close();


            responseCode = connection.getResponseCode();
            System.out.println("response code cs loginn" + responseCode);
            InputStream stream=connection.getInputStream();
            reader=new BufferedReader(new InputStreamReader(stream));

            String line="";
            while ((line= reader.readLine())!=null)
            {
                buffer.append(line);
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    public static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}