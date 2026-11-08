package org.intellij.j2ee.web.resin.ui;

import com.intellij.javaee.appServers.appServerIntegrations.ApplicationServer;
import com.intellij.javaee.appServers.run.configuration.ApplicationServerSelectionListener;
import com.intellij.javaee.appServers.run.configuration.CommonModel;
import com.intellij.openapi.options.SettingsEditor;
import org.intellij.j2ee.web.resin.ResinModelBase;
import org.intellij.j2ee.web.resin.resin.ResinPersistentDataHelper;
import org.jetbrains.annotations.Nullable;

public abstract class ResinRunConfigurationEditorBase extends SettingsEditor<CommonModel> implements ApplicationServerSelectionListener {
   public void serverSelected(@Nullable ApplicationServer server) {
      this.serverChanged(server);
   }

   public void serverProbablyEdited(@Nullable ApplicationServer server) {
      this.serverChanged(server);
   }

   private void serverChanged(@Nullable ApplicationServer server) {
      this.setJmxPortVisible((new ResinPersistentDataHelper(server)).hasJmxStrategy());
   }

   protected final void updateJmxPortVisible(ResinModelBase model) {
      this.setJmxPortVisible(model.hasJmxStrategy());
   }

   protected abstract void setJmxPortVisible(boolean var1);
}
