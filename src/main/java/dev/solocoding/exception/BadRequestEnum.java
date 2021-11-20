package dev.solocoding.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BadRequestEnum {

    NOT_VALID_URL("Url is not valid"),
    EXPIRED_URL("Url is expired, will be deleted after 30 days from expiretion: "),
    EXPIRED_DELETED("Url is expired from 60 days and has been deleted");

    private String description;

}
