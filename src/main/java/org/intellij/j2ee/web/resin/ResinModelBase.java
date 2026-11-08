package org.intellij.j2ee.web.resin;

import com.intellij.execution.configurations.RuntimeConfigurationError;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.javaee.appServers.appServerIntegrations.ApplicationServerUrlMapping;
import com.intellij.javaee.appServers.deployment.DeploymentModel;
import com.intellij.javaee.appServers.deployment.DeploymentProvider;
import com.intellij.javaee.appServers.deployment.DeploymentSource;
import com.intellij.javaee.appServers.run.configuration.CommonModel;
import com.intellij.javaee.appServers.run.configuration.ServerModel;
import com.intellij.javaee.appServers.run.execution.DefaultOutputProcessor;
import com.intellij.javaee.appServers.run.execution.OutputProcessor;
import com.intellij.javaee.appServers.serverInstances.J2EEServerInstance;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.util.xmlb.SkipDefaultValuesSerializationFilters;
import com.intellij.util.xmlb.XmlSerializer;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.intellij.j2ee.web.resin.resin.ResinInstallation;
import org.intellij.j2ee.web.resin.resin.ResinPersistentDataHelper;
import org.intellij.j2ee.web.resin.resin.configuration.JmxConfigurationStrategy;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ResinModelBase<D extends ResinModelDataBase> implements ServerModel {
   private D myData = this.createResinModelData();
   private CommonModel myCommonModel;

   public final int getDefaultPort() {
      return 80;
   }

   public final void setCommonModel(CommonModel commonModel) {
      this.myCommonModel = commonModel;
   }

   public final CommonModel getCommonModel() {
      return this.myCommonModel;
   }

   public final J2EEServerInstance createServerInstance() {
      return new ResinServerInstance(this.myCommonModel);
   }

   public final DeploymentProvider getDeploymentProvider() {
      return new ResinDeploymentProvider();
   }

   public final @NotNull String getDefaultUrlForBrowser() {
      ApplicationServerUrlMapping urlMapping = (ApplicationServerUrlMapping)this.myCommonModel.getIntegration().getDeployedFileUrlProvider();
      String var10000 = urlMapping.getDefaultUrlForServerConfig(this.myCommonModel);
      if (var10000 == null) {
         $$$reportNull$$$0(0);
      }

      return var10000;
   }

   public final OutputProcessor createOutputProcessor(ProcessHandler processHandler, J2EEServerInstance j2EEServerInstance) {
      return new DefaultOutputProcessor(processHandler);
   }

   public final Project getProject() {
      return this.myCommonModel.getProject();
   }

   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }

   protected final D getData() {
      return this.myData;
   }

   public int getLocalPort() {
      return this.getData().getPort();
   }

   public int getPort() {
      return this.getData().getPort();
   }

   public void setPort(int port) {
      this.getData().setPort(port);
   }

   public int getJmxPort() {
      return this.getData().getJmxPort();
   }

   public void setJmxPort(int jmxPort) {
      this.getData().setJmxPort(jmxPort);
   }

   public String getCharset() {
      String charset = this.getData().getCharset();
      return charset == null ? "" : charset.trim();
   }

   public void setCharset(String charset) {
      this.getData().setCharset(charset);
   }

   public void checkConfiguration() throws RuntimeConfigurationException {
      Set<String> contexts = new HashSet();
      Iterator var2 = this.getCommonModel().getDeploymentModels().iterator();

      ResinModuleDeploymentModel model;
      String contextPath;
      do {
         if (!var2.hasNext()) {
            return;
         }

         DeploymentModel deploymentModel = (DeploymentModel)var2.next();
         model = (ResinModuleDeploymentModel)deploymentModel;
         contextPath = model.getContextPath();
      } while(model.isDefaultContextPath() || contexts.add(contextPath));

      throw new RuntimeConfigurationError(ResinBundle.message("error.duplicate.context.path.text", contextPath));
   }

   public final void readExternal(Element element) throws InvalidDataException {
      D data = this.createResinModelData();
      XmlSerializer.deserializeInto(data, element);
      this.myData = data;
   }

   public final void writeExternal(Element element) throws WriteExternalException {
      XmlSerializer.serializeInto(this.myData, element, new SkipDefaultValuesSerializationFilters());
   }

   public ResinPersistentDataHelper getHelper() {
      return new ResinPersistentDataHelper(this.getCommonModel().getApplicationServer());
   }

   public @Nullable ResinInstallation getInstallation() {
      return this.getHelper().getInstallation();
   }

   public @Nullable JmxConfigurationStrategy getJmxStrategy() {
      return this.getHelper().getJmxStrategy();
   }

   public boolean hasJmxStrategy() {
      return this.getHelper().hasJmxStrategy();
   }

   protected abstract D createResinModelData();

   public abstract boolean transferFile(File var1);

   public abstract boolean deleteFile(File var1);

   public abstract @Nullable SettingsEditor<DeploymentModel> createAdditionalDeploymentSettingsEditor(CommonModel var1, DeploymentSource var2);

   public @Nullable String getJmxUsername() {
      return null;
   }

   public @Nullable String getJmxPassword() {
      return null;
   }

   // $FF: synthetic method
   private static void $$$reportNull$$$0(int var0) {
      throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "org/intellij/j2ee/web/resin/ResinModelBase", "getDefaultUrlForBrowser"));
   }
}
