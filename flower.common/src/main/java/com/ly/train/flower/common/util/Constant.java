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
/**
 * 
 */
package com.ly.train.flower.common.util;

/**
 * @author leeyazhou
 * 
 */
public class Constant {

  public static final String ENCODING_UTF_8 = "UTF-8";

  public static final String DEFAULT_CONTENT_TEXT = "text/html;charset=" + ENCODING_UTF_8;

  public static final String DEFAULT_CONTENT_JSON = "application/json;charset=" + ENCODING_UTF_8;

  public static final String AGGREGATE_SERVICE_NAME = "com.ly.train.flower.core.service.impl.AggregateService";

  /**
   * flower 配置文件参数名称
   */
  public static final String flowerConfigLocationKey = "flower.config.location";
  public static final String springConfigLocationKey = "spring.config.location";
  public static final String flowerHome = "flower.home";


  // public static final String SCOPE_SINGLETON = "singleton";
  // public static final String SCOPE_REQUEST = "request";


  public static final String applicationName = "application";


  public static final String ServiceContextOriginURL = "originURL";
}
