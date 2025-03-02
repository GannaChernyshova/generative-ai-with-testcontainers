package com.example.generativeai.testcontainers;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.ollama.OllamaContainer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public class AugmentedGeneration {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
		String apiKey = dotenv.get("OPENAI_API_KEY");

		OpenAiChatModel model = OpenAiChatModel.builder()
				.apiKey(apiKey)
				.modelName("gpt-4o-mini")
				.build();

		var orginalMessage = """
				What is the current topic of the JUG meeting?
				""";

		var augmentedMessage = """
				%s

				Use the following bullet points to answer the question:
				- The JUG meeting is about how to leverage Testcontainers for building Generative AI applications.
				- The meeting will explore how Testcontainers can be used to create a seamless development environment for AI projects.

				Do not indicate that you have been given any additional information.
				"""
			.formatted(orginalMessage);

		String answer1 = model.generate(orginalMessage);
		log.info("Response from LLM without augmentation (\uD83E\uDD16)-> {}", answer1);

		String answer2 = model.generate(augmentedMessage);
		log.info("Response from LLM with augmentation (\uD83E\uDD16)-> {}", answer2);
	}

}
