package org.intellij.j2ee.web.resin.ui;

import com.intellij.DynamicBundle;
import com.intellij.javaee.appServers.run.configuration.CommonModel;
import com.intellij.javaee.transport.TransportManager;
import com.intellij.javaee.transport.TransportManagerConfigurable;
import com.intellij.javaee.transport.TransportTarget;
import com.intellij.javaee.transport.TransportTargetConfigurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Ref;
import com.intellij.ui.IdeBorderFactory.PlainSmallWithIndent;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.intellij.j2ee.web.resin.ResinBundle;
import org.intellij.j2ee.web.resin.ResinRemoteModel;
import org.intellij.j2ee.web.resin.resin.jmx.ConnectorPingCommand;
import org.jetbrains.annotations.NotNull;

public class RemoteRunConfigurationEditor extends ResinRunConfigurationEditorBase {
   private JPanel myRootPanel;
   private JTextField myJmxPortField;
   private JButton myPingButton;
   private TransportManagerConfigurable myTransportManagerConfigurable;
   private TransportTargetConfigurable myDeploymentTransportTargetConfigurable;
   private JTextField myCharsetField;
   private JLabel myJmxPortLabel;
   private final Project myProject;
   private ResinRemoteModel myServerModel;
   private TransportTarget myDeploymentTransportTarget;

   public RemoteRunConfigurationEditor(@NotNull Project project) {
      if (project == null) {
         $$$reportNull$$$0(0);
      }

      this.myProject = project;
      this.$$$setupUI$$$();
      this.myPingButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            RemoteRunConfigurationEditor.this.onPingButton();
         }
      });
      this.myDeploymentTransportTargetConfigurable.setParentConfigurable(this.myTransportManagerConfigurable);
   }

   private Project getProject() {
      return this.myProject;
   }

   private void onPingButton() {
      int jmxPort;
      try {
         jmxPort = this.parseJmxPort();
      } catch (ConfigurationException var4) {
         return;
      }

      Ref<Boolean> success = new Ref((Object)null);
      if (ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
         try {
            Boolean pingResult = (Boolean)(new ConnectorPingCommand(this.myServerModel, jmxPort)).execute();
            success.set(pingResult != null && pingResult);
         } catch (Exception var4) {
            success.set(false);
         }

      }, ResinBundle.message("RemoteRunConfigurationEditor.message.ping.operation-name"), true, this.getProject())) {
         if (!success.isNull()) {
            String ping = ResinBundle.message("RemoteRunConfigurationEditor.message.ping");
            if ((Boolean)success.get()) {
               Messages.showInfoMessage(ResinBundle.message("RemoteRunConfigurationEditor.message.ping.ok"), ping);
            } else {
               Messages.showErrorDialog(ResinBundle.message("RemoteRunConfigurationEditor.message.ping.failed"), ping);
            }

         }
      }
   }

   private int parseJmxPort() throws ConfigurationException {
      return RunConfigurationEditor.parseInt(this.myJmxPortField, "run.config.dlg.jmx.port.error");
   }

   protected void resetEditorFrom(@NotNull CommonModel s) {
      if (s == null) {
         $$$reportNull$$$0(1);
      }

      this.myServerModel = (ResinRemoteModel)s.getServerModel();
      this.myCharsetField.setText(this.myServerModel.getCharset());
      this.myJmxPortField.setText(String.valueOf(this.myServerModel.getJmxPort()));
      this.myTransportManagerConfigurable.setHostId(this.myServerModel.getTransportHostId(), this.myServerModel.getProject());
      this.myDeploymentTransportTarget = getOrCreateTransportTarget(this.myServerModel.getTransportTargetWebApps());
      this.myDeploymentTransportTargetConfigurable.setTarget(this.myDeploymentTransportTarget);
      this.updateJmxPortVisible(this.myServerModel);
   }

   protected void applyEditorTo(@NotNull CommonModel s) throws ConfigurationException {
      if (s == null) {
         $$$reportNull$$$0(2);
      }

      ResinRemoteModel serverModel = (ResinRemoteModel)s.getServerModel();
      serverModel.setJmxPort(this.parseJmxPort());
      serverModel.setCharset(this.myCharsetField.getText());
      serverModel.setTransportHostId(this.myTransportManagerConfigurable.getHostId());
      serverModel.setTransportTargetWebApps(this.myDeploymentTransportTarget);
      this.myDeploymentTransportTargetConfigurable.saveState();
   }

   private static TransportTarget getOrCreateTransportTarget(TransportTarget target) {
      return target != null && target.getId() != null ? target : TransportManager.createTarget();
   }

   protected @NotNull JComponent createEditor() {
      JPanel var10000 = this.myRootPanel;
      if (var10000 == null) {
         $$$reportNull$$$0(3);
      }

      return var10000;
   }

   protected void setJmxPortVisible(boolean visible) {
      this.myJmxPortLabel.setVisible(visible);
      this.myJmxPortField.setVisible(visible);
      this.myPingButton.setVisible(visible);
   }

   // $FF: synthetic method
   private void $$$setupUI$$$() {
      JPanel var1 = new JPanel();
      this.myRootPanel = var1;
      var1.setLayout(new GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1, false, false));
      JLabel var2 = new JLabel();
      this.myJmxPortLabel = var2;
      this.$$$loadLabelText$$$(var2, DynamicBundle.getResourceBundle(RemoteRunConfigurationEditor.class.getClassLoader(), "messages/ResinBundle").getString("Form.RemoteRunConfigurationEditor.jmx.port"));
      var1.add(var2, new GridConstraints(1, 0, 1, 1, 8, 0, 0, 0, (Dimension)null, (Dimension)null, (Dimension)null));
      Spacer var3 = new Spacer();
      var1.add(var3, new GridConstraints(3, 1, 1, 1, 0, 2, 1, 6, (Dimension)null, (Dimension)null, (Dimension)null));
      JTextField var4 = new JTextField();
      this.myJmxPortField = var4;
      var1.add(var4, new GridConstraints(1, 1, 1, 1, 8, 1, 0, 0, (Dimension)null, new Dimension(80, -1), (Dimension)null));
      JPanel var5 = new JPanel();
      var5.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1, false, false));
      var5.putClientProperty("BorderFactoryClass", "com.intellij.ui.IdeBorderFactory$PlainSmallWithIndent");
      var1.add(var5, new GridConstraints(2, 0, 1, 3, 0, 3, 3, 3, (Dimension)null, (Dimension)null, (Dimension)null));
      var5.setBorder(PlainSmallWithIndent.createTitledBorder(BorderFactory.createEtchedBorder(), DynamicBundle.getResourceBundle(RemoteRunConfigurationEditor.class.getClassLoader(), "messages/ResinBundle").getString("Form.RemoteRunConfigurationEditor.remote.staging"), 0, 0, (Font)null, (Color)null));
      TransportManagerConfigurable var6 = new TransportManagerConfigurable();
      this.myTransportManagerConfigurable = var6;
      JComponent var7 = var6.getMainPanel();
      var5.add(var7, new GridConstraints(0, 0, 1, 1, 0, 1, 3, 3, (Dimension)null, (Dimension)null, (Dimension)null));
      JPanel var8 = new JPanel();
      var8.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1, false, false));
      var8.putClientProperty("BorderFactoryClass", "com.intellij.ui.IdeBorderFactory$PlainSmallWithIndent");
      var5.add(var8, new GridConstraints(1, 0, 1, 1, 0, 3, 3, 3, (Dimension)null, (Dimension)null, (Dimension)null));
      var8.setBorder(PlainSmallWithIndent.createTitledBorder(BorderFactory.createEtchedBorder(), DynamicBundle.getResourceBundle(RemoteRunConfigurationEditor.class.getClassLoader(), "messages/ResinBundle").getString("Form.RemoteRunConfigurationEditor.webapps.target"), 0, 0, (Font)null, (Color)null));
      TransportTargetConfigurable var9 = new TransportTargetConfigurable();
      this.myDeploymentTransportTargetConfigurable = var9;
      JComponent var10 = var9.getMainPanel();
      var8.add(var10, new GridConstraints(0, 0, 1, 1, 0, 1, 3, 3, (Dimension)null, (Dimension)null, (Dimension)null));
      JButton var11 = new JButton();
      this.myPingButton = var11;
      this.$$$loadButtonText$$$(var11, DynamicBundle.getResourceBundle(RemoteRunConfigurationEditor.class.getClassLoader(), "messages/ResinBundle").getString("Form.RemoteRunConfigurationEditor.ping"));
      var1.add(var11, new GridConstraints(1, 2, 1, 1, 8, 0, 3, 0, (Dimension)null, (Dimension)null, (Dimension)null));
      JTextField var12 = new JTextField();
      this.myCharsetField = var12;
      var1.add(var12, new GridConstraints(0, 1, 1, 1, 8, 1, 0, 0, (Dimension)null, new Dimension(80, -1), (Dimension)null));
      JLabel var13 = new JLabel();
      this.$$$loadLabelText$$$(var13, DynamicBundle.getResourceBundle(RemoteRunConfigurationEditor.class.getClassLoader(), "messages/ResinBundle").getString("run.config.dlg.charset"));
      var1.add(var13, new GridConstraints(0, 0, 1, 1, 8, 0, 0, 0, (Dimension)null, (Dimension)null, (Dimension)null));
      Spacer var14 = new Spacer();
      var1.add(var14, new GridConstraints(0, 2, 1, 1, 0, 1, 6, 1, (Dimension)null, (Dimension)null, (Dimension)null));
      var2.setLabelFor(var4);
   }

   // $FF: synthetic method
   public JComponent $$$getRootComponent$$$() {
      return this.myRootPanel;
   }

   // $FF: synthetic method
   private void $$$loadLabelText$$$(JLabel var1, String var2) {
      StringBuffer var3 = new StringBuffer();
      boolean var4 = false;
      char var5 = 0;
      int var6 = -1;

      for(int var7 = 0; var7 < var2.length(); ++var7) {
         if (var2.charAt(var7) == '&') {
            ++var7;
            if (var7 == var2.length()) {
               break;
            }

            if (!var4 && var2.charAt(var7) != '&') {
               var4 = true;
               var5 = var2.charAt(var7);
               var6 = var3.length();
            }
         }

         var3.append(var2.charAt(var7));
      }

      var1.setText(var3.toString());
      if (var4) {
         var1.setDisplayedMnemonic(var5);
         var1.setDisplayedMnemonicIndex(var6);
      }

   }

   // $FF: synthetic method
   private void $$$loadButtonText$$$(AbstractButton var1, String var2) {
      StringBuffer var3 = new StringBuffer();
      boolean var4 = false;
      char var5 = 0;
      int var6 = -1;

      for(int var7 = 0; var7 < var2.length(); ++var7) {
         if (var2.charAt(var7) == '&') {
            ++var7;
            if (var7 == var2.length()) {
               break;
            }

            if (!var4 && var2.charAt(var7) != '&') {
               var4 = true;
               var5 = var2.charAt(var7);
               var6 = var3.length();
            }
         }

         var3.append(var2.charAt(var7));
      }

      var1.setText(var3.toString());
      if (var4) {
         var1.setMnemonic(var5);
         var1.setDisplayedMnemonicIndex(var6);
      }

   }

   // $FF: synthetic method
   private static void $$$reportNull$$$0(int index) {
      if (index == 3) {
         throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "org/intellij/j2ee/web/resin/ui/RemoteRunConfigurationEditor", "createEditor"));
      } else {
         String paramName = index == 0 ? "project" : "s";
         String methodName = index == 0 ? "<init>" : (index == 1 ? "resetEditorFrom" : "applyEditorTo");
         throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", paramName, "org/intellij/j2ee/web/resin/ui/RemoteRunConfigurationEditor", methodName));
      }
   }
}
