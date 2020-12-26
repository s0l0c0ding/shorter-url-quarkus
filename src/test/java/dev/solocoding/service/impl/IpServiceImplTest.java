package dev.solocoding.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.solocoding.entity.Ip;
import dev.solocoding.repository.IpRepository;
import dev.solocoding.repository.impl.IpRepositoryImpl;

class IpServiceImplTest {
    
    private final static String IP_ADRESS = "72.77.138.60";

    private IpServiceImpl ipService;
    private IpRepository ipRepository;

    @BeforeEach
    void init() {
        ipRepository = mock(IpRepositoryImpl.class);
        ipService = new IpServiceImpl(ipRepository);
    }

    private Ip getIpStub() {
        Ip ip = new Ip();
        ip.setCountryCode("US");
        ip.setCountryName("UNITED STATES");
        ip.setId(new ObjectId("507f191e810c19729de860ea"));
        ip.setIpFrom(1213041208l);
        ip.setIpTo(1213041215l);
        return ip;
    }

    @Test
    void shouldGetIP() {
        when(ipRepository.findByIpNumber(1213041212l)).thenReturn(Optional.of(getIpStub()));
        assertEquals("US", ipService.getByIp(IP_ADRESS).getCountryCode());
    }
    
    @Test
    void shoulNotdGetIP() {
        when(ipRepository.findByIpNumber(1213041212l)).thenReturn(Optional.empty());
        assertEquals("N.A.", ipService.getByIp(IP_ADRESS).getCountryCode());
    }
}
