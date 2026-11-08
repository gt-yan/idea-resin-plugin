package org.intellij.j2ee.web.resin.resin.configuration;

import com.intellij.execution.ExecutionException;
import com.intellij.javaee.appServers.deployment.DeploymentStatus;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.openapi.util.NullableLazyValue;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.util.text.StringUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import javax.management.InstanceNotFoundException;
import javax.management.JMException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import org.intellij.j2ee.web.resin.ResinBundle;
import org.intellij.j2ee.web.resin.ResinModelBase;
import org.intellij.j2ee.web.resin.resin.ResinInstallation;
import org.intellij.j2ee.web.resin.resin.WebApp;
import org.intellij.j2ee.web.resin.resin.common.MBeanUtil;
import org.intellij.j2ee.web.resin.resin.jmx.ConnectorCommandBase;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Resin3XConfigurationStrategy extends ResinConfigurationStrategy implements JmxConfigurationStrategy {
   private static final Logger LOG = Logger.getInstance(Resin3XConfigurationStrategy.class);
   protected static final @NonNls String SERVER_ELEMENT = "server";
   protected static final @NonNls String SERVER_DEFAULT_ELEMENT = "server-default";
   protected static final @NonNls String HTTP = "http";
   protected static final @NonNls String PORT = "port";
   protected static final @NonNls String DIRTY_ATTR = "dirty";
   protected static final @NonNls String WEB_APP_ELEMENT = "web-app";
   protected static final @NonNls String ID = "id";
   private static final @NonNls String DOCUMENT_DIRECTORY_ATTR = "document-directory";
   private static final @NonNls String ARCHIVE_PATH_ATTR = "archive-path";
   protected static final @NonNls String HOST = "host";
   protected static final @NonNls String CHARSET = "character-encoding";
   private static final @NonNls String HOST_DEFAULT_ELEMENT = "host-default";
   private static final @NonNls String WEB_APP_DEPLOY_ELEMENT = "web-app-deploy";
   private static final @NonNls String ROOT_CONTEXT_PATH = "/";
   private static final @NonNls String ROOT_EXPAND_DIR = "ROOT";
   private static final @NonNls String DEFAULT_EXPAND_DIR = "webapps";
   private static final @NonNls String PATH_ATTR = "path";
   private static final @NonNls String REGEXP_ATTR = "regexp";
   protected static final @NonNls String RESIN_CONF = "resin3.conf";
   public static final ObjectName MBEAN_WEB_APP_DEPLOY = MBeanUtil.newObjectName("resin:type=WebAppDeploy,Host=default,name=webapps");
   private static final @NonNls String MBEAN_WEB_APP_PREFIX = "resin:type=WebApp,Host=default,name=/";
   public static final @NonNls String STATE_JMX_ATTRIBUTE = "State";
   public static final @NonNls String STATE_JMX_ATTRIBUTE_ACTIVE = "active";
   public static final @NonNls String STATE_JMX_ATTRIBUTE_ERROR = "error";
   public static final @NonNls String STATE_JMX_ATTRIBUTE_FAILED = "failed";
   private final ResinInstallation myResinInstallation;
   private final NotNullLazyValue<ElementsProvider> myElementsProvider = NotNullLazyValue.lazy(() -> {
      return this.createElementsProvider();
   });

   public Resin3XConfigurationStrategy(ResinInstallation resinInstallation) {
      this.myResinInstallation = resinInstallation;
   }

   protected final ResinInstallation getInstallation() {
      return this.myResinInstallation;
   }

   public void setPort(int port) {
      ElementsProvider elementsProvider = this.getElementsProvider();
      Element httpElement = elementsProvider.getOrCreateChildElement(elementsProvider.getParamParentElement(), "http");
      httpElement.setAttribute("port", Integer.toString(port));
   }

   public boolean deploy(WebApp webApp) throws ExecutionException {
      Ref<Boolean> dirty = new Ref(false);
      ElementsProvider elementsProvider = this.getElementsProvider();
      Namespace ns = elementsProvider.getNS();
      Element host = this.getHost(webApp);
      removeAttribute(host, "dirty", dirty);
      String location = webApp.getLocation();
      boolean isExploded = (new File(location)).isDirectory();
      String contextPath = webApp.getContextPath();
      Element webAppEl = findWebAppElement(host, contextPath);
      if (webAppEl == null) {
         webAppEl = new Element("web-app", ns);
         webAppEl.setAttribute("id", contextPath);
         host.addContent(webAppEl);
         dirty.set(true);
      }

      String expandDirAttr = this.getExpandDirAttr();
      setAttribute(webAppEl, isExploded ? expandDirAttr : "archive-path", location, dirty);
      if (!isExploded) {
         StringBuilder expandDir = null;
         Element hostParent = elementsProvider.getHostParent();
         Element hostDefaultEl = hostParent.getChild("host-default", ns);
         if (hostDefaultEl != null) {
            Element webAppDeployEl = hostDefaultEl.getChild("web-app-deploy", ns);
            if (webAppDeployEl != null) {
               String webAppDeployPath = webAppDeployEl.getAttributeValue("path");
               if (StringUtil.isNotEmpty(webAppDeployPath)) {
                  expandDir = new StringBuilder(webAppDeployPath);
               }
            }
         }

         if (expandDir == null) {
            expandDir = new StringBuilder("webapps");
         }

         expandDir.append("/");
         expandDir.append("/".equals(contextPath) ? "ROOT" : FileUtil.sanitizeFileName(StringUtil.trimStart(contextPath, "/")));
         setAttribute(webAppEl, expandDirAttr, expandDir.toString(), dirty, true);
      }

      String charset = webApp.getCharSet();
      if (charset != null && !StringUtil.isEmptyOrSpaces(charset)) {
         setAttribute(webAppEl, "character-encoding", charset.trim(), dirty);
      } else {
         removeAttribute(webAppEl, "character-encoding", dirty);
      }

      return (Boolean)dirty.get();
   }

   protected String getExpandDirAttr() {
      return "document-directory";
   }

   private static Element findWebAppElement(Element host, String contextPath) {
      List webApps = host.getChildren("web-app", host.getNamespace());
      if (webApps != null) {
         Iterator var3 = webApps.iterator();

         while(var3.hasNext()) {
            Object webApp = var3.next();
            Element webAppEl = (Element)webApp;
            if (webAppEl.getAttribute("id").getValue().equals(contextPath)) {
               return webAppEl;
            }
         }
      }

      return null;
   }

   private static void setAttribute(Element element, String name, String value, Ref<Boolean> dirty) {
      setAttribute(element, name, value, dirty, false);
   }

   private static void setAttribute(Element element, String name, String value, Ref<Boolean> dirty, boolean keepExisting) {
      Attribute existingAttribute = element.getAttribute(name);
      if (existingAttribute == null || !keepExisting && !existingAttribute.getValue().equals(value)) {
         element.setAttribute(name, value);
         dirty.set(true);
      }

   }

   private static void removeAttribute(Element element, String name, Ref<Boolean> dirty) {
      if (element.getAttribute(name) != null) {
         element.removeAttribute(name);
         dirty.set(true);
      }

   }

   private Element getHost(WebApp webApp) throws ExecutionException {
      try {
         ElementsProvider elementsProvider = this.getElementsProvider();
         Namespace ns = elementsProvider.getNS();
         Element hostParent = elementsProvider.getHostParent();
         String webAppHost = webApp.getHost();
         List hosts = hostParent.getChildren("host", ns);
         if (hosts != null) {
            Iterator var7 = hosts.iterator();

            while(var7.hasNext()) {
               Object host1 = var7.next();
               Element host = (Element)host1;
               Attribute idAttribute = host.getAttribute("id");
               if (idAttribute != null && StringUtil.equals(idAttribute.getValue(), webAppHost)) {
                  return host;
               }

               Attribute regexpAttribute = host.getAttribute("regexp");
               if (regexpAttribute != null && webAppHost.matches(regexpAttribute.getValue())) {
                  return host;
               }
            }
         }

         Element host = new Element("host", ns);
         host.setAttribute("id", webApp.getHost());
         host.setAttribute("dirty", "true");
         hostParent.addContent(host);
         return host;
      } catch (Exception var12) {
         throw new ExecutionException(ResinBundle.message("resin.conf.parse.error"), var12);
      }
   }

   public boolean undeploy(WebApp webApp) {
      boolean dirty = false;
      ElementsProvider elementsProvider = this.getElementsProvider();
      Namespace ns = elementsProvider.getNS();
      List hosts = elementsProvider.getHostParent().getChildren("host", ns);
      if (hosts != null) {
         Iterator var6 = hosts.iterator();

         while(var6.hasNext()) {
            Object host = var6.next();
            Element hostEl = (Element)host;
            Element webAppEl = findWebAppElement(hostEl, webApp.getContextPath());
            if (webAppEl != null) {
               hostEl.removeContent(webAppEl);
               dirty = true;
            }
         }
      }

      return dirty;
   }

   public InputStream getDefaultResinConfContent() {
      return this.getClass().getResourceAsStream("resin3.conf");
   }

   public boolean deployWithJmx(ResinModelBase resinModel, WebApp webApp) {
      File webAppFile = new File(FileUtil.toSystemDependentName(webApp.getLocation()));
      if (!webAppFile.exists()) {
         LOG.error("Can't find web app");
         return false;
      } else if (this.getDeployStateWithJmx(resinModel, webApp, new Ref()) != DeploymentStatus.UNKNOWN && !(new UndeployCommand(resinModel, webAppFile)).safeExecute()) {
         return false;
      } else if (!cleanUpWebApp(resinModel, webAppFile)) {
         return true;
      } else if (!resinModel.transferFile(webAppFile)) {
         return false;
      } else {
         return (new DeployCommand(resinModel, "start", webAppFile.getName())).safeExecute();
      }
   }

   public @NotNull DeploymentStatus getDeployStateWithJmx(ResinModelBase resinModel, WebApp webApp, Ref<Boolean> isFinal) {
      File webAppFile = new File(FileUtil.toSystemDependentName(webApp.getLocation()));
      DeploymentStatus var10000;
      if (!webAppFile.exists()) {
         isFinal.set(true);
         var10000 = DeploymentStatus.FAILED;
         if (var10000 == null) {
            $$$reportNull$$$0(0);
         }

         return var10000;
      } else {
         GetStateCommand getStateCommand = new GetStateCommand(resinModel, webAppFile);
         if (!getStateCommand.safeExecute()) {
            isFinal.set(true);
            var10000 = DeploymentStatus.FAILED;
            if (var10000 == null) {
               $$$reportNull$$$0(1);
            }

            return var10000;
         } else {
            String state = (String)getStateCommand.getResult();
            if ("active".equalsIgnoreCase(state)) {
               isFinal.set(true);
               var10000 = DeploymentStatus.DEPLOYED;
               if (var10000 == null) {
                  $$$reportNull$$$0(2);
               }

               return var10000;
            } else if (!"error".equalsIgnoreCase(state) && !"failed".equalsIgnoreCase(state)) {
               var10000 = DeploymentStatus.UNKNOWN;
               if (var10000 == null) {
                  $$$reportNull$$$0(4);
               }

               return var10000;
            } else {
               isFinal.set(true);
               var10000 = DeploymentStatus.FAILED;
               if (var10000 == null) {
                  $$$reportNull$$$0(3);
               }

               return var10000;
            }
         }
      }
   }

   private static boolean cleanUpWebApp(ResinModelBase resinModel, File webAppFile) {
      if (!webAppFile.isDirectory() && !resinModel.deleteFile(new File(webAppFile.getParent(), FileUtilRt.getNameWithoutExtension(webAppFile.getName())))) {
         return false;
      } else {
         return resinModel.deleteFile(webAppFile);
      }
   }

   public boolean undeployWithJmx(ResinModelBase resinModel, WebApp webApp) {
      File webAppFile = new File(FileUtil.toSystemDependentName(webApp.getLocation()));
      if (!webAppFile.exists()) {
         LOG.error("Can't find web app");
         return false;
      } else if (!(new UndeployCommand(resinModel, webAppFile)).safeExecute()) {
         return false;
      } else if (!cleanUpWebApp(resinModel, webAppFile)) {
         return true;
      } else {
         GetStateCommand getStateCommand = new GetStateCommand(resinModel, webAppFile);
         if (!getStateCommand.safeExecute()) {
            return false;
         } else {
            return getStateCommand.getResult() == null;
         }
      }
   }

   private String getWebAppName(File webAppFile) {
      String fileName = webAppFile.getName();
      boolean trimExtension = !webAppFile.isDirectory() || this.myResinInstallation.getVersion().getParsed().compare(4, 0, 10) <= 0;
      return trimExtension ? FileUtilRt.getNameWithoutExtension(fileName) : fileName;
   }

   protected ElementsProvider createElementsProvider() {
      return new ElementsProvider(this.getElement());
   }

   protected final ElementsProvider getElementsProvider() {
      return (ElementsProvider)this.myElementsProvider.getValue();
   }

   // $FF: synthetic method
   private static void $$$reportNull$$$0(int var0) {
      throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "org/intellij/j2ee/web/resin/resin/configuration/Resin3XConfigurationStrategy", "getDeployStateWithJmx"));
   }

   protected static class ElementsProvider {
      private final Element myRootElement;
      private final Namespace myNS;
      private final NullableLazyValue<Element> myServer = new NullableLazyValue<Element>() {
         protected Element compute() {
            return ElementsProvider.this.getRootElement().getChild("server", ElementsProvider.this.getNS());
         }
      };
      private final NotNullLazyValue<Element> myParamParent = NotNullLazyValue.lazy(() -> {
         return this.doGetParamParent();
      });

      public ElementsProvider(Element element) {
         this.myRootElement = element;
         this.myNS = this.myRootElement.getNamespace();
      }

      protected final Element getRootElement() {
         return this.myRootElement;
      }

      public Namespace getNS() {
         return this.myNS;
      }

      private Element getOrCreateServerElement() {
         return this.getOrCreateChildElement(this.getRootElement(), "server");
      }

      public Element getHostParent() {
         return this.getOrCreateServerElement();
      }

      protected Element doGetParamParent() {
         return this.getOrCreateServerElement();
      }

      public Element getOrCreateChildElement(Element parentElement, @NonNls String childName) {
         Element result = parentElement.getChild(childName, this.getNS());
         if (result == null) {
            result = new Element(childName, this.getNS());
            parentElement.addContent(result);
         }

         return result;
      }

      public @Nullable Element getServerElement() {
         return (Element)this.myServer.getValue();
      }

      public @NotNull Element getParamParentElement() {
         Element var10000 = (Element)this.myParamParent.getValue();
         if (var10000 == null) {
            $$$reportNull$$$0(0);
         }

         return var10000;
      }

      // $FF: synthetic method
      private static void $$$reportNull$$$0(int var0) {
         throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "org/intellij/j2ee/web/resin/resin/configuration/Resin3XConfigurationStrategy$ElementsProvider", "getParamParentElement"));
      }
   }

   private class UndeployCommand extends WebAppCommandBase<Boolean> {
      UndeployCommand(ResinModelBase resinModel, File webAppFile) {
         super(resinModel, webAppFile);
      }

      protected @Nullable Boolean doExecute(MBeanServerConnection connection, ObjectName objectName) throws JMException, IOException {
         return (Boolean)invokeOperation(connection, objectName, "destroy", new Object[0]);
      }
   }

   private static class DeployCommand extends ConnectorCommandBase<Object> {
      private final String myCommand;
      private final String myArg;

      DeployCommand(ResinModelBase resinModel, @NonNls String command, @NonNls String arg) {
         super(resinModel);
         this.myCommand = command;
         this.myArg = arg;
      }

      protected Object doExecute(MBeanServerConnection connection) throws JMException, IOException {
         return invokeOperation(connection, Resin3XConfigurationStrategy.MBEAN_WEB_APP_DEPLOY, this.myCommand, new Object[]{this.myArg});
      }
   }

   private class GetStateCommand extends WebAppCommandBase<String> {
      GetStateCommand(ResinModelBase resinModel, File webAppFile) {
         super(resinModel, webAppFile);
      }

      protected @Nullable String doExecute(MBeanServerConnection connection, ObjectName objectName) throws JMException, IOException {
         try {
            return (String)connection.getAttribute(objectName, "State");
         } catch (InstanceNotFoundException var4) {
            return null;
         }
      }
   }

   private abstract class WebAppCommandBase<T> extends ConnectorCommandBase<T> {
      private final ObjectName myObjectName;

      WebAppCommandBase(ResinModelBase resinModel, File webAppFile) {
         super(resinModel);
         String var10001 = Resin3XConfigurationStrategy.this.getWebAppName(webAppFile);
         this.myObjectName = MBeanUtil.newObjectName("resin:type=WebApp,Host=default,name=/" + var10001);
      }

      protected @Nullable T doExecute(MBeanServerConnection connection) throws JMException, IOException {
         return this.doExecute(connection, this.myObjectName);
      }

      protected abstract @Nullable T doExecute(MBeanServerConnection var1, ObjectName var2) throws JMException, IOException;
   }
}
