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
package com.ly.train.flower.common.sample.textflow;

import org.junit.Test;
import com.ly.train.flower.common.sample.TestBase;
import com.ly.train.flower.common.sample.textflow.model.Message1;
import com.ly.train.flower.common.sample.textflow.model.Message2;

public class Sample  extends TestBase{

  @Test
  public void main() throws Exception {

    Message2 m2 = new Message2(10, "Zhihui");
    Message1 m1 = new Message1();
    m1.setM2(m2);

    System.out.println("返回结果" + serviceFacade.syncCallService("sample", m1));
    System.out.println("返回结果" + serviceFacade.syncCallService("sample", m1));
    System.out.println("返回结果" + serviceFacade.syncCallService("sample", m1));
    System.out.println("返回结果" + serviceFacade.syncCallService("sample", m1));

  }

}
