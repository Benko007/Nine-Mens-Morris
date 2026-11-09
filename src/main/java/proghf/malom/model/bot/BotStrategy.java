package proghf.malom.model.bot;

import proghf.malom.model.GameEngine;

public interface BotStrategy {

    /**
     * Executes a move for the bot based on the current game state.
     * @param engine The current game engine instance.
     */
    void executeMove(GameEngine engine);
    
}
