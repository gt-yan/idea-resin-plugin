package org.intellij.j2ee.web.resin.resin.version;

import java.io.File;

final class FallbackDetector {
   public static ResinVersion getResinVersion(File resinHome) {
      if (ResinVersion.VERSION_3_X.isOfVersion(resinHome)) {
         return ResinVersion.VERSION_3_X;
      } else {
         return ResinVersion.VERSION_2_X.isOfVersion(resinHome) ? ResinVersion.VERSION_2_X : ResinVersion.UNKNOWN_VERSION;
      }
   }
}
