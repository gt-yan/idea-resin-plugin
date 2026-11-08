package org.intellij.j2ee.web.resin;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.javaee.appServers.deployment.DeploymentModel;
import com.intellij.javaee.appServers.deployment.DeploymentSource;
import com.intellij.javaee.appServers.run.configuration.CommonModel;
import com.intellij.javaee.directoryManager.SystemBaseDirectoryManager;
import com.intellij.javaee.jmxremote.JmxRemoteAware;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.intellij.j2ee.web.resin.resin.ResinConfiguration;
import org.intellij.j2ee.web.resin.resin.ResinInstallation;
import org.intellij.j2ee.web.resin.ui.DeploymentSettingsEditor;
import org.intellij.j2ee.web.resin.ui.RunConfigurationEditor;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResinModel extends ResinModelBase<ResinLocalModelData> implements JmxRemoteAware {
   private static final Logger LOG = Logger.getInstance(ResinModel.class);
   public static final @NonNls String DEPLOY_MODE_AUTO = "automatic";
   public static final @NonNls String DEPLOY_MODE_LAZY = "lazy";
   public static final @NonNls String DEPLOY_MODE_MANUAL = "manual";
   private ResinConfiguration myConfiguration;
   private String myJmxUsername;
   private String myJmxPassword;
   private File myAccessFile;
   private File myPasswordFile;

   public SettingsEditor<CommonModel> getEditor() {
      return new RunConfigurationEditor();
   }

   public List<Pair<String, Integer>> getAddressesToCheck() {
      return Collections.singletonList(Pair.create(this.getCommonModel().getHost(), this.getLocalPort()));
   }

   public ResinConfiguration getOrCreateResinConfiguration(boolean forceCreation) throws ExecutionException {
      if (this.myConfiguration == null || forceCreation) {
         this.myConfiguration = new ResinConfiguration(this);
      }

      return this.myConfiguration;
   }

   public void checkConfiguration() throws RuntimeConfigurationException {
      this.findConfFile();
      super.checkConfiguration();
   }

   public File findConfFile() throws RuntimeConfigurationException {
      File sourceConfig = null;
      ResinPersistentData persistentData = this.getHelper().getPersistentData();
      if (persistentData != null && !StringUtil.isEmpty(persistentData.RESIN_CONF)) {
         sourceConfig = new File(persistentData.RESIN_CONF);
      }

      String sourceConfigPath = this.getResinConf();
      if (!StringUtil.isEmpty(sourceConfigPath)) {
         sourceConfig = new File(FileUtil.toSystemDependentName(sourceConfigPath));
      }

      if (sourceConfig == null) {
         throw new RuntimeConfigurationException(ResinBundle.message("message.error.resin.conf.doesnt.chosen"));
      } else if (!sourceConfig.exists()) {
         throw new RuntimeConfigurationException(ResinBundle.message("message.error.resin.conf.doesnt.exist", sourceConfig.getAbsolutePath()));
      } else if (sourceConfig.isDirectory()) {
         throw new RuntimeConfigurationException(ResinBundle.message("message.error.resin.conf.directory", sourceConfig.getAbsolutePath()));
      } else {
         return sourceConfig;
      }
   }

   public boolean isDebugConfiguration() {
      return ((ResinLocalModelData)this.getData()).isDebugConfiguration();
   }

   public void setDebugConfiguration(boolean debugConfiguration) {
      ((ResinLocalModelData)this.getData()).setDebugConfiguration(debugConfiguration);
   }

   public boolean isAutoBuildClassPath() {
      return ((ResinLocalModelData)this.getData()).isAutoBuildClassPath();
   }

   public void setAutoBuildClassPath(boolean autoBuildClassPath) {
      ((ResinLocalModelData)this.getData()).setAutoBuildClassPath(autoBuildClassPath);
   }

   public String getResinConf() {
      return ((ResinLocalModelData)this.getData()).getResinConf();
   }

   public void setResinConf(String resinConf) {
      ((ResinLocalModelData)this.getData()).setResinConf(resinConf);
   }

   public boolean isReadOnlyConfiguration() {
      return ((ResinLocalModelData)this.getData()).isReadOnlyConfiguration();
   }

   public void setReadOnlyConfiguration(boolean readOnlyConfiguration) {
      ((ResinLocalModelData)this.getData()).setReadOnlyConfiguration(readOnlyConfiguration);
   }

   public String getAdditionalParameters() {
      String additionalParameters = ((ResinLocalModelData)this.getData()).getAdditionalParameters();
      return additionalParameters == null ? "" : additionalParameters.trim();
   }

   public void setAdditionalParameters(String additionalParameters) {
      ((ResinLocalModelData)this.getData()).setAdditionalParameters(additionalParameters);
   }

   public String getDeployMode() {
      String deployMode = ((ResinLocalModelData)this.getData()).getDeployMode();
      return deployMode == null ? "manual" : deployMode;
   }

   public void setDeployMode(String deployMode) {
      ((ResinLocalModelData)this.getData()).setDeployMode(deployMode);
   }

   protected ResinLocalModelData createResinModelData() {
      return new ResinLocalModelData();
   }

   private File getWebAppFileDestination(File webAppFile) {
      ResinInstallation installation = this.getInstallation();
      LOG.assertTrue(installation != null);
      File homeDir = installation.getResinHome();
      File webAppsDir = new File(homeDir, "webapps");
      if (!webAppsDir.exists()) {
         LOG.error("Can't find webapps folder");
         return null;
      } else {
         return new File(webAppsDir, webAppFile.getName());
      }
   }

   public boolean transferFile(File webAppFile) {
      File webAppFileDestination = this.getWebAppFileDestination(webAppFile);
      if (webAppFileDestination == null) {
         return false;
      } else {
         try {
            FileUtil.copyFileOrDir(webAppFile, webAppFileDestination);
            return true;
         } catch (IOException var4) {
            LOG.error(var4);
            return false;
         }
      }
   }

   public boolean deleteFile(File webAppFile) {
      File webAppFileDestination = this.getWebAppFileDestination(webAppFile);
      return webAppFileDestination == null ? false : FileUtil.delete(webAppFileDestination);
   }

   public SettingsEditor<DeploymentModel> createAdditionalDeploymentSettingsEditor(CommonModel commonModel, DeploymentSource source) {
      return new DeploymentSettingsEditor(commonModel, source);
   }

   public @Nullable String getJmxUsername() {
      return this.myJmxUsername;
   }

   public void setJmxUsername(@Nullable String jmxUsername) {
      this.myJmxUsername = jmxUsername;
   }

   public @Nullable String getJmxPassword() {
      return this.myJmxPassword;
   }

   public void setJmxPassword(@Nullable String jmxPassword) {
      this.myJmxPassword = jmxPassword;
   }

   public @NotNull SystemBaseDirectoryManager getSystemBaseDirectoryManager() {
      SystemBaseDirectoryManager var10000 = ResinSystemBaseDirectoryManager.getInstance();
      if (var10000 == null) {
         $$$reportNull$$$0(0);
      }

      return var10000;
   }

   public @Nullable String getBaseDirectoryName() {
      return ((ResinLocalModelData)this.getData()).getBaseDirectoryName();
   }

   public void setBaseDirectoryName(@Nullable String baseDirectoryName) {
      ((ResinLocalModelData)this.getData()).setBaseDirectoryName(baseDirectoryName);
   }

   public @Nullable File getAccessFile() {
      return this.myAccessFile;
   }

   public void setAccessFile(@Nullable File accessFile) {
      this.myAccessFile = accessFile;
   }

   public @Nullable File getPasswordFile() {
      return this.myPasswordFile;
   }

   public void setPasswordFile(@Nullable File passwordFile) {
      this.myPasswordFile = passwordFile;
   }

   // $FF: synthetic method
   private static void $$$reportNull$$$0(int var0) {
      throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "org/intellij/j2ee/web/resin/ResinModel", "getSystemBaseDirectoryManager"));
   }
}
