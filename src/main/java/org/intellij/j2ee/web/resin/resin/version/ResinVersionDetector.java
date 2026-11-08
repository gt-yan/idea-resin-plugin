package org.intellij.j2ee.web.resin.resin.version;

import java.io.File;

public final class ResinVersionDetector {
   private ResinVersionDetector() {
   }

   public static ResinVersion getResinVersion(File resinHome) {
      ResinVersion version = ClassCallDetector.getResinVersion(resinHome);
      if (version == null) {
         version = FallbackDetector.getResinVersion(resinHome);
      }

      return version;
   }
}
