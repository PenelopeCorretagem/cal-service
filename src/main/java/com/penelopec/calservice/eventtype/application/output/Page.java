package com.penelopec.calservice.eventtype.application.output;

import java.util.List;

public record Page<T>(
  List<T> content,
  int page,
  int size,
  long totalElements,
  int totalPages
) {

  public static <T> Page<T> from(List<T> items, int page, int size) {
    int resolvedPage = Math.max(page, 0);
    int resolvedSize = size <= 0 ? 20 : size;

    long totalElements = items.size();
    int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / resolvedSize);

    int fromIndex = Math.min(resolvedPage * resolvedSize, items.size());
    int toIndex = Math.min(fromIndex + resolvedSize, items.size());

    return new Page<>(items.subList(fromIndex, toIndex), resolvedPage, resolvedSize, totalElements, totalPages);
  }
}