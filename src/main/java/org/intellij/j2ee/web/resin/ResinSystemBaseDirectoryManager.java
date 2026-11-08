package org.intellij.j2ee.web.resin;

import com.intellij.javaee.directoryManager.JavaeeSystemBaseDirectoryManagerProvider;
import com.intellij.javaee.directoryManager.SystemBaseDirectoryManager;
import org.jetbrains.annotations.NotNull;

public final class ResinSystemBaseDirectoryManager {
   public static @NotNull SystemBaseDirectoryManager getInstance() {
      SystemBaseDirectoryManager var10000 = JavaeeSystemBaseDirectoryManagerProvider.getManagerInstance("resin");
      if (var10000 == null) {
         $$$reportNull$$$0(0);
      }

      return var10000;
   }

   // $FF: synthetic method
   private static void $$$reportNull$$$0(int var0) {
      throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "org/intellij/j2ee/web/resin/ResinSystemBaseDirectoryManager", "getInstance"));
   }
}
