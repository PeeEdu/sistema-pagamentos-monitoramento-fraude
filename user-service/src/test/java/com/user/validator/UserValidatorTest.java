package com.user.validator;

import com.user.exceptions.UserAlreadyExistsException;
import com.user.exceptions.UserNotFoundException;
import com.user.repository.UserRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserValidatorTest {

    private final UserRepository userRepository;
    private final UserValidator userValidator;

    UserValidatorTest() {
        this.userRepository = mock(UserRepository.class);
        this.userValidator = new UserValidator(userRepository);
    }

    @Test
    void validateEmailNotExists_DeveExecutarSemLancarExcecao_QuandoEmailNaoExistir() {
        String email = "joao.silva@email.com";

        when(userRepository.existsByEmail(email)).thenReturn(false);

        assertDoesNotThrow(() -> userValidator.validateEmailNotExists(email));

        verify(userRepository).existsByEmail(email);
    }

    @Test
    void validateEmailNotExists_DeveLancarUserAlreadyExistsException_QuandoEmailJaExistir() {
        String email = "joao.silva@email.com";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(
                UserAlreadyExistsException.class,
                () -> userValidator.validateEmailNotExists(email)
        );

        verify(userRepository).existsByEmail(email);
    }

    @Test
    void validateCpfNotExists_DeveExecutarSemLancarExcecao_QuandoCpfNaoExistir() {
        String cpf = "52998224725";

        when(userRepository.existsByCpf(cpf)).thenReturn(false);

        assertDoesNotThrow(() -> userValidator.validateCpfNotExists(cpf));

        verify(userRepository).existsByCpf(cpf);
    }

    @Test
    void validateCpfNotExists_DeveLancarUserAlreadyExistsException_QuandoCpfJaExistir() {
        String cpf = "52998224725";

        when(userRepository.existsByCpf(cpf)).thenReturn(true);

        assertThrows(
                UserAlreadyExistsException.class,
                () -> userValidator.validateCpfNotExists(cpf)
        );

        verify(userRepository).existsByCpf(cpf);
    }

    @Test
    void validatePhoneNotExists_DeveExecutarSemLancarExcecao_QuandoTelefoneNaoExistir() {
        String phone = "11999999999";

        when(userRepository.existsByPhone(phone)).thenReturn(false);

        assertDoesNotThrow(() -> userValidator.validatePhoneNotExists(phone));

        verify(userRepository).existsByPhone(phone);
    }

    @Test
    void validatePhoneNotExists_DeveLancarUserAlreadyExistsException_QuandoTelefoneJaExistir() {
        String phone = "11999999999";

        when(userRepository.existsByPhone(phone)).thenReturn(true);

        assertThrows(
                UserAlreadyExistsException.class,
                () -> userValidator.validatePhoneNotExists(phone)
        );

        verify(userRepository).existsByPhone(phone);
    }

    @Test
    void validateUserExists_DeveExecutarSemLancarExcecao_QuandoUsuarioExistir() {
        String id = "user-123";

        when(userRepository.existsById(id)).thenReturn(true);

        assertDoesNotThrow(() -> userValidator.validateUserExists(id));

        verify(userRepository).existsById(id);
    }

    @Test
    void validateUserExists_DeveLancarUserNotFoundException_QuandoUsuarioNaoExistir() {
        String id = "user-123";

        when(userRepository.existsById(id)).thenReturn(false);

        assertThrows(
                UserNotFoundException.class,
                () -> userValidator.validateUserExists(id)
        );

        verify(userRepository).existsById(id);
    }

    @Test
    void validateNewUser_DeveExecutarTodasValidacoes_QuandoDadosNaoExistirem() {
        String email = "joao.silva@email.com";
        String cpf = "52998224725";
        String phone = "11999999999";

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByCpf(cpf)).thenReturn(false);
        when(userRepository.existsByPhone(phone)).thenReturn(false);

        assertDoesNotThrow(() -> userValidator.validateNewUser(email, cpf, phone));

        verify(userRepository).existsByEmail(email);
        verify(userRepository).existsByCpf(cpf);
        verify(userRepository).existsByPhone(phone);
    }

    @Test
    void validateNewUser_DeveLancarUserAlreadyExistsException_QuandoEmailJaExistir() {
        String email = "joao.silva@email.com";
        String cpf = "52998224725";
        String phone = "11999999999";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(
                UserAlreadyExistsException.class,
                () -> userValidator.validateNewUser(email, cpf, phone)
        );

        verify(userRepository).existsByEmail(email);
        verify(userRepository, never()).existsByCpf(anyString());
        verify(userRepository, never()).existsByPhone(anyString());
    }

    @Test
    void validateNewUser_DeveLancarUserAlreadyExistsException_QuandoCpfJaExistir() {
        String email = "joao.silva@email.com";
        String cpf = "52998224725";
        String phone = "11999999999";

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByCpf(cpf)).thenReturn(true);

        assertThrows(
                UserAlreadyExistsException.class,
                () -> userValidator.validateNewUser(email, cpf, phone)
        );

        verify(userRepository).existsByEmail(email);
        verify(userRepository).existsByCpf(cpf);
        verify(userRepository, never()).existsByPhone(anyString());
    }

    @Test
    void validateNewUser_DeveLancarUserAlreadyExistsException_QuandoTelefoneJaExistir() {
        String email = "joao.silva@email.com";
        String cpf = "52998224725";
        String phone = "11999999999";

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByCpf(cpf)).thenReturn(false);
        when(userRepository.existsByPhone(phone)).thenReturn(true);

        assertThrows(
                UserAlreadyExistsException.class,
                () -> userValidator.validateNewUser(email, cpf, phone)
        );

        verify(userRepository).existsByEmail(email);
        verify(userRepository).existsByCpf(cpf);
        verify(userRepository).existsByPhone(phone);
    }
}