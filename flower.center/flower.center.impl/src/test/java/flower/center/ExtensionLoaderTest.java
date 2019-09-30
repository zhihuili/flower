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
package flower.center;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import org.junit.Test;
import com.ly.train.flower.center.core.store.ServiceInfoStore;
import com.ly.train.flower.center.core.store.memory.ServiceInfoMemoryStore;
import com.ly.train.flower.common.util.ExtensionLoader;

/**
 * @author leeyazhou
 */
public class ExtensionLoaderTest {

  @Test
  public void testLoadServiceConfig() throws ClassNotFoundException, IOException {

    Enumeration<URL> it = Thread.currentThread().getContextClassLoader()
        .getResources("META-INF/services/flower/com.ly.train.flower.center.core.store.ServiceInfoStore");
    while (it.hasMoreElements()) {
      System.out.println(it.nextElement());
    }

    ServiceInfoStore infoStore = ExtensionLoader.load(ServiceInfoStore.class).load("memory");
    assertNotNull(infoStore);
    assertEquals(infoStore.getClass(), ServiceInfoMemoryStore.class);
  }
}
