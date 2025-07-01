package com.example.generativeai.testcontainers;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocumentsRecursively;

public class Base {
    static String embeddingBaseUrl = "http://localhost:12434/engines/llama.cpp/v1";
    static String embeddingModelName = "ai/mxbai-embed-large";

    public Base() {
    }

    //Custom LLM, can be replaced with the GPT, Sonnet, etc.
    static ChatLanguageModel chatModel(String modelName, String baseUrl) {
        // Initialize the Langchain4j OpenAI-compatible model
        return OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(modelName)
                .apiKey("not-needed")
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    //RAG
    static ContentRetriever contentRetriever() {
        var store = store();
        var model = embeddingModel();
        List<Document> txtDocuments = loadDocumentsRecursively(toPath("knowledge/json"), new TextDocumentParser());

        EmbeddingStoreIngestor.builder()
                .embeddingStore(store)
                .embeddingModel(model)
                .documentSplitter(new DocumentByParagraphSplitter(1024, 100))
                .build()
                .ingest(txtDocuments);

        return EmbeddingStoreContentRetriever.builder()
                .embeddingModel(model)
                .embeddingStore(store)
                .maxResults(3)
                .minScore(0.7)
                .build();
    }

    private static Path toPath(String fileName) {
        try {
            URL fileUrl = Base.class.getClassLoader().getResource(fileName);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static EmbeddingModel embeddingModel() {
        return OpenAiEmbeddingModel.builder()
                .baseUrl(embeddingBaseUrl)
                .apiKey("not-needed")
                .modelName(embeddingModelName)
                .build();
    }

    private static EmbeddingStore<TextSegment> store() {
        var pgVector = new PostgreSQLContainer<>(
                DockerImageName.parse("pgvector/pgvector:pg16").asCompatibleSubstituteFor("postgres"));
        pgVector.start();

        return PgVectorEmbeddingStore.builder()
                .host(pgVector.getHost())
                .port(pgVector.getFirstMappedPort())
                .database(pgVector.getDatabaseName())
                .user(pgVector.getUsername())
                .password(pgVector.getPassword())
                .table("test")
                .dimension(1024)
                .build();
    }

}
