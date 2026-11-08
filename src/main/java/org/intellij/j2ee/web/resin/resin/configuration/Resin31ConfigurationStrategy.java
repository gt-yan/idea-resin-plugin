package org.intellij.j2ee.web.resin.resin.configuration;

import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.openapi.util.NullableLazyValue;
import org.intellij.j2ee.web.resin.resin.ResinInstallation;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Resin31ConfigurationStrategy extends Resin3XConfigurationStrategy {
   protected static final @NonNls String CLUSTER_ELEMENT = "cluster";
   protected static final @NonNls String CLUSTER_DEFAULT_ELEMENT = "cluster-default";

   public Resin31ConfigurationStrategy(ResinInstallation resinInstallation) {
      super(resinInstallation);
   }

   protected Resin3XConfigurationStrategy.ElementsProvider createElementsProvider() {
      return new Resin31ElementsProvider(this.getElement());
   }

   protected static class Resin31ElementsProvider extends Resin3XConfigurationStrategy.ElementsProvider {
      private final NotNullLazyValue<Element> myCluster = NotNullLazyValue.lazy(() -> {
         return this.getRootElement().getChild("cluster", this.getNS());
      });
      private final NotNullLazyValue<Element> myClusterDefault = NotNullLazyValue.lazy(() -> {
         return this.doGetClusterDefaultElement();
      });
      private final NullableLazyValue<Element> myServerDefault = new NullableLazyValue<Element>() {
         protected Element compute() {
            return Resin31ElementsProvider.this.getClusterElement().getChild("server-default", Resin31ElementsProvider.this.getNS());
         }
      };

      public Resin31ElementsProvider(Element element) {
         super(element);
      }

      public @NotNull Element getClusterElement() {
         Element var10000 = (Element)this.myCluster.getValue();
         if (var10000 == null) {
            $$$reportNull$$$0(0);
         }

         return var10000;
      }

      public @NotNull Element getClusterDefaultElement() {
         Element var10000 = (Element)this.myClusterDefault.getValue();
         if (var10000 == null) {
            $$$reportNull$$$0(1);
         }

         return var10000;
      }

      public @Nullable Element getServerDefaultElement() {
         return (Element)this.myServerDefault.getValue();
      }

      public Element getHostParent() {
         return this.getClusterElement();
      }

      protected Element doGetClusterDefaultElement() {
         return this.getOrCreateChildElement(this.getRootElement(), "cluster-default");
      }

      protected Element doGetParamParent() {
         Element serverElement = this.getServerElement();
         if (serverElement != null) {
            return serverElement;
         } else {
            Element serverDefaultElement = this.getServerDefaultElement();
            return serverDefaultElement != null ? serverDefaultElement : this.getOrCreateChildElement(this.getClusterDefaultElement(), "server-default");
         }
      }

      // $FF: synthetic method
      private static void $$$reportNull$$$0(int var0) {
         Object[] var10001 = new Object[]{"org/intellij/j2ee/web/resin/resin/configuration/Resin31ConfigurationStrategy$Resin31ElementsProvider", null};
         switch (var0) {
            case 0:
            default:
               var10001[1] = "getClusterElement";
               break;
            case 1:
               var10001[1] = "getClusterDefaultElement";
         }

         throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", var10001));
      }
   }
}
