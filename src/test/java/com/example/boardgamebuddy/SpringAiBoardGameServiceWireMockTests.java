package com.example.boardgamebuddy;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.nio.charset.Charset;

@SpringBootTest
public class SpringAiBoardGameServiceWireMockTests {

    private static MockWebServer mockWebServer;

    @Value("classpath:/test-openai-response.json")
    Resource responseResource;

    @Autowired
    ChatClient.Builder chatClientBuilder;

    @BeforeAll
    static void startServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void stopServer() throws IOException {
        if (mockWebServer != null) {
            mockWebServer.shutdown();
        }
    }

    @DynamicPropertySource
    static void registerOpenAiBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("spring.ai.openai.base-url", () -> mockWebServer.url("/").toString().replaceAll("/$", ""));
        registry.add("spring.ai.openai.api-key", () -> "test-api-key");
    }

    @Test
    void testAskQuestion() throws IOException {
        var cannedResponse = responseResource.getContentAsString(Charset.defaultCharset());
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(cannedResponse));

        var boardGameService = new SpringAiBoardGameService(chatClientBuilder);
        var answer = boardGameService.askQuestion(
                new Question("What is the capital of France?"));

        Assertions.assertThat(answer).isNotNull();
        Assertions.assertThat(answer.answer()).isEqualTo("Paris");
    }
}