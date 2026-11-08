package org.intellij.j2ee.web.resin.resin.configuration;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.util.Pair;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.intellij.j2ee.web.resin.ResinModel;
import org.intellij.j2ee.web.resin.resin.ResinInstallation;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jetbrains.annotations.NonNls;

public class Resin4XmlConfigurationStrategy extends ResinXmlConfigurationStrategy {
   protected static final String RESIN_CONF = "resin4.xml";
   private static final @NonNls String HOST_DEFAULT_ELEMENT = "host-default";
   private static final @NonNls String WEB_APP_DEPLOY_ELEMENT = "web-app-deploy";
   private static final @NonNls String SERVER_MULTI_ELEMENT = "server-multi";
   private static final @NonNls String ROOT_DIR_ATTRIBUTE = "root-directory";
   private static final @NonNls String STARTUP_MODE_ATTRIBUTE = "startup-mode";
   private static final @NonNls String ID_PREFIX_ATTRIBUTE = "id-prefix";
   private static final @NonNls String ID_ATTRIBUTE = "id";
   private static final @NonNls String FIRST_SERVER_ID_SUFFIX = "0";
   private static final @NonNls String JVM_ARG = "jvm-arg";
   private static final @NonNls String JVM_ARG_SEPARATOR = "=";
   private String myServerId;

   Resin4XmlConfigurationStrategy(ResinInstallation resinInstallation) {
      super(resinInstallation);
   }

   public void init(ResinModel serverModel, Element element) throws ExecutionException {
      super.init(serverModel, element);
      Resin4ElementsProvider elementsProvider = (Resin4ElementsProvider)this.getElementsProvider();
      Namespace ns = elementsProvider.getNS();
      Element serverElement = elementsProvider.getClusterElement().getChild("host-default", ns);
      if (serverElement == null) {
         serverElement = elementsProvider.getOrCreateChildElement(elementsProvider.getClusterDefaultElement(), "host-default");
      }

      Element serverMultiElement = elementsProvider.getOrCreateChildElement(serverElement, "web-app-deploy");
      serverMultiElement.setAttribute("startup-mode", serverModel.getDeployMode());
      serverElement = elementsProvider.getServerElement();
      if (serverElement != null) {
         this.myServerId = serverElement.getAttributeValue("id");
      } else if (elementsProvider.getServerDefaultElement() == null) {
         serverMultiElement = elementsProvider.getClusterElement().getChild("server-multi", ns);
         if (serverMultiElement == null) {
            throw new ExecutionException("Can't find neither 'server' nor 'server-default' nor 'server-multi' element");
         }

         this.myServerId = serverMultiElement.getAttributeValue("id-prefix") + "0";
      }

      Map<String, Element> jvmArgName2Element = new HashMap();
      List jvmArgElements = elementsProvider.getParamParentElement().getChildren("jvm-arg", ns);
      Iterator var8 = jvmArgElements.iterator();

      while(var8.hasNext()) {
         Object jvmArg = var8.next();
         if (jvmArg instanceof Element jvmArgElement) {
            String jvmArgText = jvmArgElement.getText();
            String[] jvmArgNameValue = jvmArgText.split("=", 2);
            jvmArgName2Element.put(jvmArgNameValue[0], jvmArgElement);
         }
      }

      List<ArgNameValue> jmxJvmArgs = new ArrayList();
      jmxJvmArgs.add(new ArgNameValue("-Dcom.sun.management.jmxremote.port", String.valueOf(serverModel.getJmxPort())));
      jmxJvmArgs.add(new ArgNameValue("-Dcom.sun.management.jmxremote.ssl", "false"));
      File accessFile = serverModel.getAccessFile();
      File passwordFile = serverModel.getPasswordFile();
      if (accessFile != null && passwordFile != null) {
         try {
            jmxJvmArgs.add(new ArgNameValue("-Dcom.sun.management.jmxremote.password.file", passwordFile.getCanonicalPath()));
            jmxJvmArgs.add(new ArgNameValue("-Dcom.sun.management.jmxremote.access.file", accessFile.getCanonicalPath()));
         } catch (IOException var14) {
            throw new ExecutionException(var14);
         }
      } else {
         jmxJvmArgs.add(new ArgNameValue("-Dcom.sun.management.jmxremote.authenticate", "false"));
      }

      Iterator var19 = jmxJvmArgs.iterator();

      while(var19.hasNext()) {
         ArgNameValue jmxJvmArg = (ArgNameValue)var19.next();
         Element jvmArgElement = (Element)jvmArgName2Element.get(jmxJvmArg.getFirst());
         if (jvmArgElement == null) {
            jvmArgElement = new Element("jvm-arg", ns);
            elementsProvider.getParamParentElement().addContent(jvmArgElement);
         }

         String var10001 = (String)jmxJvmArg.getFirst();
         jvmArgElement.setText(var10001 + "=" + (String)jmxJvmArg.getSecond());
      }

   }

   public String getServerId() {
      return this.myServerId;
   }

   protected String getExpandDirAttr() {
      return "root-directory";
   }

   public InputStream getDefaultResinConfContent() {
      return this.getClass().getResourceAsStream("resin4.xml");
   }

   protected Resin3XConfigurationStrategy.ElementsProvider createElementsProvider() {
      return new Resin4ElementsProvider(this.getElement());
   }

   private class Resin4ElementsProvider extends Resin31ConfigurationStrategy.Resin31ElementsProvider {
      Resin4ElementsProvider(Element element) {
         super(element);
      }

      public Element getDirectClusterDefaultElement() {
         return this.getRootElement().getChild("cluster-default", this.getNS());
      }

      protected Element doGetClusterDefaultElement() {
         Element result = this.getDirectClusterDefaultElement();
         if (result != null) {
            return result;
         } else {
            Iterator var2 = Resin4XmlConfigurationStrategy.this.getImports().iterator();

            while(var2.hasNext()) {
               ResinConfigImport configImport = (ResinConfigImport)var2.next();
               Element element = configImport.getImportDoc();
               if (element != null) {
                  Resin4ElementsProvider importElementsProvider = Resin4XmlConfigurationStrategy.this.new Resin4ElementsProvider(element);
                  result = importElementsProvider.getDirectClusterDefaultElement();
                  if (result != null) {
                     if (Resin4XmlConfigurationStrategy.this.getInstallation().getVersion().getParsed().compare(4, 0, 41) >= 0) {
                        Resin4XmlConfigurationStrategy.this.resolveImports(element, (List)null);
                     }

                     configImport.copy();
                     return result;
                  }
               }
            }

            return super.doGetClusterDefaultElement();
         }
      }
   }

   private static class ArgNameValue extends Pair<String, String> {
      ArgNameValue(@NonNls String first, @NonNls String second) {
         super(first, second);
      }
   }
}
