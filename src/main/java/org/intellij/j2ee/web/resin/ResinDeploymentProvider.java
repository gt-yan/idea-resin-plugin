package org.intellij.j2ee.web.resin;

import com.intellij.execution.ExecutionException;
import com.intellij.javaee.appServers.deployment.DeploymentMethod;
import com.intellij.javaee.appServers.deployment.DeploymentModel;
import com.intellij.javaee.appServers.deployment.DeploymentSource;
import com.intellij.javaee.appServers.deployment.DeploymentStatus;
import com.intellij.javaee.appServers.run.configuration.CommonModel;
import com.intellij.javaee.appServers.serverInstances.J2EEServerInstance;
import com.intellij.javaee.util.DeployStateChecker;
import com.intellij.javaee.web.artifact.WebArtifactUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.packaging.artifacts.ArtifactType;
import java.util.Arrays;
import java.util.Collection;
import org.intellij.j2ee.web.resin.resin.ResinConfiguration;
import org.intellij.j2ee.web.resin.resin.WebApp;
import org.intellij.j2ee.web.resin.resin.common.DeploymentProviderEx;
import org.intellij.j2ee.web.resin.resin.configuration.JmxConfigurationStrategy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResinDeploymentProvider extends DeploymentProviderEx {
   private static final Logger LOG = Logger.getInstance(ResinDeploymentProvider.class);
   private static final ResinDeploymentMethod JMX_DEPLOYMENT_METHOD = new ResinDeploymentMethod(ResinBundle.message("ResinDeploymentProvider.deploy.method.jmx.name"), true, true) {
      public boolean isApplicable(@NotNull CommonModel commonModel) {
         if (commonModel == null) {
            $$$reportNull$$$0(0);
         }

         return super.isApplicable(commonModel) && ((ResinModelBase)commonModel.getServerModel()).hasJmxStrategy();
      }

      public void doDeploy(Project project, final J2EEServerInstance instance, final DeploymentModel deploymentModel) {
         final JmxConfigurationStrategy strategy = this.getJmxStrategy(deploymentModel);
         final ResinModelBase serverModel = (ResinModelBase)deploymentModel.getServerModel();
         final WebApp webApp = ResinDeploymentProvider.getWebApp(deploymentModel);
         boolean success = strategy != null && strategy.deployWithJmx(serverModel, webApp);
         if (success) {
            ResinDeploymentProvider.setDeploymentStatus(instance, deploymentModel, DeploymentStatus.UNKNOWN);
            ((ResinServerInstance)instance).getPoller().putDeployStateChecker(new DeployStateChecker() {
               public DeploymentModel getDeploymentModel() {
                  return deploymentModel;
               }

               public boolean check() {
                  Ref<Boolean> isFinal = new Ref(false);
                  ResinDeploymentProvider.setDeploymentStatus(instance, deploymentModel, strategy.getDeployStateWithJmx(serverModel, webApp, isFinal));
                  return (Boolean)isFinal.get();
               }
            });
         } else {
            ResinDeploymentProvider.setDeploymentStatus(instance, deploymentModel, DeploymentStatus.FAILED);
         }

      }

      public void startUndeploy(J2EEServerInstance instance, DeploymentModel deploymentModel) {
         ((ResinServerInstance)instance).getPoller().removeDeployStateChecker(deploymentModel);
         JmxConfigurationStrategy strategy = this.getJmxStrategy(deploymentModel);
         boolean success = strategy != null && strategy.undeployWithJmx((ResinModelBase)deploymentModel.getServerModel(), ResinDeploymentProvider.getWebApp(deploymentModel));
         ResinDeploymentProvider.setDeploymentStatus(instance, deploymentModel, success ? DeploymentStatus.NOT_DEPLOYED : DeploymentStatus.UNKNOWN);
      }

      private @Nullable JmxConfigurationStrategy getJmxStrategy(DeploymentModel deploymentModel) {
         return ((ResinModelBase)deploymentModel.getServerModel()).getJmxStrategy();
      }

      // $FF: synthetic method
      private static void $$$reportNull$$$0(int var0) {
         throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "commonModel", "org/intellij/j2ee/web/resin/ResinDeploymentProvider$1", "isApplicable"));
      }
   };
   public static final ResinDeploymentMethod CONF_DEPLOYMENT_METHOD = new ResinDeploymentMethod(ResinBundle.message("ResinDeploymentProvider.deploy.method.conf.name"), true, false) {
      public void doDeploy(Project project, J2EEServerInstance instance, DeploymentModel deploymentModel) {
         ResinDeploymentProvider.setDeploymentStatus(instance, deploymentModel, DeploymentStatus.DEPLOYED);
      }

      public void startUndeploy(J2EEServerInstance instance, DeploymentModel deploymentModel) {
         boolean success = false;

         try {
            ResinModel resinModel = (ResinModel)deploymentModel.getServerModel();
            ResinConfiguration resinConfiguration = resinModel.getOrCreateResinConfiguration(false);
            WebApp webApp = ResinDeploymentProvider.getWebApp(deploymentModel);
            success = webApp != null && resinConfiguration.undeploy(webApp);
         } catch (ExecutionException var7) {
            ResinDeploymentProvider.LOG.error(var7);
         }

         ResinDeploymentProvider.setDeploymentStatus(instance, deploymentModel, success ? DeploymentStatus.NOT_DEPLOYED : DeploymentStatus.UNKNOWN);
      }
   };
   private static final ResinDeploymentMethod DEFAULT_DEPLOYMENT_METHOD;
   private static final DeploymentMethod[] DEPLOYMENT_METHODS;

   private static ResinDeploymentMethod getDeploymentMethod(DeploymentModel deploymentModel) {
      DeploymentMethod method = deploymentModel.getDeploymentMethod();
      return !(method instanceof ResinDeploymentMethod) ? DEFAULT_DEPLOYMENT_METHOD : (ResinDeploymentMethod)method;
   }

   public void doDeploy(Project project, J2EEServerInstance instance, DeploymentModel deploymentModel) {
      getDeploymentMethod(deploymentModel).doDeploy(project, instance, deploymentModel);
   }

   public DeploymentModel createNewDeploymentModel(CommonModel commonModel, DeploymentSource source) {
      return new ResinModuleDeploymentModel(commonModel, source);
   }

   public SettingsEditor<DeploymentModel> createAdditionalDeploymentSettingsEditor(CommonModel commonModel, DeploymentSource source) {
      ResinModelBase resinModel = (ResinModelBase)commonModel.getServerModel();
      return resinModel.createAdditionalDeploymentSettingsEditor(commonModel, source);
   }

   public Collection<? extends ArtifactType> getSupportedArtifactTypes() {
      return Arrays.asList(WebArtifactUtil.getInstance().getExplodedWarArtifactType(), WebArtifactUtil.getInstance().getWarArtifactType());
   }

   public void startUndeploy(J2EEServerInstance instance, DeploymentModel deploymentModel) {
      getDeploymentMethod(deploymentModel).startUndeploy(instance, deploymentModel);
   }

   public void updateDeploymentStatus(J2EEServerInstance j2EEServerInstance, DeploymentModel deploymentModel) {
   }

   public static @Nullable WebApp getWebApp(DeploymentModel deploymentModel) {
      ResinModuleDeploymentModel resinModel = (ResinModuleDeploymentModel)deploymentModel;
      String filePath = deploymentModel.getDeploymentSource().getFilePath();
      return filePath == null ? null : new WebApp(resinModel.isDefaultContextPath(), resinModel.getContextPath(), resinModel.getHost(), filePath, ((ResinModelBase)deploymentModel.getServerModel()).getCharset());
   }

   public DeploymentMethod[] getAvailableMethods() {
      return DEPLOYMENT_METHODS;
   }

   static {
      DEFAULT_DEPLOYMENT_METHOD = JMX_DEPLOYMENT_METHOD;
      DEPLOYMENT_METHODS = new DeploymentMethod[]{JMX_DEPLOYMENT_METHOD, CONF_DEPLOYMENT_METHOD};
   }

   private abstract static class ResinDeploymentMethod extends DeploymentMethod {
      ResinDeploymentMethod(String name, boolean local, boolean remote) {
         super(name, local, remote);
      }

      public abstract void doDeploy(Project var1, J2EEServerInstance var2, DeploymentModel var3);

      public abstract void startUndeploy(J2EEServerInstance var1, DeploymentModel var2);
   }
}
