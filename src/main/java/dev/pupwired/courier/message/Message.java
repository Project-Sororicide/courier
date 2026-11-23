package dev.pupwired.courier.message;

import dev.pupwired.luckylib.api.message.Color;
import dev.pupwired.luckylib.api.message.LuckyMessage;

public enum Message implements LuckyMessage {
    COMMAND_PLAYER_ONLY("Player only command.", Color.WARNING),
    CHANNEL_SWITCHED("Now speaking in %s.", Color.PASS),
    CHANNEL_ALREADY("You are already speaking in %s.", Color.WARNING);

    private final String string;
    private final Color color;

    Message(String string, Color color) {
        this.string = string;
        this.color = color;
    }

    @Override
    public String getRaw() {
        return "<" + color + ">" + string + "</" + color + ">";
    }
}