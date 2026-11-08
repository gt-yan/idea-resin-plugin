package org.intellij.j2ee.web.resin.resin.version;

import com.intellij.openapi.util.io.FileFilters;
import java.io.File;
import java.io.FileFilter;

public final class ResinLibCollector {
   private static final FileFilter JAR_FILTER = FileFilters.filesWithExtension("jar");

   private ResinLibCollector() {
   }

   public static File[] getLibFiles(File libDir, boolean all) {
      return all ? libDir.listFiles(JAR_FILTER) : libDir.listFiles((dir, name) -> {
         return JAR_FILTER.accept(new File(dir, name)) && (name.contains("jsdk") || name.contains("javaee-"));
      });
   }
}
