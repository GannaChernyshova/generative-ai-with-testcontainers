package com.example.generativeai.testcontainers;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.store.embedding.CosineSimilarity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.example.generativeai.testcontainers.Base.embeddingModel;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class GetAnswerAgentTest2 {
    static Embedding referenceResponse;
    static String question = "In F1 2024 season which driver won the Great Britain Grand Prix?";

    @BeforeAll
    static void setUp() {
        referenceResponse = embeddingModel().embed(
                        "Lewis Hamilton won the Great Britain Grand Prix on 07 Jul 2024 driving for Mercedes")
                .content();
    }

    @Test
    void getStraightAnswer() {
        String straightAnswer = new GetAnswerAgent().getStraightAnswer(question);
        log.info("Straight Answer: {}", straightAnswer);

        Embedding currentResponse = embeddingModel().embed(straightAnswer).content();

        double similarity = CosineSimilarity.between(referenceResponse, currentResponse);

        log.info("Similarity: {}", similarity);
        assertTrue(similarity > 0.8, "similarity was " + similarity);
    }

    @Test
    void getRaggedAnswer() {
        String raggedAnswer = new GetAnswerAgent().getRaggedAnswer(question);
        log.info("Ragged Answer: {}", raggedAnswer);

        Embedding currentResponse = embeddingModel().embed(raggedAnswer).content();

        double similarity = CosineSimilarity.between(referenceResponse, currentResponse);

        log.info("Similarity: {}", similarity);
        assertTrue(similarity > 0.8, "similarity was " + similarity);
    }

}