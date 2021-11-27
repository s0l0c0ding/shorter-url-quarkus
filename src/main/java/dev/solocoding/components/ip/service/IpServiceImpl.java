package dev.solocoding.components.ip.service;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import dev.solocoding.components.ip.dto.IpDto;
import dev.solocoding.components.ip.entity.Ip;
import dev.solocoding.components.ip.repository.IpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class IpServiceImpl implements IpService {

    private final IpRepository ipRepo;

    @Override
    public IpDto getByIp(String ip) {
        long ipNumber = convertIpToIpNumber(ip);
        Optional<Ip> ipEntity = ipRepo.findByIpNumber(ipNumber);
        IpDto defaultDto = new IpDto();
        defaultDto.setCountryCode("N.A.");
        return ipEntity.map(IpDto::new).orElseGet(()-> defaultDto);
    }

    private long convertIpToIpNumber(String ipString) {
        long result = 0;
        try {
            long ip = 0;
            String[] ipAddressInArray = ipString.split("\\.");
            for (int x = 3; x >= 0; x--) {
                ip = Long.parseLong(ipAddressInArray[3 - x]);
                result |= ip << (x << 3);
            }
        } catch (Exception e) {
            log.error("converting ip adress: {}  cause: {}", ipString, e.getMessage());
        }

        return result;
    }

}
