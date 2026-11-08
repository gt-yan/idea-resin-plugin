package org.intellij.j2ee.web.resin.resin.version;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import org.intellij.j2ee.web.resin.ResinBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

final class ClassCallDetector {
   private static final Logger LOG = Logger.getInstance(ClassCallDetector.class);
   private static final @NonNls String RESIN_VERSION_CLASS = "com.caucho.Version";
   private static final @NonNls String RESIN_XDEBUG_CLASS = "com.caucho.log.LogManagerImpl";
   private static final @NonNls String RESIN_JMX_CLASS = "com.caucho.jmx.MBeanServerBuilderImpl";
   private static final @NonNls String RESIN_VERSION_CLASS_ATT_NAME = "VERSION";

   private ClassCallDetector() {
   }

   public static @Nullable ResinVersion getResinVersion(File resinHome) {
      try {
         File resinJar = new File(resinHome, FileUtil.toSystemDependentName("lib/resin.jar"));
         if (!resinJar.exists()) {
            return null;
         } else {
            JarClassLoader loader = new JarClassLoader(resinJar.getAbsolutePath());
            String version = getResinVersionFromClass(loader);
            if (version == null) {
               version = getResinVersionFromManifest(resinJar);
            }

            String startupClass = StartupClassFinder.getStartupClassForVersion(version);
            boolean allowDebug = hasClass(loader, "com.caucho.log.LogManagerImpl");
            boolean allowJmx = hasClass(loader, "com.caucho.jmx.MBeanServerBuilderImpl");
            return startupClass == null ? null : new GenericResinVersion(ResinBundle.message("resin.version.prefix", version), version, startupClass, allowDebug, allowJmx);
         }
      } catch (IOException var7) {
         LOG.error(var7);
         return null;
      }
   }

   private static String getResinVersionFromClass(JarClassLoader loader) {
      try {
         Class versionClass = loader.loadClass("com.caucho.Version");
         Field field = versionClass.getDeclaredField("VERSION");
         return field.get((Object)null).toString();
      } catch (ClassNotFoundException var3) {
         return null;
      } catch (NoSuchFieldException | IllegalAccessException var4) {
         LOG.error(var4);
         return null;
      }
   }

   private static String getResinVersionFromManifest(File jarFile) throws IOException {
      JarFile jar = new JarFile(jarFile);

      String var3;
      label29: {
         try {
            Attributes attributes = jar.getManifest().getMainAttributes();
            if (attributes == null) {
               var3 = null;
               break label29;
            }

            var3 = attributes.getValue("Implementation-Version");
         } catch (Throwable var5) {
            try {
               jar.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }

            throw var5;
         }

         jar.close();
         return var3;
      }

      jar.close();
      return var3;
   }

   private static boolean hasClass(JarClassLoader loader, @NonNls String className) {
      try {
         return loader.loadClass(className) != null;
      } catch (ClassNotFoundException var3) {
         return false;
      }
   }
}
