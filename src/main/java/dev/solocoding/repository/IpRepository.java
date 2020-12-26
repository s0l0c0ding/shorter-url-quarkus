package dev.solocoding.repository;

import java.util.Optional;

import dev.solocoding.entity.Ip;
import io.quarkus.mongodb.panache.PanacheMongoRepository;

public interface IpRepository extends PanacheMongoRepository<Ip> {
    
    Optional<Ip> findByIpNumber(Long ipNumber);
}
