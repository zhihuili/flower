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
package com.ly.train.flower.web.springboot.processors;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.web.bind.annotation.RequestMethod;
import com.ly.train.flower.web.springboot.annotation.BindController;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.Set;

@SupportedAnnotationTypes("com.ly.flower.web.springboot.annotation.BindController")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class BindProcessor extends AbstractProcessor {
  private static final String TAG = "[ " + BindProcessor.class.getSimpleName() + " ]:";
  private Types mTypesUtils = null;

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return super.getSupportedAnnotationTypes();
  }

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    this.mTypesUtils = processingEnv.getTypeUtils();
  }

  @Override
  public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
    for (Element element : roundEnvironment.getElementsAnnotatedWith(BindController.class)) {
      if (element instanceof TypeElement) {
        TypeElement classElement = (TypeElement) element;
        PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();

        String fullClassName = classElement.getQualifiedName().toString();
        String className = classElement.getSimpleName().toString();
        String packageName = packageElement.getQualifiedName().toString();

        BindController bindController = classElement.getAnnotation(BindController.class);

        String BindControllerValue = bindController.value();
        String BindControllerType = bindController.type();
        String BindControllerPath = bindController.path();
        RequestMethod BindControllerMethod = bindController.method();
        TypeMirror typeMirror = null;
        boolean isPostJsonInterface = false;
        for (TypeMirror interfaceType : classElement.getInterfaces()) {
          DeclaredType declaredType = (DeclaredType) interfaceType;
          TypeElement interfaceElement = (TypeElement) declaredType.asElement();
          String interfaceTypeName = interfaceElement.getQualifiedName().toString();
          if (com.ly.train.flower.common.service.Service.class.getName().equals(interfaceTypeName)) {
            @SuppressWarnings("unchecked")
            List<TypeMirror> messageTypes = (List<TypeMirror>) declaredType.getTypeArguments();
            if (!messageTypes.isEmpty()) {
              typeMirror = messageTypes.get(0);
              String messageTypeName =
                  ((TypeElement) (mTypesUtils.asElement(typeMirror))).getQualifiedName().toString();
              loge(messageTypeName);
            }
          } else if (com.ly.train.flower.web.springboot.PostJson.class.getName().equals(interfaceTypeName)) {
            isPostJsonInterface = true;
          }
        }

        try {
          String templateName = "BindController.vm";

          Properties props = new Properties();
          URL url = this.getClass().getClassLoader().getResource("velocity.properties");
          props.load(url.openStream());

          VelocityEngine velocityEngine = new VelocityEngine(props);
          velocityEngine.init();

          VelocityContext velocityContext = new VelocityContext();
          velocityContext.put("packageName", packageName);
          velocityContext.put("className", className);
          velocityContext.put("BindControllerValue", BindControllerValue);
          velocityContext.put("BindControllerType", BindControllerType);
          velocityContext.put("BindControllerPath", BindControllerPath);
          velocityContext.put("BindControllerMethod", BindControllerMethod);
          if (typeMirror != null) {
            templateName = "BindObjectController.vm";
            String BingControllerClass = typeMirror.toString();
            velocityContext.put("BingControllerClass", BingControllerClass);
            if (isPostJsonInterface && (BindControllerMethod == RequestMethod.POST)) {
              velocityContext.put("hasRequestBody", "@RequestBody");
            } else {
              velocityContext.put("hasRequestBody", "");
            }
          }

          Template velocityEngineTemplate = velocityEngine.getTemplate(templateName);

          JavaFileObject jfo = processingEnv.getFiler().createSourceFile(fullClassName + "Controller");
          Writer writer = jfo.openWriter();
          velocityEngineTemplate.merge(velocityContext, writer);

          writer.close();

        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    return true;
  }

  private void loge(String msg) {
    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, TAG + msg);
  }
}
