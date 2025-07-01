package com.example.generativeai.testcontainers;

import com.example.generativeai.testcontainers.agents.ValidatorAgent;
import dev.langchain4j.service.AiServices;
import io.github.microcks.testcontainers.MicrocksContainer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.example.generativeai.testcontainers.Base.chatModel;
import static org.junit.jupiter.api.Assertions.assertEquals;


@Testcontainers
@Slf4j
class OpenAIServiceMicrocksTest {
    static String microcksUrl;
    static String question = "In F1 2024 season which driver won the Great Britain Grand Prix?";

    static ValidatorAgent validatorAgent;
    final static String reference = """
            - Answer must indicate that Lewis Hamilton won the Great Britain Grand Prix.
            """;

    @Container
    static MicrocksContainer microcks = new MicrocksContainer("quay.io/microcks/microcks-uber:latest")
            .withMainArtifacts("openai-api-mock.yaml")
            .withEnv("LOGGING_LEVEL_IO_GITHUB_MICROCKS", "DEBUG")
            .withEnv("LOGGING_LEVEL_IO_GITHUB_MICROCKS_WEB", "DEBUG")
            .withEnv("LOGGING_LEVEL_ROOT", "INFO");

    @BeforeAll
    static void setUp() {
        microcksUrl = microcks.getRestMockEndpoint("OpenAI_API", "1.0");
        log.info("Microcks mock URL: {}", microcksUrl);

        String baseValidatorUrl = "http://localhost:12434/engines/llama.cpp/v1";
        String validatorModelName = "ai/gemma3";
        validatorAgent = AiServices.builder(ValidatorAgent.class)
                .chatLanguageModel(chatModel(validatorModelName, baseValidatorUrl))
                .build();
    }

    @Test
    void testCorrectResponse() {
        GetAnswerAgent getAnswerAgent = new GetAnswerAgent(microcksUrl, "gpt-3.5-turbo");
        String response = getAnswerAgent.getStraightAnswer(question);
        log.info("Straight Answer: {}", response);

        ValidatorAgent.ValidatorResponse validate = validatorAgent.validate(question, response, reference);
        log.info("Validation: {}", validate);

        assertEquals("yes", validate.response());
    }

    @Test
    void testModelHallucinate() {
        GetAnswerAgent getAnswerAgent = new GetAnswerAgent(microcksUrl, "hallucinate");
        String response = getAnswerAgent.getStraightAnswer(question);
        log.info("Straight Answer: {}", response);

        ValidatorAgent.ValidatorResponse validate = validatorAgent.validate(question, response, reference);
        log.info("Validation: {}", validate);

        assertEquals("no", validate.response());
    }
}
