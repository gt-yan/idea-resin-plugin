package org.intellij.j2ee.web.resin.ui;

import com.intellij.DynamicBundle;
import com.intellij.javaee.appServers.run.configuration.CommonModel;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.PanelWithAnchor;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.ui.components.JBLabel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.intellij.j2ee.web.resin.ResinBundle;
import org.intellij.j2ee.web.resin.ResinModel;
import org.intellij.j2ee.web.resin.resin.common.ParseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class RunConfigurationEditor extends ResinRunConfigurationEditorBase implements PanelWithAnchor {
   private JTextField myHttpPortTextField;
   private JPanel mainPanel;
   private JCheckBox debugConfiguration;
   private TextFieldWithBrowseButton resinConfSelector;
   private JTextField charset;
   private JCheckBox readOnlyConfiguration;
   private RawCommandLineEditor additionalParameters;
   private JCheckBox autoBuildClasspath;
   private JComboBox myDeployModeComboBox;
   private JTextField myJmxPortTextField;
   private JBLabel myAdditionalResinCommandLineLabel;
   private JLabel myJmxPortLabel;
   private JComponent anchor;

   public RunConfigurationEditor() {
      this.$$$setupUI$$$();
      initChooser(this.resinConfSelector, ResinBundle.message("message.text.settings.resin.conf.file.title"), ResinBundle.message("message.text.settings.resin.conf.file.select"));
      this.myDeployModeComboBox.addItem("automatic");
      this.myDeployModeComboBox.addItem("lazy");
      this.myDeployModeComboBox.addItem("manual");
      this.setAnchor(this.myAdditionalResinCommandLineLabel);
   }

   protected void resetEditorFrom(@NotNull CommonModel commonModel) {
      if (commonModel == null) {
         $$$reportNull$$$0(0);
      }

      ResinModel resinModel = (ResinModel)commonModel.getServerModel();
      this.myHttpPortTextField.setText(String.valueOf(resinModel.getLocalPort()));
      this.resinConfSelector.setText(resinModel.getResinConf());
      this.debugConfiguration.setSelected(resinModel.isDebugConfiguration());
      this.readOnlyConfiguration.setSelected(resinModel.isReadOnlyConfiguration());
      this.autoBuildClasspath.setSelected(resinModel.isAutoBuildClassPath());
      this.charset.setText(resinModel.getCharset());
      this.additionalParameters.setText(resinModel.getAdditionalParameters());
      this.myJmxPortTextField.setText(String.valueOf(resinModel.getJmxPort()));
      this.myDeployModeComboBox.setSelectedItem(resinModel.getDeployMode());
      this.updateJmxPortVisible(resinModel);
   }

   protected void applyEditorTo(@NotNull CommonModel commonModel) throws ConfigurationException {
      if (commonModel == null) {
         $$$reportNull$$$0(1);
      }

      ResinModel resinModel = (ResinModel)commonModel.getServerModel();
      resinModel.setPort(parseInt(this.myHttpPortTextField, "run.config.dlg.http.port.error"));
      resinModel.setResinConf(this.resinConfSelector.getText());
      resinModel.setDebugConfiguration(this.debugConfiguration.isSelected());
      resinModel.setReadOnlyConfiguration(this.readOnlyConfiguration.isSelected());
      resinModel.setAutoBuildClassPath(this.autoBuildClasspath.isSelected());
      resinModel.setCharset(this.charset.getText());
      resinModel.setAdditionalParameters(this.additionalParameters.getText());
      resinModel.setJmxPort(parseInt(this.myJmxPortTextField, "run.config.dlg.jmx.port.error"));
      resinModel.setDeployMode((String)this.myDeployModeComboBox.getSelectedItem());
   }

   protected void setJmxPortVisible(boolean visible) {
      this.myJmxPortLabel.setVisible(visible);
      this.myJmxPortTextField.setVisible(visible);
   }

   protected @NotNull JComponent createEditor() {
      JPanel var10000 = this.mainPanel;
      if (var10000 == null) {
         $$$reportNull$$$0(2);
      }

      return var10000;
   }

   public JComponent getAnchor() {
      return this.anchor;
   }

   public void setAnchor(JComponent anchor) {
      this.anchor = anchor;
      this.myAdditionalResinCommandLineLabel.setAnchor(anchor);
   }

   private static void initChooser(TextFieldWithBrowseButton field, String title, String description) {
      field.setText("");
      field.getTextField().setEditable(true);
      field.addBrowseFolderListener(title, description, (Project)null, FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());
   }

   public static int parseInt(JTextField text, final @PropertyKey(
   resourceBundle = "messages.ResinBundle"
) String errorKey) throws ConfigurationException {
      return (new ParseUtil() {
         protected String getErrorMessage(String unparsableValue) {
            return ResinBundle.message(errorKey, unparsableValue);
         }
      }).parseInt(text);
   }

   // $FF: synthetic method
   private void $$$setupUI$$$() {
      JPanel var1 = new JPanel();
      this.mainPanel = var1;
      var1.setLayout(new GridLayoutManager(6, 1, new Insets(0, 0, 0, 0), -1, -1, false, false));
      JCheckBox var2 = new JCheckBox();
      this.readOnlyConfiguration = var2;
      this.$$$loadButtonText$$$(var2, DynamicBundle.getBundle("messages/ResinBundle", RunConfigurationEditor.class).getString("run.config.dlg.notalter.resin.conf"));
      var1.add(var2, new GridConstraints(1, 0, 1, 1, 8, 0, 3, 0, (Dimension)null, (Dimension)null, (Dimension)null));
      JCheckBox var3 = new JCheckBox();
      this.autoBuildClasspath = var3;
      var3.setText("Include compiler output and libraries on classpath");
      var1.add(var3, new GridConstraints(2, 0, 1, 1, 8, 0, 3, 0, (Dimension)null, (Dimension)null, (Dimension)null));
      JCheckBox var4 = new JCheckBox();
      this.debugConfiguration = var4;
      this.$$$loadButtonText$$$(var4, DynamicBundle.getBundle("messages/ResinBundle", RunConfigurationEditor.class).getString("run.config.dlg.debug.resin.conf"));
      var1.add(var4, new GridConstraints(3, 0, 1, 1, 8, 0, 3, 0, (Dimension)null, (Dimension)null, (Dimension)null));
      JPanel var5 = new JPanel();
      var5.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1, false, false));
      var1.add(var5, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 3, (Dimension)null, (Dimension)null, (Dimension)null));
      JLabel var6 = new JLabel();
      this.$$$loadLabelText$$$(var6, DynamicBundle.getBundle("messages/ResinBundle", RunConfigurationEditor.class).getString("run.config.dlg.resin.conf"));
      var5.add(var6, new GridConstraints(0, 0, 1, 1, 8, 0, 0, 0, (Dimension)null, (Dimension)null, (Dimension)null));
      TextFieldWithBrowseButton var7 = new TextFieldWithBrowseButton();
      this.resinConfSelector = var7;
      var7.setEditable(true);
      var7.setEnabled(true);
      var5.add(var7, new GridConstraints(0, 1, 1, 1, 1, 1, 3, 0, (Dimension)null, new Dimension(200, -1), (Dimension)null));
      JBLabel var8 = new JBLabel();
      this.myAdditionalResinCommandLineLabel = var8;
      this.$$$loadLabelText$$$(var8, DynamicBundle.getBundle("messages/ResinBundle", RunConfigurationEditor.class).getString("run.config.dlg.additional.params"));
      var5.add(var8, new GridConstraints(1, 0, 1, 1, 8, 0, 0, 0, (Dimension)null, (Dimension)null, (Dimension)null));
      RawCommandLineEditor var9 = new RawCommandLineEditor();
      this.additionalParameters = var9;
      var9.setDialogCaption(DynamicBundle.getBundle("messages/ResinBundle", RunConfigurationEditor.class).getString("run.config.dlg.additional.params"));
      var5.add(var9, new GridConstraints(1, 1, 1, 1, 1, 1, 3, 0, (Dimension)null, (Dimension)null, (Dimension)null));
      JPanel var10 = new JPanel();
      var10.setLayout(new GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1, false, false));
      var1.add(var10, new GridConstraints(4, 0, 1, 1, 0, 3, 3, 3, (Dimension)null, (Dimension)null, (Dimension)null));
      JLabel var11 = new JLabel();
      this.$$$loadLabelText$$$(var11, DynamicBundle.getBundle("messages/ResinBundle", RunConfigurationEditor.class).getString("run.config.dlg.deploy.mode"));
      var10.add(var11, new GridConstraints(1, 0, 1, 1, 8, 0, 0, 0, (Dimension)null, (Dimension)null, (Dimension)null));
      JComboBox var12 = new JComboBox();
      this.myDeployModeComboBox = var12;
      var10.add(var12, new GridConstraints(1, 1, 1, 1, 8, 1, 6, 0, (Dimension)null, (Dimension)null, (Dimension)null));
      JLabel var13 = new JLabel();
      this.myJmxPortLabel = var13;
      this.$$$loadLabelText$$$(var13, DynamicBundle.getBundle("messages/ResinBundle", RunConfigurationEditor.class).getString("run.config.dlg.jmx.port"));
      var10.add(var13, new GridConstraints(0, 2, 1, 1, 8, 0, 0, 0, (Dimension)null, (Dimension)null, (Dimension)null));
      JTextField var14 = new JTextField();
      this.myJmxPortTextField = var14;
      var10.add(var14, new GridConstraints(0, 3, 1, 1, 8, 1, 6, 0, (Dimension)null, new Dimension(150, -1), (Dimension)null));
      JLabel var15 = new JLabel();
      this.$$$loadLabelText$$$(var15, DynamicBundle.getBundle("messages/ResinBundle", RunConfigurationEditor.class).getString("run.config.dlg.charset"));
      var10.add(var15, new GridConstraints(0, 0, 1, 1, 8, 0, 0, 0, (Dimension)null, (Dimension)null, (Dimension)null));
      JTextField var16 = new JTextField();
      this.charset = var16;
      var10.add(var16, new GridConstraints(0, 1, 1, 1, 8, 1, 6, 0, (Dimension)null, new Dimension(150, -1), (Dimension)null));
      JTextField var17 = new JTextField();
      this.myHttpPortTextField = var17;
      var10.add(var17, new GridConstraints(1, 3, 1, 1, 8, 1, 6, 0, (Dimension)null, new Dimension(150, -1), (Dimension)null));
      JLabel var18 = new JLabel();
      this.$$$loadLabelText$$$(var18, DynamicBundle.getBundle("messages/ResinBundle", RunConfigurationEditor.class).getString("run.config.dlg.http.port"));
      var10.add(var18, new GridConstraints(1, 2, 1, 1, 8, 0, 0, 0, (Dimension)null, (Dimension)null, (Dimension)null));
      Spacer var19 = new Spacer();
      var1.add(var19, new GridConstraints(5, 0, 1, 1, 0, 2, 1, 6, (Dimension)null, new Dimension(95, 14), (Dimension)null));
   }

   // $FF: synthetic method
   public JComponent $$$getRootComponent$$$() {
      return this.mainPanel;
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
         throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "org/intellij/j2ee/web/resin/ui/RunConfigurationEditor", "createEditor"));
      } else {
         String methodName = index == 1 ? "applyEditorTo" : "resetEditorFrom";
         throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "commonModel", "org/intellij/j2ee/web/resin/ui/RunConfigurationEditor", methodName));
      }
   }
}
