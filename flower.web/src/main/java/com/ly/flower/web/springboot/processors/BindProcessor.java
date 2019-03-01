package com.ly.flower.web.springboot.processors;

import com.ly.flower.web.springboot.annotation.BindController;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

@SupportedAnnotationTypes("com.ly.flower.web.springboot.annotation.BindController")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class BindProcessor extends AbstractProcessor {
  private static final String TAG = "[ " + BindProcessor.class.getSimpleName() + " ]:";

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return super.getSupportedAnnotationTypes();
  }
  @Override
  public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
    for (Element element : roundEnvironment.getElementsAnnotatedWith(BindController.class)) {
      if (element instanceof  TypeElement) {
        TypeElement classElement = (TypeElement)element;
        PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();

        String fullClassName = classElement.getQualifiedName().toString();
        String className = classElement.getSimpleName().toString();
        String packageName = packageElement.getQualifiedName().toString();

        BindController bindController = classElement.getAnnotation(BindController.class);

        String BindControllerValue = bindController.value();
        String BindControllerType = bindController.type();
        String BindControllerPath = bindController.path();
        RequestMethod BindControllerMethod = bindController.method();

        loge(fullClassName + "\n" + className + "\n" + packageName + "\n" + BindControllerValue + " " + BindControllerType + " " + BindControllerPath + " " + BindControllerMethod.toString());
        try {
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

          Template velocityEngineTemplate = velocityEngine.getTemplate("BindController.vm");

          JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
              fullClassName + "Controller");

          loge("creating source file: " + jfo.toUri());

          Writer writer = jfo.openWriter();

          loge("applying velocity template: " + velocityEngineTemplate.getName());

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
