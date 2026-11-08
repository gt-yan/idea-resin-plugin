package org.intellij.j2ee.web.resin;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.javaee.appServers.appServerIntegrations.AppServerIntegration;
import com.intellij.javaee.appServers.run.configuration.J2EEConfigurationFactory;
import com.intellij.javaee.appServers.run.configuration.JavaeeAppServerConfigurationType;
import com.intellij.javaee.appServers.run.configuration.ServerModel;
import com.intellij.openapi.project.Project;
import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResinConfigurationType extends JavaeeAppServerConfigurationType {
   public ResinConfigurationType() {
      super("ResinConfigurationType");
   }

   public @NotNull String getDisplayName() {
      String var10000 = ResinBundle.message("run.config.tab.title.resin");
      if (var10000 == null) {
         $$$reportNull$$$0(0);
      }

      return var10000;
   }

   public String getConfigurationTypeDescription() {
      return ResinBundle.message("run.config.tab.description.resin");
   }

   public Icon getIcon() {
      return ResinManager.ICON_RESIN;
   }

   public String getHelpTopic() {
      return "reference.dialogs.rundebug.ResinConfigurationType";
   }

   public RunConfiguration createJ2EEConfigurationTemplate(ConfigurationFactory factory, Project project, boolean isLocal) {
      return J2EEConfigurationFactory.getInstance().createJ2EERunConfiguration(factory, project, (ServerModel)(isLocal ? new ResinModel() : new ResinRemoteModel()), ResinManager.getInstance(), isLocal, isLocal ? new ResinStartupPolicy() : null);
   }

   public @Nullable AppServerIntegration getIntegration() {
      return ResinManager.getInstance();
   }

   public static ResinConfigurationType getInstance() {
      return (ResinConfigurationType)ConfigurationTypeUtil.findConfigurationType(ResinConfigurationType.class);
   }

   // $FF: synthetic method
   private static void $$$reportNull$$$0(int var0) {
      throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "org/intellij/j2ee/web/resin/ResinConfigurationType", "getDisplayName"));
   }
}
