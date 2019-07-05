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
package com.ly.train.flower.center.core.controller;

import com.ly.train.flower.center.core.util.R;
import com.ly.train.flower.center.core.util.Response;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.web.spring.FlowerController;

/**
 * @author leeyazhou
 * 
 */
public abstract class BaseController extends FlowerController {
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  protected <T> Response<T> ok(T data) {

    return R.ok(data);
  }

  public <T> Response<T> ok() {
    return R.ok();
  }

  protected <T> Response<T> error(int code, String msg) {
    return R.error(code, msg);
  }

}
