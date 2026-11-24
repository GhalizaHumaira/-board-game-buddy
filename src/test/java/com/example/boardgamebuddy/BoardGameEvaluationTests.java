package com.example.boardgamebuddy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class BoardGameEvaluationTests {

    @Autowired
    private BoardGameService boardGameService;

    @Test
    void shouldGenerateRelevantAnswerForCatanPlayers() {
        Question question = new Question(
                "How many players can play Catan in the base game? Answer concisely with the number of players."
        );

        Answer answer = boardGameService.askQuestion(question);

        assertNotNull(answer);
        assertNotNull(answer.answer());
        assertFalse(answer.answer().isBlank());

        String normalized = answer.answer().toLowerCase();
        boolean mentionsThree = normalized.contains("3") || normalized.contains("three");
        boolean mentionsFour = normalized.contains("4") || normalized.contains("four");

        assertTrue(
                mentionsThree || mentionsFour,
                "Expected answer about Catan base game players to mention 3 or 4 players, but got: " + answer.answer()
        );
    }
}