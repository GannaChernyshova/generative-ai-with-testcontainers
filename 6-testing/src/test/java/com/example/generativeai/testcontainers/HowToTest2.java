package com.example.generativeai.testcontainers;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.store.embedding.CosineSimilarity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.example.generativeai.testcontainers.Base.embeddingModel;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class HowToTest2 {
    static Embedding referenceResponse;

    @BeforeAll
    static void setUp() {
        referenceResponse = embeddingModel().embed(
                        "To enable verbose logging in Testcontainers Desktop you can set the property cloud.logs.verbose to true in the ~/.testcontainers.properties file file or add the --verbose flag when running the cli")
                .content();
    }

    @Test
    void getStraightAnswer() {
        String straightAnswer = HowTo.getStraightAnswer();
        log.info("Straight Answer: {}", straightAnswer);

        Embedding currentResponse = embeddingModel().embed(straightAnswer).content();

        double similarity = CosineSimilarity.between(referenceResponse, currentResponse);

        log.info("Similarity: {}", similarity);
        assertTrue(similarity > 0.8, "similarity was " + similarity);
    }

    @Test
    void getRaggedAnswer() {
        String raggedAnswer = HowTo.getRaggedAnswer();
        log.info("Ragged Answer: {}", raggedAnswer);

        Embedding currentResponse = embeddingModel().embed(raggedAnswer).content();

        double similarity = CosineSimilarity.between(referenceResponse, currentResponse);

        log.info("Similarity: {}", similarity);
        assertTrue(similarity > 0.8, "similarity was " + similarity);
    }

}