package com.ly.flower.web.springboot.processors;

import com.ly.flower.web.springboot.annotation.BindController;
import javassist.bytecode.SignatureAttribute;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.net.URL;
import java.util.Map;
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
        TypeMirror typeMirror = null;
        for (AnnotationMirror annotationMirror : classElement.getAnnotationMirrors()) {
          Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues
                  = annotationMirror.getElementValues();
          for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry
                  : elementValues.entrySet()) {
            String key = entry.getKey().getSimpleName().toString();
            Object value = entry.getValue().getValue();
            switch (key) {
              case "paramClass":{
                //Error:java: [ BindProcessor ]:class com.sun.tools.javac.code.Type$ClassType
                typeMirror = (TypeMirror)value;
                break;
              }
            }
          }
        }

        //loge(fullClassName + "\n" + className + "\n" + packageName + "\n" + BindControllerValue + " " + BindControllerType + " " + BindControllerPath + " " + BindControllerMethod.toString());
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
          }

          Template velocityEngineTemplate = velocityEngine.getTemplate(templateName);

          JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
              fullClassName + "Controller");

          //loge("creating source file: " + jfo.toUri());

          Writer writer = jfo.openWriter();

          //loge("applying velocity template: " + velocityEngineTemplate.getName());

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
