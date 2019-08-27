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
package com.ly.train.flower.filter.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.common.exception.FlowException;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.filter.AbstractFilter;
import com.ly.train.flower.filter.FilterChain;
import brave.Tracing;
import brave.opentracing.BraveTracer;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.log.Fields;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;
import io.opentracing.propagation.TextMapAdapter;
import io.opentracing.tag.Tags;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

/**
 * @author leeyazhou
 * 
 */
public class OpenTracingFilter extends AbstractFilter {
  static final Logger logger = LoggerFactory.getLogger(OpenTracingFilter.class);
  private volatile Tracer tracer = null;

  @Override
  public Object doFilter(Object message, ServiceContext context, FilterChain chain) {
    String spanName = context.getFlowName() + "." + context.getCurrentServiceName();
    SpanBuilder spanBuilder = tracer.buildSpan(spanName);
    SpanContext spanContext = null;
    Map<String, String> headerMap = getMap(context);
    if (headerMap != null) {
      spanContext = tracer.extract(Format.Builtin.TEXT_MAP, new TextMapAdapter(headerMap));
      if (spanContext != null) {
        spanBuilder.asChildOf(spanContext);
      }
    }


    Span span = spanBuilder.start();
    span.log("Flower Trace start.");

    span.setTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_SERVER);
    Scope scope = tracer.scopeManager().activate(span);
    addAttachements(context, span);

    span.setTag("id", context.getId());
    span.setTag("sync", context.isSync());
    span.setTag("codec", context.getCodec());
    span.setTag("flowName", context.getFlowName());
    span.setTag("serviceName", context.getCurrentServiceName());
    span.setTag("flowMessage.transactionId", context.getFlowMessage().getTransactionId());
    span.setTag("flowMessage.messageType", context.getFlowMessage().getMessageType());

    try {
      return chain.doFilter(message, context);
    } catch (Exception ex) {
      Tags.ERROR.set(span, true);
      Map<String, Object> map = new HashMap<String, Object>();
      map.put(Fields.EVENT, "error");
      map.put(Fields.ERROR_OBJECT, ex);
      map.put(Fields.MESSAGE, ex.getMessage());
      span.log(map);
      throw new FlowException(ex);
    } finally {
      span.log("Flower Trace end.");
      scope.close();
      span.finish();
    }

  }

  private Map<String, String> getMap(ServiceContext context) {
    Map<String, String> map = null;
    // 注入请求头:X-B3-SpanId=a435b7758253d491
    // 注入请求头:X-B3-Sampled=1
    // 注入请求头:X-B3-TraceId=a435b7758253d491
    String key = "X-B3-TraceId";
    String value = (String) context.getAttachment(key);
    if (value != null) {
      map = new HashMap<>();
      map.put(key, value);
      key = "X-B3-SpanId";
      value = (String) context.getAttachment(key);
      map.put(key, value);
      key = "X-B3-Sampled";
      value = (String) context.getAttachment(key);
      map.put(key, value);
    }

    return map;
  }

  private void addAttachements(ServiceContext request, Span span) {
    tracer.inject(span.context(), Format.Builtin.TEXT_MAP, new TextMap() {

      @Override
      public Iterator<Entry<String, String>> iterator() {
        throw new UnsupportedOperationException("TextMapInjectAdapter should only be used with Tracer.inject()");
      }

      @Override
      public void put(String key, String value) {
        request.addAttachment(key, value);
      }
    });
  }

  public Tracer getTracer() {
    if (tracer != null) {
      return tracer;
    }
    final String endpoint = "http://10.100.216.147:9411/api/v2/spans";
    tracer = BraveTracer.create(Tracing.newBuilder()
        // send to zipkin by okhttp
        .spanReporter(AsyncReporter.create(OkHttpSender.create(endpoint)))
        // log to the console
        .spanReporter(span -> {
          logger.info("flower report span : {}", span.toString());
        })
        //
        .localServiceName("flower-filter").build());
    return tracer;
  }
}
