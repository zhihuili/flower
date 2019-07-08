package com.ly.train.flower.tools.http.factory.httpclient;

import java.util.concurrent.CompletableFuture;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.util.EntityUtils;

/**
 * @author leeyazhou
 */
public class HttpClientCallback extends CompletableFuture<String> implements FutureCallback<HttpResponse> {

  @Override
  public void completed(HttpResponse result) {
    try {
      String r = EntityUtils.toString(result.getEntity());
      complete(r);
    } catch (Exception e) {
      completeExceptionally(e);
    }
  }

  @Override
  public void failed(Exception ex) {
    completeExceptionally(ex);
  }

  @Override
  public void cancelled() {
    completeExceptionally(new IllegalStateException("it is canceled."));
  }

}
