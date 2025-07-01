package com.example.generativeai.testcontainers;

import com.example.generativeai.testcontainers.agents.ValidatorAgent;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.example.generativeai.testcontainers.Base.chatModel;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class GetAnswerAgentTest3 {

    static String question = "In F1 2024 season which driver won the Great Britain Grand Prix?";

    static ValidatorAgent validatorAgent;
    final static String reference = """
            - Answer must indicate that Lewis Hamilton won the Great Britain Grand Prix.
            """;

    @BeforeAll
    static void setUp() {
        String baseUrl = "http://localhost:12434/engines/llama.cpp/v1";
        String modelName = "ai/gemma3";
        validatorAgent = AiServices.builder(ValidatorAgent.class)
                .chatLanguageModel(chatModel(modelName, baseUrl))
                .build();
    }

    @Test
    void getStraightAnswer() {
        String straightAnswer = new GetAnswerAgent().getStraightAnswer(question);
        log.info("Straight Answer: {}", straightAnswer);

        ValidatorAgent.ValidatorResponse validate = validatorAgent.validate(GetAnswerAgent.question, straightAnswer, reference);
        log.info("Validation: {}", validate);

        assertEquals("yes", validate.response());
    }

    @Test
    void getRaggedAnswer() {
        String raggedAnswer = new GetAnswerAgent().getRaggedAnswer(question);
        log.info("Ragged Answer: {}", raggedAnswer);

        ValidatorAgent.ValidatorResponse validate = validatorAgent.validate(GetAnswerAgent.question, raggedAnswer, reference);
        log.info("Validation: {}", validate);

        assertEquals("yes", validate.response());
    }

}