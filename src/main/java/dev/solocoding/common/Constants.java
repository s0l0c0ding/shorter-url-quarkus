package dev.solocoding.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
    public static final String REDIRECT_COUNTER = "redirectCounter";
    public static final String FORWARDED_HEADER = "x-forwarded-for";
    
}
