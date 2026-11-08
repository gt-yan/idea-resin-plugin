package org.intellij.j2ee.web.resin.resin.version;

import com.intellij.javaee.oss.util.Version;
import com.intellij.openapi.util.io.FileUtil;
import java.io.File;
import org.intellij.j2ee.web.resin.ResinBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

public abstract class ResinVersion {
   private final String name;
   private final String versionNumber;
   public static final ResinVersion VERSION_2_X = new ResinVersion(ResinBundle.message("resin.version.fallback.v2"), "2.x") {
      public boolean isOfVersion(File resinHome) {
         return (new File(resinHome, FileUtil.toSystemDependentName("lib/jsdk23.jar"))).exists();
      }

      public String getStartupClass() {
         return "com.caucho.server.http.HttpServer";
      }

      public boolean allowXdebug() {
         return false;
      }

      public boolean allowJmx() {
         return false;
      }
   };
   public static final ResinVersion VERSION_3_X = new ResinVersion(ResinBundle.message("resin.version.fallback.v3"), "3.x") {
      public boolean isOfVersion(File resinHome) {
         return (new File(resinHome, FileUtil.toSystemDependentName("lib/jsdk-24.jar"))).exists();
      }

      public String getStartupClass() {
         return "com.caucho.server.resin.Resin";
      }

      public boolean allowXdebug() {
         return true;
      }

      public boolean allowJmx() {
         return true;
      }
   };
   public static final ResinVersion UNKNOWN_VERSION = new ResinVersion(ResinBundle.message("resin.version.fallback.vUnknown"), "unknown") {
      public boolean isOfVersion(File resinHome) {
         return !VERSION_2_X.isOfVersion(resinHome) && !VERSION_3_X.isOfVersion(resinHome);
      }

      public @Nullable String getStartupClass() {
         return null;
      }

      public boolean allowXdebug() {
         return false;
      }

      public boolean allowJmx() {
         return false;
      }
   };

   protected ResinVersion(String name, @NonNls String versionNumber) {
      this.name = name;
      this.versionNumber = versionNumber;
   }

   public String toString() {
      return this.name;
   }

   public String getVersionNumber() {
      return this.versionNumber;
   }

   public Version getParsed() {
      return new Version(this.versionNumber);
   }

   public boolean equals(Object obj) {
      return obj instanceof ResinVersion && this.toString().equals(obj.toString());
   }

   public abstract boolean isOfVersion(File var1);

   public abstract @Nullable @NonNls String getStartupClass();

   public abstract boolean allowXdebug();

   public abstract boolean allowJmx();
}
