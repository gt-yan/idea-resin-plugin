package org.intellij.j2ee.web.resin.resin.version;

import java.io.File;
import org.jetbrains.annotations.NotNull;

public class GenericResinVersion extends ResinVersion {
   private final String myStartupClass;
   private final boolean myAllowXdebug;
   private final boolean myAllowJmx;

   public GenericResinVersion(String name, String versionNumber, @NotNull String startupClass, boolean allowXdebug, boolean allowJmx) {
      super(name, versionNumber);
      if (startupClass == null) {
         $$$reportNull$$$0(0);
      }

      this.myStartupClass = startupClass;
      this.myAllowXdebug = allowXdebug;
      this.myAllowJmx = allowJmx;
   }

   public boolean isOfVersion(File resinHome) {
      ResinVersion ver = ResinVersionDetector.getResinVersion(resinHome);
      return this.equals(ver);
   }

   public String getStartupClass() {
      return this.myStartupClass;
   }

   public boolean allowXdebug() {
      return this.myAllowXdebug;
   }

   public boolean allowJmx() {
      return this.myAllowJmx;
   }

   // $FF: synthetic method
   private static void $$$reportNull$$$0(int var0) {
      throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "startupClass", "org/intellij/j2ee/web/resin/resin/version/GenericResinVersion", "<init>"));
   }
}
