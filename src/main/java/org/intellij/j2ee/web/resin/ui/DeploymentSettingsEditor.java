package org.intellij.j2ee.web.resin.ui;

import com.intellij.DynamicBundle;
import com.intellij.javaee.appServers.deployment.DeploymentModel;
import com.intellij.javaee.appServers.deployment.DeploymentSource;
import com.intellij.javaee.appServers.run.configuration.CommonModel;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.intellij.j2ee.web.resin.ResinModuleDeploymentModel;
import org.jetbrains.annotations.NotNull;

public class DeploymentSettingsEditor extends SettingsEditor<DeploymentModel> {
   private JPanel myMainPanel;
   private JTextField myHostField;
   private JTextField myApplicationContextField;
   private JCheckBox myDefaultContextCheckBox;

   public DeploymentSettingsEditor(CommonModel commonModel, DeploymentSource deploymentSource) {
      super(() -> {
         return new ResinModuleDeploymentModel(commonModel, deploymentSource);
      });
      this.$$$setupUI$$$();
      this.myDefaultContextCheckBox.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            DeploymentSettingsEditor.this.updateContextEnabled();
         }
      });
      this.myDefaultContextCheckBox.setSelected(true);
      this.updateContextEnabled();
   }

   private void updateContextEnabled() {
      this.myApplicationContextField.setEnabled(!this.myDefaultContextCheckBox.isSelected());
   }

   public void resetEditorFrom(@NotNull DeploymentModel settings) {
      if (settings == null) {
         $$$reportNull$$$0(0);
      }

      ResinModuleDeploymentModel resinDeploymentModel = (ResinModuleDeploymentModel)settings;
      this.myDefaultContextCheckBox.setSelected(resinDeploymentModel.isDefaultContextPath());
      this.updateContextEnabled();
      this.myApplicationContextField.setText(resinDeploymentModel.getContextPath());
      this.myHostField.setText(resinDeploymentModel.getHost());
   }

   public void applyEditorTo(@NotNull DeploymentModel settings) throws ConfigurationException {
      if (settings == null) {
         $$$reportNull$$$0(1);
      }

      ResinModuleDeploymentModel resinDeploymentModel = (ResinModuleDeploymentModel)settings;
      resinDeploymentModel.setDefaultContextPath(this.myDefaultContextCheckBox.isSelected());
      resinDeploymentModel.setContextPath(this.myApplicationContextField.getText());
      resinDeploymentModel.setHost(this.myHostField.getText());
   }

   protected @NotNull JComponent createEditor() {
      JPanel var10000 = this.myMainPanel;
      if (var10000 == null) {
         $$$reportNull$$$0(2);
      }

      return var10000;
   }

   // $FF: synthetic method
   private void $$$setupUI$$$() {
      JPanel var1 = new JPanel();
      this.myMainPanel = var1;
      var1.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1, false, false));
      JLabel var2 = new JLabel();
      this.$$$loadLabelText$$$(var2, DynamicBundle.getBundle("messages/ResinBundle", DeploymentSettingsEditor.class).getString("deployment.dlg.app.context"));
      var1.add(var2, new GridConstraints(2, 0, 1, 1, 8, 0, 0, 0, (Dimension)null, (Dimension)null, (Dimension)null));
      Spacer var3 = new Spacer();
      var1.add(var3, new GridConstraints(3, 1, 1, 1, 0, 2, 1, 6, (Dimension)null, (Dimension)null, (Dimension)null));
      JTextField var4 = new JTextField();
      this.myApplicationContextField = var4;
      var1.add(var4, new GridConstraints(2, 1, 1, 1, 8, 1, 6, 0, (Dimension)null, new Dimension(150, -1), (Dimension)null));
      JLabel var5 = new JLabel();
      this.$$$loadLabelText$$$(var5, DynamicBundle.getBundle("messages/ResinBundle", DeploymentSettingsEditor.class).getString("deployment.dlg.host.name.text"));
      var1.add(var5, new GridConstraints(0, 0, 1, 1, 8, 0, 0, 0, (Dimension)null, (Dimension)null, (Dimension)null));
      JTextField var6 = new JTextField();
      this.myHostField = var6;
      var1.add(var6, new GridConstraints(0, 1, 1, 1, 8, 1, 6, 0, (Dimension)null, new Dimension(150, -1), (Dimension)null));
      JCheckBox var7 = new JCheckBox();
      this.myDefaultContextCheckBox = var7;
      this.$$$loadButtonText$$$(var7, DynamicBundle.getBundle("messages/ResinBundle", DeploymentSettingsEditor.class).getString("deployment.dlg.default.context.checkbox"));
      var1.add(var7, new GridConstraints(1, 0, 1, 2, 8, 0, 3, 0, (Dimension)null, (Dimension)null, (Dimension)null));
   }

   // $FF: synthetic method
   public JComponent $$$getRootComponent$$$() {
      return this.myMainPanel;
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
      if (index == 2) {
         throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "org/intellij/j2ee/web/resin/ui/DeploymentSettingsEditor", "createEditor"));
      } else {
         String methodName = index == 1 ? "applyEditorTo" : "resetEditorFrom";
         throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "settings", "org/intellij/j2ee/web/resin/ui/DeploymentSettingsEditor", methodName));
      }
   }
}
