package org.techmarket.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceImplTest {

  @Mock
  private JavaMailSender javaMailSender;

  @InjectMocks
  private EmailServiceImpl emailService;

  @BeforeEach
  void setUp() {
    // simulate injection of @Value("${spring.mail.username}")
    ReflectionTestUtils.setField(emailService, "userMail", "techmarketJP@gmail.com");
  }

  @Test
  void whenSendMessage_thenShouldReceiveEmail() {
    // given
    String to = "receiver@example.com";
    String subject = "Test Subject";
    String text = "Test Body";

    // when
    emailService.sendMessage(to, subject, text);

    // then
    ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
    verify(javaMailSender, times(1)).send(messageCaptor.capture());

    SimpleMailMessage sentMessage = messageCaptor.getValue();
    Assertions.assertEquals("techmarketJP@gmail.com", sentMessage.getFrom());
    Assertions.assertEquals(to, sentMessage.getTo()[0]);
    Assertions.assertEquals(subject, sentMessage.getSubject());
    Assertions.assertEquals(text, sentMessage.getText());
  }

  @Test
  void whenSendMessageWithRuntimeError_thenShouldThrowException() {
    // given
    String to = "receiver@example.com";
    String subject = "Fail Subject";
    String text = "Fail Body";

    doThrow(new MailSendException("SMTP error")).when(javaMailSender).send(any(SimpleMailMessage.class));

    // when / then
    RuntimeException exception = assertThrows(RuntimeException.class, () ->
        emailService.sendMessage(to, subject, text)
    );

    assertInstanceOf(MailSendException.class, exception.getCause());
  }
}
