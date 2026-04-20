package com.penelopec.calservice.shared.validation;

import com.penelopec.calservice.shared.error.core.ErrorContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Notification Pattern: acumula todos os erros de validação sem interromper no primeiro.
 * <p>
 * Regra de ouro: nunca use if/return dentro do validator.
 * Cada verificação é independente via addErrorIf.
 * <p>
 * Todos os métodos exigem ErrorContract tipado — nenhuma String solta de código é aceita.
 * Para validações com IO (ex: unicidade no banco), use as sobrecargas com Supplier<Boolean>
 * para avaliação lazy (executada somente quando erros anteriores não impedem).
 */
public final class ValidationResult {

  private final List<ValidationError> errors = new ArrayList<>();

  public ValidationResult addError(String field, ErrorContract code) {
    errors.add(ValidationError.of(field, code));
    return this;
  }

  public ValidationResult addGlobal(ErrorContract code) {
    errors.add(ValidationError.global(code));
    return this;
  }

  /** Adiciona erro de campo somente se condition for true (avaliação imediata). */
  public ValidationResult addErrorIf(boolean condition, String field, ErrorContract code) {
    if (condition) addError(field, code);
    return this;
  }

  /** Adiciona erro global somente se condition for true (avaliação imediata). */
  public ValidationResult addGlobalIf(boolean condition, ErrorContract code) {
    if (condition) addGlobal(code);
    return this;
  }

  /**
   * Adiciona erro de campo somente se o Supplier retornar true.
   * O Supplier é avaliado apenas se não houver erros acumulados até este ponto,
   * evitando consultas desnecessárias ao banco quando o command já é inválido.
   */
  public ValidationResult addErrorIf(Supplier<Boolean> condition, String field, ErrorContract code) {
    if (!hasErrors() && Boolean.TRUE.equals(condition.get())) addError(field, code);
    return this;
  }

  /**
   * Adiciona erro global somente se o Supplier retornar true (avaliação lazy).
   */
  public ValidationResult addGlobalIf(Supplier<Boolean> condition, ErrorContract code) {
    if (!hasErrors() && Boolean.TRUE.equals(condition.get())) addGlobal(code);
    return this;
  }

  /** Permite compor results de sub-validadores (ex: validar sub-objetos separadamente). */
  public ValidationResult merge(ValidationResult other) {
    this.errors.addAll(other.errors);
    return this;
  }

  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  public List<ValidationError> getErrors() {
    return Collections.unmodifiableList(errors);
  }

  /**
   * Lança ValidationException com todos os erros acumulados.
   * Noop se não há erros.
   */
  public void throwIfHasErrors() {
    if (hasErrors()) {
      throw new ValidationException(errors);
    }
  }
}
