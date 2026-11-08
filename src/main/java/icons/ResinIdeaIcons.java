package icons;

import com.intellij.ui.IconManager;
import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;

public final class ResinIdeaIcons {
   public static final @NotNull Icon Resin = load("icons/resin.png", 0, 7);

   private static @NotNull Icon load(@NotNull String path, int cacheKey, int flags) {
      if (path == null) {
         $$$reportNull$$$0(0);
      }

      Icon var10000 = IconManager.getInstance().loadRasterizedIcon(path, ResinIdeaIcons.class.getClassLoader(), cacheKey, flags);
      if (var10000 == null) {
         $$$reportNull$$$0(1);
      }

      return var10000;
   }

   // $FF: synthetic method
   private static void $$$reportNull$$$0(int index) {
      if (index == 1) {
         throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "icons/ResinIdeaIcons", "load"));
      } else {
         throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "path", "icons/ResinIdeaIcons", "load"));
      }
   }
}
