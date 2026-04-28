package dev.solocoding.config;

import java.time.ZonedDateTime;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class ZoneDateCodecProvider implements CodecProvider {

    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (clazz == ZonedDateTime.class) {
            return (Codec<T>) new ZoneDateCodec();
        }
        return null;
    }
    
}
