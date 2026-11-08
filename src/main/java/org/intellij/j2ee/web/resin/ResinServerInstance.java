package org.intellij.j2ee.web.resin;

import com.intellij.debugger.DebuggerManager;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.debugger.engine.DebugProcessListener;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.javaee.appServers.run.configuration.CommonModel;
import com.intellij.javaee.appServers.serverInstances.DefaultJ2EEServerEvent;
import com.intellij.javaee.appServers.serverInstances.DefaultServerInstance;
import com.intellij.javaee.util.ServerInstancePoller;
import com.intellij.javaee.web.debugger.engine.DefaultJSPPositionManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import org.intellij.j2ee.web.resin.resin.ResinConfiguration;
import org.intellij.j2ee.web.resin.resin.ResinInstallation;
import org.intellij.j2ee.web.resin.resin.version.ResinVersion;
import org.jetbrains.annotations.NotNull;

public class ResinServerInstance extends DefaultServerInstance {
   private static final Logger LOG = Logger.getInstance(ResinServerInstance.class);
   private final ServerInstancePoller myPoller = new ServerInstancePoller();

   public ResinServerInstance(CommonModel runConfiguration) {
      super(runConfiguration);
   }

   public ServerInstancePoller getPoller() {
      return this.myPoller;
   }

   public void start(final ProcessHandler processHandler) {
      super.start(processHandler);
      this.fireServerListeners(new DefaultJ2EEServerEvent(true, false));
      final ResinModelBase resinModel = (ResinModelBase)this.getServerModel();
      DebuggerManager.getInstance(resinModel.getProject()).addDebugProcessListener(processHandler, new DebugProcessListener() {
         public void processAttached(@NotNull DebugProcess process) {
            if (process == null) {
               $$$reportNull$$$0(0);
            }

            if (resinModel instanceof ResinModel) {
               try {
                  ResinModel resinLocalModel = (ResinModel)resinModel;
                  if (resinLocalModel.isDebugConfiguration()) {
                     ResinConfiguration configuration = resinLocalModel.getOrCreateResinConfiguration(false);
                     File configFile = configuration.getConfigFile();
                     StringWriter sw = new StringWriter();
                     sw.append("\n---\n");
                     sw.append(ResinBundle.message("message.text.resin.conf.debug", configFile.getAbsolutePath()));
                     sw.append("\n");
                     sw.append(FileUtil.loadFile(configFile));
                     sw.append("\n---\n");
                     processHandler.notifyTextAvailable(sw.toString(), ProcessOutputTypes.SYSTEM);
                  }
               } catch (IOException | ExecutionException var6) {
                  ResinServerInstance.LOG.error(var6);
               }
            }

            ResinInstallation installation = resinModel.getInstallation();
            if (installation != null && installation.getVersion() != ResinVersion.VERSION_2_X) {
               process.appendPositionManager(new DefaultJSPPositionManager(process, DefaultServerInstance.getScopeFacets(ResinServerInstance.this.getCommonModel())) {
                  protected String getGeneratedClassesPackage() {
                     return "_jsp";
                  }
               });
            }

         }

         // $FF: synthetic method
         private static void $$$reportNull$$$0(int var0) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "process", "org/intellij/j2ee/web/resin/ResinServerInstance$1", "processAttached"));
         }
      });
      this.myPoller.onInstanceStart();
   }

   public void shutdown() {
      this.myPoller.onInstanceShutdown();
      super.shutdown();
      ProcessHandler processHandler = this.getProcessHandler();
      if (processHandler instanceof OSProcessHandler) {
         ((OSProcessHandler)processHandler).getProcess().destroy();
      }

   }
}
