package com.ly.train.flower.common.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtil {

  public static List<String[]> readFlow(String path) throws Exception {
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
        br.close();
        fr.close();
        throw new Exception("Illegal flow config:" + path);
      }
      flow.add(connection);
    }
    br.close();
    fr.close();
    return flow;
  }

  public static Map<String, String> readService(String path) throws Exception {
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
        br.close();
        fr.close();
        throw new Exception("Illegal flow config:" + path);
      }
      map.put(kv[0], kv[1]);
    }
    br.close();
    fr.close();
    return map;
  }
}
