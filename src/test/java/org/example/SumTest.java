package org.example;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SumTest {

  @Test
  @DisplayName("Test sum")
  void sum() {
    assertEquals(Sum.sum(1, 1), 2);
  }
}
