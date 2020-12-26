package dev.usercoding.repository;

import java.util.Optional;

import dev.usercoding.entity.Ip;
import io.quarkus.mongodb.panache.PanacheMongoRepository;

public interface IpRepository extends PanacheMongoRepository<Ip> {
    
    Optional<Ip> findByIpNumber(Long ipNumber);
}
