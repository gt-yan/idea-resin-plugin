package org.intellij.j2ee.web.resin.resin.configuration;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.util.io.FileUtil;
import java.io.File;
import java.io.IOException;
import org.intellij.j2ee.web.resin.ResinBundle;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class ResinGeneratedConfig {
   private final Element myElement;
   private final File myFile;

   public ResinGeneratedConfig(@NotNull Element element, @NonNls String prefix) throws ExecutionException {
      if (element == null) {
         $$$reportNull$$$0(0);
      }

      this.myElement = element;

      try {
         this.myFile = FileUtil.createTempFile(prefix, ".conf");
         this.myFile.deleteOnExit();
      } catch (IOException var4) {
         throw new ExecutionException(ResinBundle.message("message.error.resin.conf.cant.create", var4));
      }
   }

   public File getFile() {
      return this.myFile;
   }

   public void save() throws ExecutionException {
      try {
         JDOMUtil.write(this.myElement, this.myFile.toPath());
      } catch (IOException var2) {
         throw new ExecutionException(ResinBundle.message("message.error.resin.conf.update"), var2);
      }
   }

   // $FF: synthetic method
   private static void $$$reportNull$$$0(int var0) {
      throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "element", "org/intellij/j2ee/web/resin/resin/configuration/ResinGeneratedConfig", "<init>"));
   }
}
