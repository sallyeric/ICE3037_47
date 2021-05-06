package edu.skku2.map.ice3037;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpRequest {

    private String server;
    public HttpRequest(String server){
        this.server = server;
    }
    // send Post and get JSON
    public String sendPost(String url, String params) {

        String response = null;
        HttpURLConnection httpURLConnection = null;
        try {
            // server와 연결
            httpURLConnection = (HttpURLConnection) new URL(server+url).openConnection();

            // POST 형식으로 json 데이터 보내기
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes(params);
            wr.flush();
            wr.close();

            InputStream _is;
            if (httpURLConnection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                _is = httpURLConnection.getInputStream();
            } else {
                /* error from server */
                _is = httpURLConnection.getErrorStream();
            }

            // 서버로부터 데이터 읽기
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(_is, StandardCharsets.UTF_8));
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();

            // response code 입력
            int status = httpURLConnection.getResponseCode();

            response = status + sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) httpURLConnection.disconnect();
        }
        return response;
    }

    public String sendDelete(String url, String params) {

        String response = null;
        HttpURLConnection httpURLConnection = null;
        try {
            // server와 연결
            httpURLConnection = (HttpURLConnection) new URL(server+url).openConnection();

            // POST 형식으로 json 데이터 보내기
            httpURLConnection.setRequestMethod("DELETE");
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes(params);
            wr.flush();
            wr.close();

            InputStream _is;
            if (httpURLConnection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                _is = httpURLConnection.getInputStream();
            } else {
                /* error from server */
                _is = httpURLConnection.getErrorStream();
            }

            // 서버로부터 데이터 읽기
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(_is, StandardCharsets.UTF_8));
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();

            // response code 입력
            int status = httpURLConnection.getResponseCode();

            response = status + sb.toString(); // response 문자열은 status + response body로 구성

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // disconnection
            if (httpURLConnection != null) httpURLConnection.disconnect();
        }
        return response;
    }

    public String sendGet(String url, String params) {
        String response = "";
        HttpURLConnection httpURLConnection = null;
        try {
            // server와 연결
            httpURLConnection = (HttpURLConnection) new URL(server+url+params).openConnection();

            // request는 GET이며, json으로 응답 받음
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setDoInput(true);

            InputStream _is;
            if (httpURLConnection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                _is = httpURLConnection.getInputStream();
            } else {
                /* error from server */
                _is = httpURLConnection.getErrorStream();
            }

            // 서버로부터 데이터 읽기
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(_is, StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();

            int status = httpURLConnection.getResponseCode();
            response = status + sb.toString(); // response 문자열은 status + response body로 구성

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // disconnection
            if (httpURLConnection != null) httpURLConnection.disconnect();
        }

        return response;
    }

}