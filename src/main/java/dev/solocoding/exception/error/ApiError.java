package dev.solocoding.exception.error;


import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiError implements Serializable {
    private int code;
    private String name;
    private String description;
}
