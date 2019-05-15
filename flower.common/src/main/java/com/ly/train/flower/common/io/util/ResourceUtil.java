package com.ly.train.flower.common.io.util;

/**
 * @author leeyazhou
 */
public class ResourceUtil {
  /** URL prefix for loading from the file system: "file:". */
  public static final String FILE_URL_PREFIX = "file:";

  /** URL prefix for loading from a jar file: "jar:". */
  public static final String JAR_URL_PREFIX = "jar:";

  /** URL prefix for loading from a war file on Tomcat: "war:". */
  public static final String WAR_URL_PREFIX = "war:";

  /** URL protocol for a file in the file system: "file". */
  public static final String URL_PROTOCOL_FILE = "file";

  /** URL protocol for an entry from a jar file: "jar". */
  public static final String URL_PROTOCOL_JAR = "jar";

  /** URL protocol for an entry from a war file: "war". */
  public static final String URL_PROTOCOL_WAR = "war";

  /** URL protocol for an entry from a zip file: "zip". */
  public static final String URL_PROTOCOL_ZIP = "zip";
  /** File extension for a regular jar file: ".jar". */
  public static final String JAR_FILE_EXTENSION = ".jar";

  /** Separator between JAR URL and file path within the JAR: "!/". */
  public static final String JAR_URL_SEPARATOR = "!/";

  /** Special separator between WAR URL and jar part on Tomcat. */
  public static final String WAR_URL_SEPARATOR = "*/";
}
