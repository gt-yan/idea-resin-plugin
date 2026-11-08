package org.intellij.j2ee.web.resin.resin;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.javaee.appServers.deployment.DeploymentModel;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.JDOMUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.intellij.j2ee.web.resin.ResinBundle;
import org.intellij.j2ee.web.resin.ResinDeploymentProvider;
import org.intellij.j2ee.web.resin.ResinModel;
import org.intellij.j2ee.web.resin.resin.configuration.ResinConfigurationStrategy;
import org.intellij.j2ee.web.resin.resin.configuration.ResinGeneratedConfig;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class ResinConfiguration {
   private static final Logger LOG = Logger.getInstance(ResinConfiguration.class);
   private static final @NonNls String JAVAC_ELEMENT = "javac";
   private static final @NonNls String ARGS_ATTRIBUTE = "args";
   private static final @NonNls String COMPILER_ATTRIBUTE = "compiler";
   private static final @NonNls String COMPILER_ATTRIBUTE_VALUE = "internal";
   private static final @NonNls String ARGS_ATTRIBUTE_VALUE = "-source 1.8";
   private static final @NonNls String ARGS_ATTRIBUTE_VALUE_PREFIX = "-g";
   private final ResinInstallation myInstallation;
   private final ResinGeneratedConfig myGeneratedConfig;
   private final File mySourceConfig;
   private final ResinConfigurationStrategy myStrategy;

   public ResinConfiguration(ResinModel serverModel) throws ExecutionException {
      ResinPersistentDataHelper helper = serverModel.getHelper();
      this.myInstallation = helper.getInstallationOrError();
      LOG.assertTrue(this.myInstallation != null);

      try {
         this.mySourceConfig = serverModel.findConfFile();
      } catch (RuntimeConfigurationException var7) {
         throw new ExecutionException(var7.getMessageHtml().toString());
      }

      this.myStrategy = helper.getStrategy();
      LOG.assertTrue(this.myStrategy != null);
      if (serverModel.isReadOnlyConfiguration()) {
         this.myGeneratedConfig = null;
      } else {
         try {
            Element document;
            if (this.mySourceConfig.length() == 0L) {
               InputStream is = this.myStrategy.getDefaultResinConfContent();
               if (is == null) {
                  throw new ExecutionException(ResinBundle.message("run.resin.conf.doesnt.exist"));
               }

               document = JDOMUtil.load(is);
            } else {
               document = JDOMUtil.load(this.mySourceConfig);
            }

            this.myGeneratedConfig = new ResinGeneratedConfig(document, "resin");
            patchConfigToMakeDebuggerWork(document);
            this.myStrategy.init(serverModel, document);
            this.myStrategy.setPort(serverModel.getPort());
         } catch (IOException | JDOMException var6) {
            throw new ExecutionException(ResinBundle.message("run.resin.conf.load.error"), var6);
         }

         Iterator var8 = serverModel.getCommonModel().getDeploymentModels().iterator();

         while(var8.hasNext()) {
            DeploymentModel model = (DeploymentModel)var8.next();
            if (model.getDeploymentMethod() == ResinDeploymentProvider.CONF_DEPLOYMENT_METHOD) {
               WebApp webApp = ResinDeploymentProvider.getWebApp(model);
               if (webApp != null) {
                  this.deploy(webApp);
               }
            }
         }

         this.save();
      }

   }

   public String getServerId() {
      return this.myStrategy.getServerId();
   }

   private static void patchConfigToMakeDebuggerWork(@NotNull Element element) {
      if (element == null) {
         $$$reportNull$$$0(0);
      }

      Element javac = element.getChild("javac", element.getNamespace());
      if (javac == null) {
         javac = new Element("javac", element.getNamespace());
         javac.setAttribute("compiler", "internal");
         javac.setAttribute("args", ARGS_ATTRIBUTE_VALUE);
         element.addContent(javac);
      }

      Attribute args = javac.getAttribute("args");
      if (args == null) {
         javac.setAttribute("args", "-g " + ARGS_ATTRIBUTE_VALUE);
      } else if (!args.getValue().contains("-g")) {
         args.setValue("-g " + args.getValue());
      }

   }

   public ResinInstallation getInstallation() {
      return this.myInstallation;
   }

   private boolean isWritable() {
      return this.myGeneratedConfig != null;
   }

   public File getConfigFile() {
      return this.isWritable() ? this.myGeneratedConfig.getFile() : this.mySourceConfig;
   }

   public void deploy(WebApp webApp) throws ExecutionException {
      if (this.isWritable()) {
         this.myStrategy.deploy(webApp);
         this.save();
      }
   }

   public boolean undeploy(WebApp webApp) throws ExecutionException {
      if (!this.isWritable()) {
         return false;
      } else {
         boolean result = this.myStrategy.undeploy(webApp);
         this.save();
         return result;
      }
   }

   private void save() throws ExecutionException {
      this.myGeneratedConfig.save();
      this.myStrategy.save();
   }

   // $FF: synthetic method
   private static void $$$reportNull$$$0(int var0) {
      throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "element", "org/intellij/j2ee/web/resin/resin/ResinConfiguration", "patchConfigToMakeDebuggerWork"));
   }
}
