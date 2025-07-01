package com.example.generativeai.testcontainers;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.openai.OpenAiChatModel;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.DockerModelRunnerContainer;

import java.util.List;

@Slf4j
public class DockerModelRun {

    public static void main(String[] args) {
        String baseUrl = "http://localhost:12434/engines/llama.cpp/v1";
        String modelName = "ai/gemma3"; // Model name

        // Initialize the Langchain4j OpenAI-compatible model
        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(modelName)
                .apiKey("not needed")
                .logRequests(true)
                .logResponses(true)
                .build();

        // Construct messages
        List<ChatMessage> messages = List.of(
                new SystemMessage("You are a helpful assistant."),
                new UserMessage("Please write 500 words about the fall of Rome.")
        );

        // Get the model's response
        String response = String.valueOf(model.generate(messages));

        // Print the response
        System.out.println("Response from model: " + response);
    }

}
