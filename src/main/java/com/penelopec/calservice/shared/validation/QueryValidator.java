package com.penelopec.calservice.shared.validation;

/**
 * Contrato para validadores de Query (operações de leitura).
 * Paralelo ao CommandValidator, cobre filtros, paginação e parâmetros de busca.
 * <p>
 * Exemplo:
 *   <java>
 *   public class ListAppointmentsQueryValidator implements QueryValidator<ListAppointmentsQuery> {
 *       public ValidationResult validate(ListAppointmentsQuery query) { ... }
 *   }
 *   </java>
 */
@FunctionalInterface
public interface QueryValidator<Q> {

  ValidationResult validate(Q query);

  default void validateAndThrow(Q query) {
    validate(query).throwIfHasErrors();
  }
}
