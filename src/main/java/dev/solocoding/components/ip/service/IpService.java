package dev.solocoding.components.ip.service;

import dev.solocoding.components.ip.dto.IpDto;

public interface IpService {
    
    IpDto getByIp (String ip);
}
