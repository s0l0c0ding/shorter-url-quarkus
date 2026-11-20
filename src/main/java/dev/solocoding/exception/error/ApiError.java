package dev.solocoding.exception.error;


import java.io.Serializable;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ApiError", description = "Service error details")
public class ApiError implements Serializable {
    @Schema(description = "Service error code", type = SchemaType.INTEGER, format = "int32", required = true, example = "1")
    private int code;
    @Schema(description = "Service error name", type = SchemaType.STRING, required = true, example = "NOT_VALID_URL")
    private String name;
    @Schema(description = "Human-readable error description", type = SchemaType.STRING, required = true, example = "Url is not valid")
    private String description;
}
