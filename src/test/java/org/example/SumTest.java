package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SumTest {

  @Test
  @DisplayName("Test sum")
  void sum() {
    assertEquals(Sum.sum(1, 1), 2);
  }
}
