package com.example.generativeai.testcontainers;

import com.example.generativeai.testcontainers.agents.ChatAgent;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GetAnswerAgent extends Base {

    private String baseUrl = "http://localhost:12434/engines/llama.cpp/v1";
    private String modelName = "ai/gemma3n";

    static String question = "In F1 2024 season which driver won the Great Britain Grand Prix?";

    public GetAnswerAgent() {
    }

    public GetAnswerAgent(String chatBaseUrl, String chatModelName) {
        this.baseUrl = chatBaseUrl;
        this.modelName = chatModelName;
    }

    public static void main(String[] args) {

        String straightAnswer = new GetAnswerAgent().getStraightAnswer(question);
        log.info("Question: {} - Straight Answer: {}", question, straightAnswer);

        String raggedAnswer = new GetAnswerAgent().getRaggedAnswer(question);
        log.info("Question: {} - Ragged Answer: {}", question, raggedAnswer);

    }

    public String getStraightAnswer(String question) {
        ChatAgent straight = AiServices.builder(ChatAgent.class)
                .chatLanguageModel(chatModel(modelName, baseUrl))
                .build();
        return straight.chat(question);
    }

    public String getRaggedAnswer(String question) {
        ChatAgent ragged = AiServices.builder(ChatAgent.class)
                .chatLanguageModel(chatModel(modelName, baseUrl))
                .contentRetriever(contentRetriever())
                .build();
        return ragged.chat(question);
    }

}