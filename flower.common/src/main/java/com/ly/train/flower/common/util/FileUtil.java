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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {
  private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

  public static List<String[]> readFlow(String path) throws IOException {
    InputStreamReader fr = new InputStreamReader(FileUtil.class.getResourceAsStream(path));
    BufferedReader br = new BufferedReader(fr);
    String line = "";
    List<String[]> flow = new ArrayList<String[]>();
    while ((line = br.readLine()) != null) {
      String sl = line.trim();
      if ((sl.startsWith("//")) || sl.startsWith("#") || sl.equals("")) {
        continue;
      }
      String[] connection = sl.split("->");
      if (connection == null || connection.length != 2) {
        close(br, fr);
        throw new RuntimeException("Illegal flow config:" + path);
      }
      flow.add(connection);
    }
    close(br, fr);
    return flow;
  }

  public static Map<String, String> readService(String path) throws IOException {
    InputStreamReader fr = new InputStreamReader(FileUtil.class.getResourceAsStream(path));
    BufferedReader br = new BufferedReader(fr);
    String line = "";
    Map<String, String> map = new HashMap<String, String>();
    while ((line = br.readLine()) != null) {
      String sl = line.trim();
      if ((sl.startsWith("//")) || sl.startsWith("#") || sl.equals("")) {
        continue;
      }
      String[] kv = sl.split("=");
      if (kv == null || kv.length != 2) {
        close(br, fr);
        throw new RuntimeException("Illegal flow config:" + path + ", sl");
      }
      map.put(kv[0], kv[1]);
    }
    close(br, fr);
    return map;
  }

  static void close(Reader reader, InputStreamReader is) {
    if (reader != null) {
      try {
        reader.close();
      } catch (Exception e) {
        logger.error("", e);
      }
    }

    if (is != null) {
      try {
        is.close();
      } catch (Exception e) {
        logger.error("", e);
      }
    }

  }

}
