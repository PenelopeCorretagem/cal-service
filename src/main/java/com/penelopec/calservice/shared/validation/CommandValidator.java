package com.penelopec.calservice.shared.validation;

/**
 * Contrato para validadores de Command.
 * Cada UseCase (ou família de commands) tem o seu próprio validator.
 * <p>
 * Exemplo de implementação:
 *   public class CreateUserCommandValidator implements CommandValidator<CreateUserCommand> {
 *       public ValidationResult validate(CreateUserCommand cmd) { ... }
 *   }
 */
@FunctionalInterface
public interface CommandValidator<C> {

  ValidationResult validate(C command);

  /**
   * Atalho: valida e já lança se houver erros.
   * Uso no UseCase: validator.validateAndThrow(command);
   */
  default void validateAndThrow(C command) {
    validate(command).throwIfHasErrors();
  }
}