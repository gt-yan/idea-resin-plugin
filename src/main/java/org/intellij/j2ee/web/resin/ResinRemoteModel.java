package org.intellij.j2ee.web.resin;

import com.intellij.execution.configurations.RuntimeConfigurationError;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.javaee.appServers.deployment.DeploymentModel;
import com.intellij.javaee.appServers.deployment.DeploymentSource;
import com.intellij.javaee.appServers.run.configuration.CommonModel;
import com.intellij.javaee.transport.TransportHost;
import com.intellij.javaee.transport.TransportHostTarget;
import com.intellij.javaee.transport.TransportManager;
import com.intellij.javaee.transport.TransportTarget;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import java.io.File;
import java.util.Collections;
import java.util.List;
import org.intellij.j2ee.web.resin.ui.RemoteRunConfigurationEditor;
import org.jetbrains.annotations.Nullable;

public class ResinRemoteModel extends ResinModelBase<ResinRemoteModelData> {
   public SettingsEditor<CommonModel> getEditor() {
      return new RemoteRunConfigurationEditor(this.getProject());
   }

   public List<Pair<String, Integer>> getAddressesToCheck() {
      return Collections.emptyList();
   }

   public void checkConfiguration() throws RuntimeConfigurationException {
      if (!this.hasJmxStrategy() && this.getCommonModel().getDeploymentModels().size() > 0) {
         throw new RuntimeConfigurationError("Remote deployment is not supported for Resin 2.x");
      } else {
         super.checkConfiguration();
      }
   }

   public String getTransportHostId() {
      return ((ResinRemoteModelData)this.getData()).getTransportHostId();
   }

   public void setTransportHostId(String transportHostId) {
      ((ResinRemoteModelData)this.getData()).setTransportHostId(transportHostId);
   }

   public TransportTarget getTransportTargetWebApps() {
      return ((ResinRemoteModelData)this.getData()).getTransportTargetWebApps();
   }

   public void setTransportTargetWebApps(TransportTarget transportTargetWebApps) {
      ((ResinRemoteModelData)this.getData()).setTransportTargetWebApps(transportTargetWebApps);
   }

   protected ResinRemoteModelData createResinModelData() {
      return new ResinRemoteModelData();
   }

   private @Nullable TransportHost getHost() {
      return TransportManager.getInstance().findHost(this.getTransportHostId(), this.getProject());
   }

   private @Nullable TransportHostTarget getTransportHostTarget() {
      TransportHost host = this.getHost();
      return host == null ? null : host.findOrCreateHostTarget(this.getTransportTargetWebApps());
   }

   public boolean transferFile(File webAppFile) {
      TransportHostTarget target = this.getTransportHostTarget();
      return target != null && target.transfer(this.getProject(), Collections.singletonList(LocalFileSystem.getInstance().refreshAndFindFileByIoFile(webAppFile)));
   }

   public boolean deleteFile(File webAppFile) {
      TransportHostTarget target = this.getTransportHostTarget();
      VirtualFile vFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(webAppFile);
      if (vFile == null) {
         return true;
      } else {
         return target != null && target.delete(this.getProject(), Collections.singletonList(vFile));
      }
   }

   public @Nullable SettingsEditor<DeploymentModel> createAdditionalDeploymentSettingsEditor(CommonModel commonModel, DeploymentSource source) {
      return null;
   }
}
