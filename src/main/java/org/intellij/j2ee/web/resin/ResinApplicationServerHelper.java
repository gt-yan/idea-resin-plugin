package org.intellij.j2ee.web.resin;

import com.intellij.execution.ExecutionException;
import com.intellij.javaee.appServers.appServerIntegrations.ApplicationServerHelper;
import com.intellij.javaee.appServers.appServerIntegrations.ApplicationServerInfo;
import com.intellij.javaee.appServers.appServerIntegrations.ApplicationServerPersistentData;
import com.intellij.javaee.appServers.appServerIntegrations.ApplicationServerPersistentDataEditor;
import com.intellij.javaee.appServers.appServerIntegrations.CantFindApplicationServerJarsException;
import java.io.File;
import org.intellij.j2ee.web.resin.resin.ResinInstallation;
import org.intellij.j2ee.web.resin.ui.SelectResinLocationEditor;

public class ResinApplicationServerHelper implements ApplicationServerHelper {
   public ApplicationServerInfo getApplicationServerInfo(ApplicationServerPersistentData persistentData) throws CantFindApplicationServerJarsException {
      try {
         ResinPersistentData resinPersistentData = (ResinPersistentData)persistentData;
         ResinInstallation resinInstallation = ResinInstallation.create(resinPersistentData.RESIN_HOME);
         File[] resinLib = resinInstallation.getLibFiles(resinPersistentData.INCLUDE_ALL_JARS);
         String version = resinInstallation.getDisplayName();
         return new ApplicationServerInfo(resinLib, version);
      } catch (ExecutionException var6) {
         throw new CantFindApplicationServerJarsException(var6.getMessage());
      }
   }

   public ApplicationServerPersistentData createPersistentDataEmptyInstance() {
      return new ResinPersistentData();
   }

   public ApplicationServerPersistentDataEditor createConfigurable() {
      return new SelectResinLocationEditor();
   }
}
