package com.example.generativeai.testcontainers;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ollama.OllamaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

@Slf4j
public class RagWithServices {
    static String apiKey;

    interface Assistant {

        String generate(String input);

    }

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        apiKey = dotenv.get("OPENAI_API_KEY");

        EmbeddingModel embeddingModel = buildEmbeddingModel();
        EmbeddingStore<TextSegment> store = buildEmbeddingStore();

        ingestion(embeddingModel, store);

        ChatLanguageModel chatModel = buildChatModel();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(chatModel)
                .contentRetriever(EmbeddingStoreContentRetriever.builder()
                        .embeddingModel(embeddingModel)
                        .embeddingStore(store)
                        .maxResults(1)
                        .build())
                .build();

        String response = assistant.generate("What is my favorite sport?");

        log.info("Response from LLM (\uD83E\uDD16)-> {}", response);
    }

    private static void ingestion(EmbeddingModel model, EmbeddingStore<TextSegment> store) {
        TextSegment segment1 = TextSegment.from("I like football.");
        Embedding embedding1 = model.embed(segment1).content();
        store.add(embedding1, segment1);

        TextSegment segment2 = TextSegment.from("It's cold in Ottawa today");
        Embedding embedding2 = model.embed(segment2).content();
        store.add(embedding2, segment2);
    }

    private static EmbeddingModel buildEmbeddingModel() {
        return OpenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                //returns a vector (list of floating-point numbers) that represents the meaning of the text in a high-dimensional space.
                .modelName("text-embedding-3-small")
                .build();
    }

    private static ChatLanguageModel buildChatModel() {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gpt-4o-mini")
                .build();
    }

    private static PgVectorEmbeddingStore buildEmbeddingStore() {
        var pgVector = new PostgreSQLContainer<>(
                DockerImageName.parse("pgvector/pgvector:pg16")
                        .asCompatibleSubstituteFor("postgres"));
        pgVector.start();
        return PgVectorEmbeddingStore.builder()
                .host(pgVector.getHost())
                .port(pgVector.getFirstMappedPort())
                .database(pgVector.getDatabaseName())
                .user(pgVector.getUsername())
                .password(pgVector.getPassword())
                .table("test")
                .dimension(1536)
                .build();
    }

}
