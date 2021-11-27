package dev.solocoding.components.ip.repository;

import java.util.Optional;

import dev.solocoding.components.ip.entity.Ip;
import io.quarkus.mongodb.panache.PanacheMongoRepository;

public interface IpRepository extends PanacheMongoRepository<Ip> {
    
    Optional<Ip> findByIpNumber(Long ipNumber);
}
