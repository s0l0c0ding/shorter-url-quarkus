package dev.solocoding.entity;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Ip {
    
    private ObjectId id;
    @BsonProperty("ipfrom")
    private Long ipFrom;
    @BsonProperty("ipto")
    private Long ipTo;
    @BsonProperty("countrycode")
    private String countryCode;
    @BsonProperty("countryname")
    private String countryName;
}
