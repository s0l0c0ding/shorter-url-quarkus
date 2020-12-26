package dev.solocoding.repository.impl;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import dev.solocoding.entity.Ip;
import dev.solocoding.repository.IpRepository;

@ApplicationScoped
public class IpRepositoryImpl implements IpRepository {

    @Override
    public Optional<Ip> findByIpNumber(Long ipNumber) {
        return find("{$and: [{'ipfrom':{'$lte':?1}},{'ipto':{'$gte':?1}}]}", ipNumber).firstResultOptional();
    }

 
}
