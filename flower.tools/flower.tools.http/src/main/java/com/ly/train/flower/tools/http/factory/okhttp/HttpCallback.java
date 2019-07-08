package com.ly.train.flower.tools.http.factory.okhttp;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @author leeyazhou
 */
public class HttpCallback extends CompletableFuture<String> implements Callback {

  @Override
  public void onFailure(Call arg0, IOException ex) {
    completeExceptionally(ex);
  }

  @Override
  public void onResponse(Call arg0, Response response) throws IOException {
    try {
      complete(response.body().string());
    } catch (Exception e) {
      completeExceptionally(e);
    }
  }

}
