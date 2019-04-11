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
package com.ly.train.flower.center.util;

import com.ly.train.flower.center.model.Response;

/**
 * @author leeyazhou
 *
 */
public class R {

  public static <T> Response<T> ok(T data) {

    return new Response<>(data);
  }

  public static <T> Response<T> ok() {
    return new Response<T>();
  }

  public static <T> Response<T> error(int code, String msg) {
    return new Response<>(code, msg);
  }
}
