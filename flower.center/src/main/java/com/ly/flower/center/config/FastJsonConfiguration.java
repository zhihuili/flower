/**
 * 
 */
package com.ly.flower.center.config;

import java.nio.charset.Charset;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

/**
 * @author leeyazhou
 *
 */
@Configuration
public class FastJsonConfiguration implements WebMvcConfigurer {

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    FastJsonConfig fastJsonConfig = new FastJsonConfig();
    fastJsonConfig.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect, // SerializerFeature.WriteMapNullValue,
        SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullBooleanAsFalse,
        SerializerFeature.PrettyFormat);
    fastJsonConfig.setCharset(Charset.forName("UTF-8"));
    fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");

    FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
    converter.setFastJsonConfig(fastJsonConfig);
    converters.add(0, converter);
  }

}
