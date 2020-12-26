package dev.usercoding.service;

import dev.usercoding.dto.IpDto;

public interface IpService {
    
    IpDto getByIp (String ip);
}
