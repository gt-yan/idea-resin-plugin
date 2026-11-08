package org.intellij.j2ee.web.resin;

import com.intellij.DynamicBundle;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public final class ResinBundle {
   public static final @NonNls String BUNDLE = "messages.ResinBundle";
   private static final DynamicBundle INSTANCE = new DynamicBundle(ResinBundle.class, "messages.ResinBundle");

   private ResinBundle() {
   }

   public static @NotNull @Nls String message(@NotNull @PropertyKey(
   resourceBundle = "messages.ResinBundle"
) String key, @NotNull Object... params) {
      if (key == null) {
         $$$reportNull$$$0(0);
      }

      if (params == null) {
         $$$reportNull$$$0(1);
      }

      String var10000 = INSTANCE.getMessage(key, params);
      if (var10000 == null) {
         $$$reportNull$$$0(2);
      }

      return var10000;
   }

   public static @NotNull Supplier<@Nls String> messagePointer(@NotNull @PropertyKey(
   resourceBundle = "messages.ResinBundle"
) String key, @NotNull Object... params) {
      if (key == null) {
         $$$reportNull$$$0(3);
      }

      if (params == null) {
         $$$reportNull$$$0(4);
      }

      Supplier var10000 = INSTANCE.getLazyMessage(key, params);
      if (var10000 == null) {
         $$$reportNull$$$0(5);
      }

      return var10000;
   }

   // $FF: synthetic method
   private static void $$$reportNull$$$0(int index) {
      switch (index) {
         case 2:
            throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "org/intellij/j2ee/web/resin/ResinBundle", "message"));
         case 5:
            throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "org/intellij/j2ee/web/resin/ResinBundle", "messagePointer"));
         case 1:
         case 4:
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "params", "org/intellij/j2ee/web/resin/ResinBundle", index == 4 ? "messagePointer" : "message"));
         default:
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "key", "org/intellij/j2ee/web/resin/ResinBundle", index == 3 ? "messagePointer" : "message"));
      }
   }
}
