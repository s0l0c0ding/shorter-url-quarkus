package dev.usercoding.components.ip.repository;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;

import dev.usercoding.components.ip.entity.Ip;

@ApplicationScoped
public class IpRepositoryImpl implements IpRepository {

    @Override
    public Optional<Ip> findByIpNumber(Long ipNumber) {
        return find("{$and: [{'ipfrom':{'$lte':?1}},{'ipto':{'$gte':?1}}]}", ipNumber).firstResultOptional();
    }

 
}
