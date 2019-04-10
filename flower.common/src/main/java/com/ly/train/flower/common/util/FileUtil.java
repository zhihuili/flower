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
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.common.exception.FlowerException;

public class FileUtil {
  static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

  public static List<Pair<String, String>> readFlow(String path) {
    InputStreamReader fr = null;
    BufferedReader br = null;
    String line;
    List<Pair<String, String>> flow = new ArrayList<>();
    try {
      fr = new InputStreamReader(FileUtil.class.getResourceAsStream(path), Constant.ENCODING_UTF_8);
      br = new BufferedReader(fr);
      while ((line = br.readLine()) != null) {
        String sl = line.trim();
        if ((sl.startsWith("//")) || sl.startsWith("#") || sl.equals("")) {
          continue;
        }
        String[] connection = sl.split("->");
        if (connection == null || connection.length != 2) {
          throw new RuntimeException("Illegal flow config:" + path);
        } else {
          flow.add(new Pair<String, String>(connection[0].trim(), connection[1].trim()));
        }
      }
    } catch (IOException e) {
      logger.error("filePath : " + path, e);
    } finally {
      close(br, fr);
    }
    return flow;
  }

  public static List<Pair<String, String>> readService(String path) {
    List<Pair<String, String>> result = new ArrayList<Pair<String, String>>();
    InputStreamReader fr = null;
    BufferedReader br = null;
    try {
      fr = new InputStreamReader(FileUtil.class.getResourceAsStream(path), Constant.ENCODING_UTF_8);
      br = new BufferedReader(fr);
      String line;
      while ((line = br.readLine()) != null) {
        String sl = line.trim();
        if ((sl.startsWith("//")) || sl.startsWith("#") || sl.equals("")) {
          continue;
        }
        String[] kv = sl.split("=");
        if (kv == null || kv.length != 2) {
          throw new FlowerException("Illegal flow config:" + path);
        }
        result.add(new Pair<String, String>(kv[0].trim(), kv[1].trim()));
      }
    } catch (Exception e) {
      logger.error("filePath : " + path, e);
    } finally {
      close(br, fr);
    }
    return result;
  }

  static void close(Reader reader, InputStreamReader is) {
    IOUtil.close(reader);
    IOUtil.close(is);
  }

}
