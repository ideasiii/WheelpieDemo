package org.iii.wheelpiedemo.course.util;

import org.iii.more.restapiclient.Config;
import org.iii.more.restapiclient.Response;
import org.iii.wheelpiedemo.common.Logs;
import org.iii.wheelpiedemo.common.RestApiHeaderClient;

import java.util.HashMap;

public class HttpUtils {
    public static void postAuthAPI(
        RestApiHeaderClient restApiHeaderClient,
        String url,
        Config.HTTP_DATA_TYPE httpDataType,
        HashMap<String, String> params,
        String userToken )
    {
        authAPI(
            restApiHeaderClient, url, "POST", httpDataType , params, userToken
        );
    }

    public static void getAuthAPI(
            RestApiHeaderClient restApiHeaderClient,
            String url,
            Config.HTTP_DATA_TYPE httpDataType,
            HashMap<String, String> params,
            String userToken )
    {
        authAPI(
            restApiHeaderClient, url, "GET", httpDataType , params, userToken
        );
    }

    public static void authAPI(
        RestApiHeaderClient restApiHeaderClient,
        String url,
        String method,
        Config.HTTP_DATA_TYPE httpDataType,
        HashMap<String, String> params,
        String userToken)
    {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", String.format("Bearer %s", userToken));
        requestAPI(restApiHeaderClient, url, method, httpDataType, params, headers);
    }

    public static void requestAPI(
        RestApiHeaderClient restApiHeaderClient,
        String url,
        String method,
        Config.HTTP_DATA_TYPE httpDataType,
        HashMap<String, String> param,
        HashMap<String, String> headers )
    {
        if (restApiHeaderClient == null) {
            return;
        }
        boolean isHTTPS = url != null && url.startsWith("https://");
        if (param == null) {
            param = new HashMap<String, String>();
        }
        Response response = new Response();
        int nResponse_id = -1;
        switch(String.valueOf(method)) {
            case "POST":
                if (isHTTPS) {
                    restApiHeaderClient.HttpsPost(url, httpDataType, param, response, headers);
                } else {
                    restApiHeaderClient.HttpPost(url, httpDataType, param, response, headers);
                }
                break;
            case "PUT":
                if (isHTTPS) {
                    restApiHeaderClient.HttpsPut(url, httpDataType, param, response, headers);
                } else {
                    restApiHeaderClient.HttpPut(url, httpDataType, param, response, headers);
                }
                break;
            case "GET":
            default:
                if (isHTTPS) {
                    restApiHeaderClient.HttpsGet(url, httpDataType, param, response, headers);
                } else {
                    restApiHeaderClient.HttpGet(url, httpDataType, param, response, headers);
                }
        }
        Logs.showTrace("[API] http response id: " + nResponse_id);
    }
}
