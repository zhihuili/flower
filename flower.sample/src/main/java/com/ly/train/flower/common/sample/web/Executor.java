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
package com.ly.train.flower.common.sample.web;

import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.AsyncContext;

public class Executor implements Runnable {
  private AsyncContext ctx = null;

  public Executor(AsyncContext ctx) {
    this.ctx = ctx;
  }

  public void run() {
    try {
//      Thread.sleep(100);
      PrintWriter out = ctx.getResponse().getWriter();
      out.println("- end：" + System.currentTimeMillis());
      out.flush();
      ctx.complete();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
