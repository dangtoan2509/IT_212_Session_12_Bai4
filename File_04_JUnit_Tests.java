package com.abcbank.ekyc;

import com.abcbank.ekyc.controller.AccountController;
import com.abcbank.ekyc.dto.AccountRegistrationRequest;
import com.abcbank.ekyc.dto.AccountRegistrationResponse;
import com.abcbank.ekyc.entity.Account;
import com.abcbank.ekyc.entity.AccountStatus;
import com.abcbank.ekyc.exception.DuplicateResourceException;
import com.abcbank.ekyc.repository.AccountRepository;
import com.abcbank.ekyc.service.AccountService;
import com.abcbank.ekyc.service.impl.AccountServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * FILE 04: Mã nguồn JUnit Test (sử dụng Mockito)
 * Gồm 2 phần test: AccountServiceImplTest và AccountControllerTest
 * Lưu ý: Đặt chung trong 1 file để tiện nộp bài. Khi thực tế có thể tách ra thành 2 file riêng.
 */
class File_04_JUnit_Tests {

    /**
     * 1. Unit Test cho AccountServiceImpl
     */
    @ExtendWith(MockitoExtension.class)
    static class AccountServiceImplTest {

        @Mock
        private AccountRepository accountRepository;

        @InjectMocks
        private AccountServiceImpl accountService;

        private AccountRegistrationRequest validRequest;

        @BeforeEach
        void setUp() {
            validRequest = new AccountRegistrationRequest();
            validRequest.setFullName("Nguyen Van A");
            validRequest.setPhone("0912345678");
            validRequest.setEmail("nguyenvana@gmail.com");
            validRequest.setCitizenId("001090123456");
        }

        @Test
        void testRegisterAccount_Success() {
            // Arrange
            when(accountRepository.existsByCitizenId(validRequest.getCitizenId())).thenReturn(false);
            when(accountRepository.existsByPhone(validRequest.getPhone())).thenReturn(false);

            Account mockSavedAccount = new Account();
            mockSavedAccount.setId(UUID.randomUUID());
            mockSavedAccount.setAccountNumber("123456789012");
            mockSavedAccount.setStatus(AccountStatus.PENDING);
            
            when(accountRepository.save(any(Account.class))).thenReturn(mockSavedAccount);

            // Act
            AccountRegistrationResponse response = accountService.registerAccount(validRequest);

            // Assert
            assertNotNull(response);
            assertEquals(mockSavedAccount.getId(), response.getAccountId());
            assertEquals("123456789012", response.getAccountNumber());
            assertEquals("PENDING", response.getStatus());
            Mockito.verify(accountRepository, Mockito.times(1)).save(any(Account.class));
        }

        @Test
        void testRegisterAccount_DuplicateCitizenId_ThrowsException() {
            // Arrange
            when(accountRepository.existsByCitizenId(validRequest.getCitizenId())).thenReturn(true);

            // Act & Assert
            DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
                accountService.registerAccount(validRequest);
            });
            assertEquals("Số CCCD đã tồn tại trên hệ thống!", exception.getMessage());
            Mockito.verify(accountRepository, Mockito.never()).save(any());
        }

        @Test
        void testRegisterAccount_DuplicatePhone_ThrowsException() {
            // Arrange
            when(accountRepository.existsByCitizenId(validRequest.getCitizenId())).thenReturn(false);
            when(accountRepository.existsByPhone(validRequest.getPhone())).thenReturn(true);

            // Act & Assert
            DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
                accountService.registerAccount(validRequest);
            });
            assertEquals("Số điện thoại đã được đăng ký!", exception.getMessage());
            Mockito.verify(accountRepository, Mockito.never()).save(any());
        }
    }

    /**
     * 2. Unit Test cho AccountController
     */
    @ExtendWith(MockitoExtension.class)
    static class AccountControllerTest {

        private MockMvc mockMvc;

        @Mock
        private AccountService accountService;

        @InjectMocks
        private AccountController accountController;

        private ObjectMapper objectMapper = new ObjectMapper();

        @BeforeEach
        void setUp() {
            mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
        }

        @Test
        void testRegisterAccount_Success() throws Exception {
            // Arrange
            AccountRegistrationRequest request = new AccountRegistrationRequest();
            request.setFullName("Nguyen Van A");
            request.setPhone("0912345678");
            request.setEmail("a@b.com");
            request.setCitizenId("001090123456");

            AccountRegistrationResponse mockResponse = AccountRegistrationResponse.builder()
                    .accountId(UUID.randomUUID())
                    .accountNumber("123456789012")
                    .status("PENDING")
                    .build();

            when(accountService.registerAccount(any(AccountRegistrationRequest.class))).thenReturn(mockResponse);

            // Act & Assert
            mockMvc.perform(post("/api/v1/accounts/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.accountNumber").value("123456789012"))
                    .andExpect(jsonPath("$.status").value("PENDING"));
        }
    }
}
