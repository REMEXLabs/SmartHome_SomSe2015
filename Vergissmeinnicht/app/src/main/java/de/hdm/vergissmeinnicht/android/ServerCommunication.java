package de.hdm.vergissmeinnicht.android;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import de.hdm.vergissmeinnicht.helpers.StaticMethods;

/**
 * Created by root on 18.08.15.
 */
public class ServerCommunication {
    private static final String KOUBACHI_API_TASKS =" https://api.koubachi.com/v2/plants/276368/pending_tasks.json?user_credentials=%s&app_key=%s";
    private static final String KOUBACHI_PLANT_API_URL =" https://api.koubachi.com/v2/plants/276368.json?user_credentials=%s&app_key=%s";
    private static final String KOUBACHI_DEVICE_API_URL = " https://api.koubachi.com/v2/user/smart_devices/00066672c11c.json?user_credentials=%s&app_key=%s";
    private static final String KOUBACHI_SOILMOISTURE= "recent_soilmoisture_reading_value";
    private static final String KOUBACHI_TEMPERATURE= "recent_temperature_reading_value";
    private static final String KOUBACHI_ILLUMINANCE= "vdm_light_level";
    private static final String KOUBACHI_NAME= "name";


    public void getSmartDeviceInfo( final String user_creds,final String app_key, final CustomApplication app){
        Request request = new Request.Builder()
                .url(String.format(KOUBACHI_DEVICE_API_URL, user_creds, app_key))
                .build();

        createHttpClient().newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                System.out.println(e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String responseStr = response.body().string();
                if (response != null) {
                    try {
                        JSONObject jsonObjectResponse = new JSONObject(responseStr);
                        JSONObject sensorList = jsonObjectResponse.getJSONObject("device");
                        String temperature = (String)StaticMethods.handleSensorResult(sensorList, KOUBACHI_TEMPERATURE);
                        String soilmoisture =  (String)StaticMethods.handleSensorResult(sensorList, KOUBACHI_SOILMOISTURE);
                        app.loadKoubachiDeviceInfo(temperature, soilmoisture, "");
                    } catch (JSONException e) {
                        System.out.println("Exception raised" + e);
                    }
                }

            }
        });
    }

    public void getPlantInfo( String user_creds, String app_key, final CustomApplication app){
        Request request = new Request.Builder()
                .url(String.format(KOUBACHI_PLANT_API_URL, user_creds, app_key))
                .build();

        createHttpClient().newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                System.out.println("%%%%%%%%%%%%FAILURE" + e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String responseStr = response.body().string();
                if (response != null) {
                    try {
                        JSONObject jsonObjectResponse = new JSONObject(responseStr);
                        JSONObject sensorList = jsonObjectResponse.getJSONObject("plant");
                        Double illuminance = (Double) StaticMethods.handleSensorResult(sensorList, KOUBACHI_ILLUMINANCE);
                        String name = (String) StaticMethods.handleSensorResult(sensorList, KOUBACHI_NAME);
                        app.loadPlantinfo(StaticMethods.toPercentage(illuminance), name);
                    } catch (JSONException e) {
                        System.out.println("Exception raised" + e);
                    }
                }
            }
        });
    }

    public void getPlantTasks( String user_creds, String app_key, final CustomApplication app) {
        Request request = new Request.Builder()
                .url(String.format(KOUBACHI_API_TASKS, user_creds, app_key))
                .build();

        createHttpClient().newCall(request).enqueue(new Callback() {


            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String responseStr = response.body().string();
                if (response != null) {
                    try {
                        JSONArray jsonObjectResponse = new JSONArray(responseStr);
                        ArrayList<String> taskList = new ArrayList<String>();
                        if(jsonObjectResponse.length() > 0) {
                            for (int i = 0; i < jsonObjectResponse.length(); i++) {
                                JSONObject careAdivce = jsonObjectResponse.getJSONObject(i).getJSONObject("plant_care_advice");
                                String careType = careAdivce.getString("advice_type");
                                taskList.add(careType);
                            }
                            app.loadTasks(taskList);
                            app.getImportantEvents();
                        }
                    } catch (JSONException e) {

                    }
                }
            }


        });
    }
        // creates a custom OkHttpClient

    private static OkHttpClient createHttpClient() {
        final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                }
        };

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        OkHttpClient client = new OkHttpClient();

        client.setSslSocketFactory(sslSocketFactory);
        client.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        return client;
    }
}


