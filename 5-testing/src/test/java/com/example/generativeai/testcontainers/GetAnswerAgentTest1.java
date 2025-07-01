package com.example.generativeai.testcontainers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class GetAnswerAgentTest1 {

	static String question = "In F1 2024 season which driver won the Great Britain Grand Prix?";

	@Test
	void getStraightAnswer() {
		String straightAnswer = new GetAnswerAgent().getStraightAnswer(question);
		log.info("Straight Answer: {}", straightAnswer);
		assertTrue(straightAnswer.contains("Lewis Hamilton"));
	}

	@Test
	void getRaggedAnswer() {
		String raggedAnswer = new GetAnswerAgent().getRaggedAnswer(question);
		log.info("Ragged Answer: {}", raggedAnswer);
		assertTrue(raggedAnswer.contains("Lewis Hamilton"));
	}

}