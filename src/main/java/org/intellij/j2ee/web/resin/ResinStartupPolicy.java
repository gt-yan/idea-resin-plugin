package org.intellij.j2ee.web.resin;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.javaee.appServers.deployment.DeploymentModel;
import com.intellij.javaee.appServers.run.configuration.CommonModel;
import com.intellij.javaee.appServers.run.configuration.JavaCommandLineStartupPolicy;
import com.intellij.javaee.artifact.JavaeeArtifactUtil;
import com.intellij.javaee.jmxremote.JmxRemotePrepareResult;
import com.intellij.javaee.jmxremote.JmxRemoteUtil;
import com.intellij.javaee.web.facet.WebFacet;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.packaging.artifacts.Artifact;
import com.intellij.util.PathsList;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.intellij.j2ee.web.resin.resin.ResinConfiguration;
import org.intellij.j2ee.web.resin.resin.ResinInstallation;
import org.intellij.j2ee.web.resin.resin.version.ResinVersion;
import org.jetbrains.annotations.NonNls;

public class ResinStartupPolicy implements JavaCommandLineStartupPolicy {
   private static final Logger LOG = Logger.getInstance(ResinStartupPolicy.class);
   private static final @NonNls String RESIN_RUN_PROP_FILE = "ResinRun.properties";
   private static final @NonNls String DEBUG_VM_PARAMS_PROP = "resin.debug.vm.param";
   private static final @NonNls String JMX_VM_PARAMS_PROP = "resin.jmx.vm.param";
   private static final @NonNls String RESINHOME_VM_PARAMS_PROP = "resin.home.vm.param";
   private static final @NonNls String JAVA_LIB_PATH_VM_PARAMS_PROP = "java.lib.path.vm.param";
   private static final @NonNls String SERVER_ID_VM_PARAMS_PROP = "server.id.vm.param";
   private static final @NonNls String COMMAND_LINE_CONF_ARG_PROP = "resin.command.line.conf.arg.name";
   private static final @NonNls String COMMAND_LINE_SERVER_ID_ARG_PROP = "resin.command.line.server.id.arg.name";
   private static final @NonNls String RESIN_VERSIONS_INVALID_PATHS_PROP = "resin.versions.not.allow.white.spaces";
   private static final @NonNls String RESIN_PROPERTIES_FILE_PATH = "/conf/resin.properties";
   private static final @NonNls String RESIN_JVM_ARGS_PROP = "jvm_args";
   private Properties resinRunProps = null;

   public JavaParameters createCommandLine(CommonModel commonModel) throws ExecutionException {
      ResinModel resinModel = (ResinModel)commonModel.getServerModel();
      JmxRemotePrepareResult prepareResult;
      if (resinModel.hasJmxStrategy()) {
         prepareResult = JmxRemoteUtil.prepare(resinModel);
         JmxRemoteUtil.apply(resinModel, prepareResult);
      } else {
         prepareResult = null;
      }

      if (prepareResult == null) {
         resinModel.setAccessFile((File)null);
         resinModel.setPasswordFile((File)null);
      } else {
         resinModel.setAccessFile(prepareResult.getAccessFile());
         resinModel.setPasswordFile(prepareResult.getPasswordFile());
      }

      ResinConfiguration resinConfiguration = resinModel.getOrCreateResinConfiguration(true);
      ResinInstallation installation = resinConfiguration.getInstallation();
      String homePath = FileUtil.toSystemDependentName(installation.getResinHome().getPath());
      JavaParameters parameters = new JavaParameters();
      String charset = resinModel.getCharset();
      if (charset != null && !charset.isEmpty()) {
         parameters.setCharset(Charset.forName(resinModel.getCharset()));
      }

      if (resinConfiguration.getInstallation().getVersion().allowXdebug()) {
         this.loadResinRunProp("resin.debug.vm.param", parameters);
      }

      ResinVersion resinVersion = resinConfiguration.getInstallation().getVersion();
      parameters.setWorkingDirectory(homePath);
      parameters.setMainClass(resinVersion.getStartupClass());
      if (resinModel.hasJmxStrategy()) {
         this.loadResinRunProp("resin.jmx.vm.param", parameters, String.valueOf(resinModel.getJmxPort()));
         JmxRemoteUtil.apply(parameters.getVMParametersList(), prepareResult);
      }

      this.loadResinRunProp("resin.home.vm.param", parameters, homePath);
      if (homePath.indexOf(32) != -1 && !this.allowsRunWithWhiteSpace(resinVersion)) {
         throw new ExecutionException(ResinBundle.message("resin.run.error.invalid.path", homePath));
      } else {
         this.loadResinRunProp("java.lib.path.vm.param", parameters, homePath);
         ParametersList parametersList = parameters.getProgramParametersList();
         parametersList.add(this.getResinRunProperty("resin.command.line.conf.arg.name")[0], resinConfiguration.getConfigFile().getAbsolutePath());
         String serverId = resinConfiguration.getServerId();
         if (!StringUtil.isEmpty(serverId)) {
            parametersList.add(this.getResinRunProperty("resin.command.line.server.id.arg.name")[0], serverId);
            this.loadResinRunProp("server.id.vm.param", parameters, serverId);
         }

         String additionalParameters = resinModel.getAdditionalParameters();
         if (additionalParameters != null && !additionalParameters.isEmpty()) {
            parametersList.addParametersString(additionalParameters);
         }

         loadResinConfProperties(homePath, parameters);
         PathsList classpath = parameters.getClassPath();
         VirtualFile[] files = commonModel.getApplicationServer().getLibrary().getFiles(OrderRootType.CLASSES);
         VirtualFile[] var15 = files;
         int var16 = files.length;

         int var17;
         VirtualFile vfile;
         for(var17 = 0; var17 < var16; ++var17) {
            vfile = var15[var17];
            classpath.add(vfile.getPresentableUrl());
         }

         File[] allJars = resinConfiguration.getInstallation().getLibFiles(true);
         File[] var26 = allJars;
         var17 = allJars.length;

         for(int var29 = 0; var29 < var17; ++var29) {
            File jar = var26[var29];
            String s = jar.getAbsolutePath();
            if (!classpath.getPathList().contains(s)) {
               classpath.add(s);
            }
         }

         if (resinModel.isAutoBuildClassPath()) {
            Collection<VirtualFile> outputAndLibs = new ArrayList();
            Iterator var28 = commonModel.getDeploymentModels().iterator();

            label84:
            while(true) {
               Artifact artifact;
               do {
                  if (!var28.hasNext()) {
                     var28 = outputAndLibs.iterator();

                     while(var28.hasNext()) {
                        vfile = (VirtualFile)var28.next();
                        classpath.add(vfile);
                     }
                     break label84;
                  }

                  DeploymentModel model = (DeploymentModel)var28.next();
                  artifact = model.getArtifact();
               } while(artifact == null);

               Collection<WebFacet> webFacets = JavaeeArtifactUtil.getInstance().getFacetsIncludedInArtifact(commonModel.getProject(), artifact, WebFacet.ID);
               Iterator var21 = webFacets.iterator();

               while(var21.hasNext()) {
                  WebFacet webFacet = (WebFacet)var21.next();
                  ModuleRootManager mrm = ModuleRootManager.getInstance(webFacet.getModule());
                  VirtualFile[] roots = mrm.orderEntries().withoutSdk().recursively().exportedOnly().getClassesRoots();
                  Collections.addAll(outputAndLibs, roots);
               }
            }
         }

         return parameters;
      }
   }

   private boolean allowsRunWithWhiteSpace(ResinVersion resinVersion) throws ExecutionException {
      String[] value = this.getResinRunProperty("resin.versions.not.allow.white.spaces");
      List invalids = Arrays.asList(value);
      String verNumber = resinVersion.getVersionNumber();
      if (invalids.contains(verNumber)) {
         return false;
      } else {
         String[] tocheck = verNumber.split("\\.");
         String[] var6 = tocheck;
         int var7 = tocheck.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            String actual = var6[var8];
            if (invalids.contains(actual + ".x")) {
               return false;
            }
         }

         return true;
      }
   }

   private void loadResinRunProp(String prop, JavaParameters parameters, Object... substitution) throws ExecutionException {
      String[] values = this.getResinRunProperty(prop, substitution);
      String[] var5 = values;
      int var6 = values.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         String value = var5[var7];
         parameters.getVMParametersList().add(value);
      }

   }

   private String[] getResinRunProperty(String prop, Object... substitution) throws ExecutionException {
      this.loadResinRunProperties();
      String value = this.resinRunProps.getProperty(prop);
      if (value == null) {
         throw new ExecutionException(ResinBundle.message("resin.run.property.missing", prop));
      } else {
         String[] res = value.split(" ");
         if (substitution != null) {
            for(int i = 0; i < res.length; ++i) {
               res[i] = MessageFormat.format(res[i], substitution);
            }
         }

         return res;
      }
   }

   private void loadResinRunProperties() throws ExecutionException {
      if (this.resinRunProps == null) {
         this.resinRunProps = new Properties();

         try {
            this.resinRunProps.load(this.getClass().getResourceAsStream("ResinRun.properties"));
         } catch (IOException var2) {
            throw new ExecutionException(ResinBundle.message("resin.run.startup.no.prop"));
         }
      }
   }

   private static void loadResinConfProperties(String homePath, JavaParameters parameters) {
      File propertiesFile = new File(FileUtil.toSystemDependentName(homePath + "/conf/resin.properties"));
      if (propertiesFile.exists()) {
         Properties props = new Properties();

         try {
            props.load(new FileReader(propertiesFile));
         } catch (IOException var5) {
            LOG.info(var5);
         }

         parameters.getVMParametersList().addParametersString(props.getProperty("jvm_args"));
      }
   }
}
