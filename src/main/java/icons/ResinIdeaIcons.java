package icons;

import com.intellij.openapi.util.IconLoader;
import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;

public final class ResinIdeaIcons {
   public static final @NotNull Icon Resin = load("/icons/resin.png");

   private static @NotNull Icon load(@NotNull String path) {
      return IconLoader.getIcon(path, ResinIdeaIcons.class);
   }
}
