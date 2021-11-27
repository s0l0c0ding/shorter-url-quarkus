package dev.usercoding.components.ip.service;

import dev.usercoding.components.ip.dto.IpDto;

public interface IpService {
    
    IpDto getByIp (String ip);
}
