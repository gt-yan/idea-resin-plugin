package org.intellij.j2ee.web.resin.resin.jmx;

import com.intellij.javaee.oss.util.AbstractConnectorCommand;
import com.intellij.openapi.diagnostic.Logger;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.intellij.j2ee.web.resin.ResinModelBase;
import org.jetbrains.annotations.Nullable;

public abstract class ConnectorCommandBase<T> extends AbstractConnectorCommand<T> {
   private static final Logger LOG = Logger.getInstance(ConnectorCommandBase.class);
   private final ResinModelBase myResinModel;
   private T myResult;

   public ConnectorCommandBase(ResinModelBase resinModel) {
      this.myResinModel = resinModel;
   }

   protected String getHost() {
      return this.myResinModel.getCommonModel().getHost();
   }

   protected int getJmxPort() {
      return this.myResinModel.getJmxPort();
   }

   public boolean safeExecute() {
      try {
         this.myResult = this.execute();
         return true;
      } catch (ExecutionException | TimeoutException var2) {
         LOG.debug(var2);
         return false;
      }
   }

   public T getResult() {
      return this.myResult;
   }

   protected @Nullable String getJmxUsername() {
      return this.myResinModel.getJmxUsername();
   }

   protected @Nullable String getJmxPassword() {
      return this.myResinModel.getJmxPassword();
   }
}
