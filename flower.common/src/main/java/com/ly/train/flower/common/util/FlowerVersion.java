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

import java.io.IOException;
import java.util.Properties;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;

public class FlowerVersion implements Comparable<FlowerVersion> {
  private static final Logger logger = LoggerFactory.getLogger(FlowerVersion.class);
  private String completeVersion;
  private Integer major;
  private Integer minor;
  private Integer subminor;

  public FlowerVersion(String completeVersion, int major, int minor, int subminor) {
    this.completeVersion = completeVersion;
    this.major = major;
    this.minor = minor;
    this.subminor = subminor;
  }

  public FlowerVersion(int major, int minor, int subminor) {
    this(null, major, minor, subminor);
  }

  public String getCompleteVersion() {
    return completeVersion;
  }

  public int getMajor() {
    return this.major;
  }

  public int getMinor() {
    return this.minor;
  }

  public int getSubminor() {
    return this.subminor;
  }

  /**
   * A string representation of this version. If this version was parsed from, or
   * provided with, a "complete" string which may contain more than just the
   * version number, this string is returned verbatim. Otherwise, a string
   * representation of the version numbers is given.
   */
  @Override
  public String toString() {
    if (this.completeVersion != null) {
      return this.completeVersion;
    }
    return String.format("%d.%d.%d", this.major, this.minor, this.subminor);
  }

  public int compareTo(FlowerVersion other) {
    int com;
    if ((com = this.major.compareTo(other.getMajor())) != 0) {
      return com;
    } else if ((com = this.minor.compareTo(other.getMinor())) != 0) {
      return com;
    }
    return this.subminor.compareTo(other.getSubminor());
  }

  /**
   * Does this version meet the minimum specified by `min'?
   *
   * @param min The minimum version to compare against.
   * @return true or false
   */
  public boolean meetsMinimum(FlowerVersion min) {
    return compareTo(min) >= 0;
  }

  /**
   * Parse the server version into major/minor/subminor.
   * 
   * @param versionString string version
   * @return {@link FlowerVersion}
   */
  public static FlowerVersion parseVersion(final String versionString) {
    int point = versionString.indexOf('.');

    if (point != -1) {
      try {
        int serverMajorVersion = Integer.parseInt(versionString.substring(0, point));

        String remaining = versionString.substring(point + 1, versionString.length());
        point = remaining.indexOf('.');

        if (point != -1) {
          int serverMinorVersion = Integer.parseInt(remaining.substring(0, point));

          remaining = remaining.substring(point + 1, remaining.length());

          int pos = 0;

          while (pos < remaining.length()) {
            if ((remaining.charAt(pos) < '0') || (remaining.charAt(pos) > '9')) {
              break;
            }

            pos++;
          }

          int serverSubminorVersion = Integer.parseInt(remaining.substring(0, pos));

          return new FlowerVersion(versionString, serverMajorVersion, serverMinorVersion, serverSubminorVersion);
        }
      } catch (NumberFormatException NFE1) {
      }
    }

    // can't parse the server version
    return new FlowerVersion(0, 0, 0);
  }

  private static FlowerVersion flowerVersion;

  public static FlowerVersion getFlowerVersion() {
    if (null == flowerVersion) {
      Properties properties = new Properties();
      try {
        properties.load(FlowerVersion.class.getClassLoader().getResourceAsStream("FlowerVersion.properties"));
        if (!properties.isEmpty()) {
          String flowerVersionStr = properties.getProperty("flower.version");
          flowerVersion = FlowerVersion.parseVersion(flowerVersionStr);
        }
      } catch (IOException e) {
        // do nothing
      }
    }
    return flowerVersion;
  }

  public static void logVersionInfo() {
    logger.info("flower.version : {}", FlowerVersion.getFlowerVersion().getCompleteVersion());
    logger.info("serverInfo.server.number : {}", FlowerVersion.getFlowerVersion().getCompleteVersion());
    logger.info("os.name : {}", System.getProperty("os.name"));
    logger.info("os.version : {}", System.getProperty("os.version"));
    logger.info("os.arch : {}", System.getProperty("os.arch"));
    logger.info("java.home : {}", System.getProperty("java.home"));
    logger.info("vm.version : {}", System.getProperty("java.runtime.version"));
    logger.info("vm.vendor : {}", System.getProperty("java.vm.vendor"));
  }
}
