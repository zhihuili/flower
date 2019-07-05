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
package com.ly.train.flower.core.service.container;

import java.util.concurrent.atomic.AtomicBoolean;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;

/**
 * @author leeyazhou
 * 
 */
public abstract class AbstractInit implements IInit {
  private static final Logger logger = LoggerFactory.getLogger(AbstractInit.class);
  private AtomicBoolean init = new AtomicBoolean();

  @Override
  public void init() {
    if (init.compareAndSet(false, true)) {
      logger.debug("init class : {}", this);
      doInit();
    }
  }

  /**
   * 实际初始化时执行方法。一般应用调用初始化请调用init()方法
   * 
   * @see AbstractInit#init()
   */
  protected abstract void doInit();
}
