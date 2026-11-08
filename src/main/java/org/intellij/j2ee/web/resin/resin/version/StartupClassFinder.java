package org.intellij.j2ee.web.resin.resin.version;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

final class StartupClassFinder {
   private static final String MAPPING_PROP_FILE = "org/intellij/j2ee/web/resin/resin/version/versionmapping.properties";
   private static Properties versionMapping = null;

   static String getStartupClassForVersion(String version) {
      if (versionMapping == null) {
         loadVersionMapping();
      }

      do {
         String startup = versionMapping.getProperty(version);
         if (startup != null) {
            return startup;
         }

         int posDot = version.lastIndexOf(46);
         if (posDot == -1) {
            version = null;
         } else {
            version = version.substring(0, posDot);
         }
      } while(version != null);

      return null;
   }

   private static void loadVersionMapping() {
      versionMapping = new Properties();

      try {
         InputStream in = StartupClassFinder.class.getClassLoader().getResourceAsStream("org/intellij/j2ee/web/resin/resin/version/versionmapping.properties");

         try {
            if (in != null) {
               versionMapping.load(in);
            }
         } catch (Throwable var4) {
            if (in != null) {
               try {
                  in.close();
               } catch (Throwable var3) {
                  var4.addSuppressed(var3);
               }
            }

            throw var4;
         }

         if (in != null) {
            in.close();
         }
      } catch (IOException var5) {
         var5.printStackTrace();
      }

   }
}
