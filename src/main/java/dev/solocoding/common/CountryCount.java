package dev.solocoding.common;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CountryCount", description = "Redirect count for one country")
public class CountryCount {
    
    @Schema(description = "Country code", type = SchemaType.STRING, example = "DE")
    private String country;
    @Schema(description = "Number of redirects from the country", type = SchemaType.INTEGER, format = "int32", example = "1")
    private Integer count;
}
