package dev.solocoding.components.ip.dto;

import dev.solocoding.components.ip.entity.Ip;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IpDto {
  
    private String id;
    private Long ipFrom;
    private Long ipTo;
    private String countryCode;
    private String countryName;

    public IpDto(Ip entity) {
        id = entity.getId().toHexString();
        ipFrom = entity.getIpFrom();
        ipTo = entity.getIpTo();
        countryCode = entity.getCountryCode();
        countryName = entity.getCountryName();
    }
}
