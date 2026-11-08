package org.intellij.j2ee.web.resin.ui;

import com.intellij.DynamicBundle;
import com.intellij.execution.ExecutionException;
import com.intellij.icons.AllIcons.General;
import com.intellij.javaee.appServers.appServerIntegrations.ApplicationServerPersistentDataEditor;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DocumentAdapter;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.File;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import org.intellij.j2ee.web.resin.ResinBundle;
import org.intellij.j2ee.web.resin.ResinPersistentData;
import org.intellij.j2ee.web.resin.resin.ResinInstallation;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class SelectResinLocationEditor extends ApplicationServerPersistentDataEditor<ResinPersistentData> {
   private static final @NonNls String RESIN_CONF_FILE = "conf/resin.xml";
   private static final @NonNls String OLD_RESIN_CONF_FILE = "conf/resin.conf";
   private JPanel mainPanel;
   private TextFieldWithBrowseButton resinHomeSelector;
   private JLabel resinVersionLabel;
   private JCheckBox includeAllResinjarsCheckbox;
   private TextFieldWithBrowseButton defaultResinConf;
   private JLabel myErrorLabel;
   private boolean suggestConfPath = false;
   private boolean myHasHomeError;

   public SelectResinLocationEditor() {
      this.$$$setupUI$$$();
      initChooser(this.resinHomeSelector, ResinBundle.message("message.text.locator.resin.home.title"), ResinBundle.message("message.text.locator.resin.home.summary"), false, true);
      initChooser(this.defaultResinConf, ResinBundle.message("message.text.locator.resin.conf.title"), ResinBundle.message("message.text.locator.resin.conf.summary"), true, false);
      this.resinHomeSelector.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
         public void textChanged(@NotNull DocumentEvent event) {
            if (event == null) {
               $$$reportNull$$$0(0);
            }

            SelectResinLocationEditor.this.suggestConfPath = true;
            SelectResinLocationEditor.this.update();
         }

         // $FF: synthetic method
         private static void $$$reportNull$$$0(int var0) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "event", "org/intellij/j2ee/web/resin/ui/SelectResinLocationEditor$1", "textChanged"));
         }
      });
      this.defaultResinConf.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
         public void textChanged(@NotNull DocumentEvent event) {
            if (event == null) {
               $$$reportNull$$$0(0);
            }

            SelectResinLocationEditor.this.suggestConfPath = false;
            SelectResinLocationEditor.this.updateConfPath();
         }

         // $FF: synthetic method
         private static void $$$reportNull$$$0(int var0) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "event", "org/intellij/j2ee/web/resin/ui/SelectResinLocationEditor$2", "textChanged"));
         }
      });
      this.includeAllResinjarsCheckbox.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent e) {
            SelectResinLocationEditor.this.update();
         }
      });
      this.myErrorLabel.setIcon(General.BalloonError);
      this.update();
   }

   private void update() {
      String homePath = this.resinHomeSelector.getText();
      this.myErrorLabel.setText("");

      ResinInstallation installation;
      try {
         installation = ResinInstallation.create(homePath);
      } catch (ExecutionException var4) {
         this.myErrorLabel.setText(var4.getMessage());
         this.myErrorLabel.setVisible(true);
         this.myHasHomeError = true;
         return;
      }

      this.hideError();
      this.myHasHomeError = false;
      if (!installation.isVersionDetected()) {
         this.resinVersionLabel.setText(ResinBundle.message("location.dlg.detected.version.unknown"));
      } else {
         this.resinVersionLabel.setText(installation.getVersion().toString());
         if (this.suggestConfPath) {
            File resinConfDef = new File(homePath, FileUtil.toSystemDependentName("conf/resin.xml"));
            if (resinConfDef.exists()) {
               this.defaultResinConf.setText(resinConfDef.getAbsoluteFile().getAbsolutePath());
            } else {
               resinConfDef = new File(homePath, FileUtil.toSystemDependentName("conf/resin.conf"));
               if (resinConfDef.exists()) {
                  this.defaultResinConf.setText(resinConfDef.getAbsoluteFile().getAbsolutePath());
               }
            }
         }
      }

      this.updateConfPath();
   }

   private void updateConfPath() {
      if (!this.myHasHomeError) {
         String confFilePath = this.defaultResinConf.getText();
         File confFile = StringUtil.isEmpty(confFilePath) ? null : new File(confFilePath);
         if (confFile == null) {
            this.showError(ResinBundle.message("message.error.resin.conf.doesnt.chosen"));
         } else if (!confFile.exists()) {
            this.showError(ResinBundle.message("message.error.resin.conf.doesnt.exist", ""));
         } else if (confFile.isDirectory()) {
            this.showError(ResinBundle.message("message.error.resin.conf.directory", confFile.getAbsolutePath()));
         } else {
            this.hideError();
         }
      }
   }

   private void showError(String errorMsg) {
      this.myErrorLabel.setText(errorMsg);
      this.myErrorLabel.setVisible(true);
   }

   private void hideError() {
      this.myErrorLabel.setVisible(false);
   }

   protected void resetEditorFrom(@NotNull ResinPersistentData resinPersistentData) {
      if (resinPersistentData == null) {
         $$$reportNull$$$0(0);
      }

      this.suggestConfPath = resinPersistentData.RESIN_HOME == null;
      this.resinHomeSelector.setText(resinPersistentData.RESIN_HOME);
      this.includeAllResinjarsCheckbox.setSelected(resinPersistentData.INCLUDE_ALL_JARS);
      this.defaultResinConf.setText(resinPersistentData.RESIN_CONF);
      this.update();
   }

   protected void applyEditorTo(@NotNull ResinPersistentData data) {
      if (data == null) {
         $$$reportNull$$$0(1);
      }

      data.RESIN_HOME = this.resinHomeSelector.getText();
      data.RESIN_CONF = this.defaultResinConf.getText();
      data.INCLUDE_ALL_JARS = this.includeAllResinjarsCheckbox.isSelected();
   }

   protected @NotNull JComponent createEditor() {
      JPanel var10000 = this.mainPanel;
      if (var10000 == null) {
         $$$reportNull$$$0(2);
      }

      return var10000;
   }

   private static void initChooser(TextFieldWithBrowseButton field, String title, String description, boolean chooseFiles, boolean chooseDirs) {
      field.setText("");
      field.getTextField().setEditable(true);
      field.addBrowseFolderListener(title, description, (Project)null, new FileChooserDescriptor(chooseFiles, chooseDirs, false, false, false, false));
   }

   // $FF: synthetic method
   private void $$$setupUI$$$() {
      JPanel var1 = new JPanel();
      this.mainPanel = var1;
      var1.setLayout(new GridLayoutManager(6, 2, new Insets(0, 0, 0, 0), -1, -1, false, false));
      Spacer var2 = new Spacer();
      var1.add(var2, new GridConstraints(5, 1, 1, 1, 0, 2, 1, 6, (Dimension)null, (Dimension)null, (Dimension)null));
      JLabel var3 = new JLabel();
      this.$$$loadLabelText$$$(var3, DynamicBundle.getBundle("messages/ResinBundle", SelectResinLocationEditor.class).getString("location.dlg.resin.home.text"));
      var1.add(var3, new GridConstraints(0, 0, 1, 1, 8, 0, 0, 0, (Dimension)null, (Dimension)null, (Dimension)null));
      TextFieldWithBrowseButton var4 = new TextFieldWithBrowseButton();
      this.resinHomeSelector = var4;
      var1.add(var4, new GridConstraints(0, 1, 1, 1, 1, 1, 3, 0, (Dimension)null, new Dimension(200, -1), (Dimension)null));
      JLabel var5 = new JLabel();
      this.$$$loadLabelText$$$(var5, DynamicBundle.getBundle("messages/ResinBundle", SelectResinLocationEditor.class).getString("location.dlg.detected.version"));
      var1.add(var5, new GridConstraints(3, 0, 1, 1, 8, 0, 0, 0, (Dimension)null, (Dimension)null, (Dimension)null));
      JLabel var6 = new JLabel();
      this.resinVersionLabel = var6;
      this.$$$loadLabelText$$$(var6, DynamicBundle.getBundle("messages/ResinBundle", SelectResinLocationEditor.class).getString("location.dlg.detected.version.default"));
      var1.add(var6, new GridConstraints(3, 1, 1, 1, 8, 0, 0, 0, (Dimension)null, (Dimension)null, (Dimension)null));
      JLabel var7 = new JLabel();
      this.$$$loadLabelText$$$(var7, DynamicBundle.getBundle("messages/ResinBundle", SelectResinLocationEditor.class).getString("location.dlg.resin.include.jars"));
      var1.add(var7, new GridConstraints(1, 0, 1, 1, 8, 0, 0, 0, (Dimension)null, (Dimension)null, (Dimension)null));
      JCheckBox var8 = new JCheckBox();
      this.includeAllResinjarsCheckbox = var8;
      var8.setText("");
      var1.add(var8, new GridConstraints(1, 1, 1, 1, 8, 0, 3, 0, (Dimension)null, (Dimension)null, (Dimension)null));
      JLabel var9 = new JLabel();
      this.$$$loadLabelText$$$(var9, DynamicBundle.getBundle("messages/ResinBundle", SelectResinLocationEditor.class).getString("location.dlg.default.resin.conf"));
      var1.add(var9, new GridConstraints(2, 0, 1, 1, 8, 0, 0, 0, (Dimension)null, (Dimension)null, (Dimension)null));
      TextFieldWithBrowseButton var10 = new TextFieldWithBrowseButton();
      this.defaultResinConf = var10;
      var1.add(var10, new GridConstraints(2, 1, 1, 1, 1, 1, 3, 0, (Dimension)null, new Dimension(200, -1), (Dimension)null));
      JLabel var11 = new JLabel();
      this.myErrorLabel = var11;
      var11.setText("");
      var1.add(var11, new GridConstraints(4, 0, 1, 2, 8, 0, 0, 0, (Dimension)null, (Dimension)null, (Dimension)null));
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
   private static void $$$reportNull$$$0(int index) {
      if (index == 2) {
         throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "org/intellij/j2ee/web/resin/ui/SelectResinLocationEditor", "createEditor"));
      } else {
         String paramName = index == 1 ? "data" : "resinPersistentData";
         String methodName = index == 1 ? "applyEditorTo" : "resetEditorFrom";
         throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", paramName, "org/intellij/j2ee/web/resin/ui/SelectResinLocationEditor", methodName));
      }
   }
}
