package com.example.generativeai.testcontainers;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.output.Response;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Streaming {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
		String apiKey = dotenv.get("OPENAI_API_KEY");

		OpenAiStreamingChatModel model = OpenAiStreamingChatModel.builder()
				.apiKey(apiKey)
				.modelName("gpt-4o-mini")
				.build();

		model.generate("Give me a detailed and long explanation of why Testcontainers is great",
				new StreamingResponseHandler<>() {
					@Override
					public void onNext(String token) {
						System.out.print(token);
					}

					@Override
					public void onError(Throwable error) {
						System.out.println("Error: " + error.getMessage());
					}

					@Override
					public void onComplete(Response<AiMessage> response) {
						System.exit(0);
					}
				});
	}

}
