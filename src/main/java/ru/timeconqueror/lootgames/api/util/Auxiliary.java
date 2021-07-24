package ru.timeconqueror.lootgames.api.util;

import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessage;
//TODO remove, because it's now in TimeCore
public class Auxiliary {
    public static Message makeLogMessage(String message, Object... arguments) {
        return new ParameterizedMessage(message, arguments);
    }
}
