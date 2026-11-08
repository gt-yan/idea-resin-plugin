package org.intellij.j2ee.web.resin.resin.common;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.jetbrains.annotations.NonNls;

public final class MBeanUtil {
   private MBeanUtil() {
   }

   public static ObjectName newObjectName(@NonNls String objectName) {
      try {
         return ObjectName.getInstance(objectName);
      } catch (MalformedObjectNameException var2) {
         throw new InternalError("Never happens");
      }
   }
}
