package org.intellij.j2ee.web.resin.resin;

import com.intellij.execution.ExecutionException;
import com.intellij.javaee.appServers.appServerIntegrations.ApplicationServer;
import com.intellij.openapi.diagnostic.Logger;
import org.intellij.j2ee.web.resin.ResinPersistentData;
import org.intellij.j2ee.web.resin.resin.configuration.JmxConfigurationStrategy;
import org.intellij.j2ee.web.resin.resin.configuration.ResinConfigurationStrategy;
import org.jetbrains.annotations.Nullable;

public class ResinPersistentDataHelper {
   private static final Logger LOG = Logger.getInstance(ResinPersistentDataHelper.class);
   private final ApplicationServer myApplicationServer;

   public ResinPersistentDataHelper(ApplicationServer applicationServer) {
      this.myApplicationServer = applicationServer;
   }

   public @Nullable ResinPersistentData getPersistentData() {
      return this.myApplicationServer != null ? (ResinPersistentData)this.myApplicationServer.getPersistentData() : null;
   }

   public @Nullable ResinInstallation getInstallation() {
      try {
         return this.getInstallationOrError();
      } catch (ExecutionException var2) {
         LOG.debug(var2);
         return null;
      }
   }

   public @Nullable ResinInstallation getInstallationOrError() throws ExecutionException {
      ResinPersistentData persistentData = this.getPersistentData();
      return persistentData != null ? ResinInstallation.create(persistentData.RESIN_HOME) : null;
   }

   @Nullable ResinConfigurationStrategy getStrategy() {
      ResinInstallation installation = this.getInstallation();
      return installation == null ? null : ResinConfigurationStrategy.getForInstallation(installation);
   }

   public @Nullable JmxConfigurationStrategy getJmxStrategy() {
      ResinConfigurationStrategy strategy = this.getStrategy();
      return strategy instanceof JmxConfigurationStrategy ? (JmxConfigurationStrategy)strategy : null;
   }

   public boolean hasJmxStrategy() {
      return this.getJmxStrategy() != null;
   }
}
