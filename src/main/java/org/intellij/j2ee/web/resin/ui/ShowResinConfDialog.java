package org.intellij.j2ee.web.resin.ui;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import org.intellij.j2ee.web.resin.ResinBundle;

public class ShowResinConfDialog extends JDialog {
   private JPanel contentPane;
   private JButton buttonOK;
   private JTextArea textArea;

   public ShowResinConfDialog(String text) {
      this.$$$setupUI$$$();
      this.setContentPane(this.contentPane);
      this.setModal(true);
      this.getRootPane().setDefaultButton(this.buttonOK);
      this.buttonOK.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            ShowResinConfDialog.this.onOK();
         }
      });
      this.setTitle(ResinBundle.message("message.text.resin.conf.altered"));
      this.textArea.setText(text);
      this.textArea.setEditable(false);
      this.contentPane.setPreferredSize(new Dimension(640, 480));
   }

   private void onOK() {
      this.dispose();
   }

   // $FF: synthetic method
   private void $$$setupUI$$$() {
      JPanel var1 = new JPanel();
      this.contentPane = var1;
      var1.setLayout(new GridLayoutManager(2, 3, new Insets(10, 10, 10, 10), -1, -1, false, false));
      JBScrollPane var2 = new JBScrollPane();
      var1.add(var2, new GridConstraints(0, 0, 1, 3, 0, 3, 7, 7, (Dimension)null, (Dimension)null, (Dimension)null));
      JTextArea var3 = new JTextArea();
      this.textArea = var3;
      var3.setLineWrap(true);
      var3.setWrapStyleWord(true);
      var2.setViewportView(var3);
      JButton var4 = new JButton();
      this.buttonOK = var4;
      var4.setText("OK");
      var1.add(var4, new GridConstraints(1, 2, 1, 1, 0, 1, 3, 0, (Dimension)null, (Dimension)null, (Dimension)null));
      JPanel var5 = new JPanel();
      var1.add(var5, new GridConstraints(1, 0, 1, 1, 0, 3, 3, 3, (Dimension)null, (Dimension)null, (Dimension)null));
      Spacer var6 = new Spacer();
      var1.add(var6, new GridConstraints(1, 1, 1, 1, 0, 1, 6, 1, (Dimension)null, (Dimension)null, (Dimension)null));
   }

   // $FF: synthetic method
   public JComponent $$$getRootComponent$$$() {
      return this.contentPane;
   }
}
