package dev.solocoding.components.ip.repository;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import dev.solocoding.components.ip.entity.Ip;

@ApplicationScoped
public class IpRepositoryImpl implements IpRepository {

    @Override
    public Optional<Ip> findByIpNumber(Long ipNumber) {
        return find("{$and: [{'ipfrom':{'$lte':?1}},{'ipto':{'$gte':?1}}]}", ipNumber).firstResultOptional();
    }

 
}
