package dev.solocoding.service;

import dev.solocoding.dto.IpDto;

public interface IpService {
    
    IpDto getByIp (String ip);
}
