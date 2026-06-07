package com.penelopec.calservice.shared.pagination;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PageTest {

  @Test
  void shouldResolveDefaults_whenPageAndSizeAreInvalid() {
    // Given
    List<String> items = List.of("A", "B");

    // When
    Page<String> page = Page.from(items, -1, 0);

    // Then
    assertEquals(0, page.page());
    assertEquals(20, page.size());
    assertEquals(2, page.totalElements());
    assertEquals(1, page.totalPages());
    assertEquals(List.of("A", "B"), page.content());
  }

  @Test
  void shouldReturnSliceForRequestedPage_whenPaginationIsApplied() {
    // Given
    List<Integer> items = List.of(1, 2, 3, 4, 5, 6, 7);

    // When
    Page<Integer> page = Page.from(items, 1, 3);

    // Then
    assertEquals(1, page.page());
    assertEquals(3, page.size());
    assertEquals(7, page.totalElements());
    assertEquals(3, page.totalPages());
    assertEquals(List.of(4, 5, 6), page.content());
  }
}

