package org.intellij.j2ee.web.resin;

import com.intellij.javaee.appServers.appServerIntegrations.AppServerDeployedFileUrlProvider;
import com.intellij.javaee.appServers.appServerIntegrations.AppServerIntegration;
import com.intellij.javaee.appServers.appServerIntegrations.ApplicationServerHelper;
import com.intellij.javaee.appServers.appServerIntegrations.ApplicationServerUrlMapping;
import com.intellij.javaee.appServers.context.FacetContextProvider;
import com.intellij.javaee.appServers.deployment.DeploymentProvider;
import com.intellij.javaee.appServers.openapi.ex.AppServerIntegrationsManager;
import com.intellij.javaee.web.WebFacetContextProvider;
import icons.ResinIdeaIcons;
import java.util.List;
import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;

public class ResinManager extends AppServerIntegration {
   private final ResinDeploymentProvider deploymentProvider = new ResinDeploymentProvider();
   public static final Icon ICON_RESIN;
   private final ApplicationServerHelper resinApplicationServerHelper = new ResinApplicationServerHelper();
   private ApplicationServerUrlMapping myUrlMapping;

   public Icon getIcon() {
      return ICON_RESIN;
   }

   public String getPresentableName() {
      return ResinBundle.message("resin.application.server.name");
   }

   public DeploymentProvider getDeploymentProvider(boolean local) {
      return local ? this.deploymentProvider : null;
   }

   public static ResinManager getInstance() {
      return (ResinManager)AppServerIntegrationsManager.getInstance().getIntegration(ResinManager.class);
   }

   public ApplicationServerHelper getApplicationServerHelper() {
      return this.resinApplicationServerHelper;
   }

   public @NotNull AppServerDeployedFileUrlProvider getDeployedFileUrlProvider() {
      if (this.myUrlMapping == null) {
         this.myUrlMapping = new ApplicationServerUrlMapping() {
            protected void collectFacetContextProviders(List<FacetContextProvider> facetContextProvider) {
               facetContextProvider.add(new WebFacetContextProvider());
            }
         };
      }

      ApplicationServerUrlMapping var10000 = this.myUrlMapping;
      if (var10000 == null) {
         $$$reportNull$$$0(0);
      }

      return var10000;
   }

   static {
      ICON_RESIN = ResinIdeaIcons.Resin;
   }

   // $FF: synthetic method
   private static void $$$reportNull$$$0(int var0) {
      throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "org/intellij/j2ee/web/resin/ResinManager", "getDeployedFileUrlProvider"));
   }
}
