/**
 * Copyright © 2019 同程艺龙 (zhihui.li@ly.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ly.train.flower.common.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

public class HttpClient {
  static final Logger logger = LoggerFactory.getLogger(HttpClient.class);
  public static final String GET = "GET";
  public static final String POST = "POST";
  public static final String PUT = "PUT";

  public static HttpClientBuilder builder() {
    return new HttpClientBuilder();
  }

  private HttpClientBuilder httpClientBuilder;

  HttpClient(HttpClientBuilder httpClientBuilder) {
    this.httpClientBuilder = httpClientBuilder;
  }

  public String get() {
    HttpURLConnection connection = null;
    InputStream is = null;
    BufferedReader br = null;
    String result = null;
    try {
      URL url = new URL(httpClientBuilder.getUrl());
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod(GET);
      connection.setConnectTimeout(httpClientBuilder.getConnectionTime());
      connection.setReadTimeout(httpClientBuilder.getReadTimeout());
      connection.connect();
      if (connection.getResponseCode() == 200) {
        is = connection.getInputStream();
        br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuffer sbf = new StringBuffer();
        String temp = null;
        while ((temp = br.readLine()) != null) {
          sbf.append(temp);
          sbf.append("\r\n");
        }
        result = sbf.toString();
      }
    } catch (Exception e) {
      logger.error("", e);
    } finally {
      IOUtil.close(br);
      IOUtil.close(is);
      connection.disconnect();
    }

    return result;
  }

  public String post() {

    HttpURLConnection connection = null;
    InputStream is = null;
    OutputStream os = null;
    BufferedReader br = null;
    String result = null;
    try {
      URL url = new URL(httpClientBuilder.getUrl());
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod(POST);
      connection.setConnectTimeout(httpClientBuilder.getConnectionTime());
      connection.setReadTimeout(httpClientBuilder.getReadTimeout());

      connection.setDoOutput(true);
      connection.setDoInput(true);
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      // connection.setRequestProperty("Authorization", "Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0");
      os = connection.getOutputStream();
      if (httpClientBuilder.getParam() != null) {
        os.write(httpClientBuilder.getParam().getBytes());
      }
      if (connection.getResponseCode() == 200) {
        is = connection.getInputStream();
        br = new BufferedReader(new InputStreamReader(is, Constant.ENCODING_UTF_8));

        StringBuffer sbf = new StringBuffer();
        String temp = null;
        while ((temp = br.readLine()) != null) {
          sbf.append(temp);
          sbf.append("\r\n");
        }
        result = sbf.toString();
      }
    } catch (Exception e) {
      logger.error("", e);
    } finally {
      IOUtil.close(br);
      IOUtil.close(is);
      IOUtil.close(os);
      connection.disconnect();
    }
    return result;
  }


}
