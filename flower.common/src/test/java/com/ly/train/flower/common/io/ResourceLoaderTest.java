package com.ly.train.flower.common.io;

import java.util.List;
import org.junit.Test;
import com.ly.train.flower.common.util.FileUtil;
import com.ly.train.flower.common.util.Pair;

/**
 * @author leeyazhou
 */
public class ResourceLoaderTest {

  @Test
  public void testGetResources() {
    ResourceLoader resourceLoader = new ResourceLoader("", ".services");
    Resource[] a = resourceLoader.getResources();
    System.out.println(a);
    for (Resource r : a) {
      System.out.println(r.getURL());
    List<Pair<String, String>> re =  FileUtil.readService(r);
    System.out.println(re);
    }
  }

  @Test
  public void testGetResourcesCLass() {
    ResourceLoader resourceLoader = new ResourceLoader("com.ly", ".class");
    Resource[] a = resourceLoader.getResources();
    System.out.println(a.length);
  }

}
