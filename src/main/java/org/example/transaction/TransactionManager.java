package org.example.transaction;

import java.util.function.Supplier;

public interface TransactionManager {

  <R> R inTransaction(Supplier<R> supplier);

  void useTransaction(Runnable runnable);
}
