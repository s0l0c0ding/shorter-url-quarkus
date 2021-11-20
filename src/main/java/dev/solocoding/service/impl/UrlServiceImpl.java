package dev.solocoding.service.impl;

import static dev.solocoding.common.Constants.REDIRECT_COUNTER;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.enterprise.context.ApplicationScoped;

import org.bson.types.ObjectId;

import dev.solocoding.common.CountryCount;
import dev.solocoding.dto.IpDto;
import dev.solocoding.dto.UrlDto;
import dev.solocoding.entity.Url;
import dev.solocoding.exception.BadRequestEnum;
import dev.solocoding.exception.BadRequestException;
import dev.solocoding.exception.UrlNotFoundException;
import dev.solocoding.repository.UrlRepository;
import dev.solocoding.service.IpService;
import dev.solocoding.service.RequestDetails;
import dev.solocoding.service.UrlService;
import dev.solocoding.service.UrlValidation;
import io.quarkus.panache.common.Page;
import io.quarkus.vertx.ConsumeEvent;
import io.vertx.mutiny.core.eventbus.EventBus;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService, UrlValidation {

    private final UrlRepository repo;
    private final IpService ipService;
    private final RequestDetails requestDetails;
    private final EventBus bus;

    @Override
    public UrlDto getUrlByShortUrl(String shortUrl) {
        Url url = checkExistence(shortUrl);
        checkExpireDateAndDeleteIfOneMonth(url);
        return new UrlDto(url);
    }

    @Override
    public UrlDto saveUrl(UrlDto dto) {
        isValidUrl(dto);
        var entity = new Url(dto);
        ObjectId id = new ObjectId();
        entity.setId(id);
        entity.setShortUrl(id.toHexString().substring(18, 24));
        repo.persist(entity);
        return new UrlDto(entity);
    }

    @Override
    public void deleteUrlById(String id) {
        if (ObjectId.isValid(id)) {
            var url = Optional.ofNullable(repo.findById(new ObjectId(id))).orElseThrow(UrlNotFoundException::new);
            repo.deleteById(url.getId());
        }
    }

    @Override
    public UrlDto getAndRedirect(String shortUrl) {
        Url url = checkExistence(shortUrl);
        checkExpireDateAndDeleteIfOneMonth(url);
        bus.publish(REDIRECT_COUNTER, url);
        return new UrlDto(url);
    }
    
    @ConsumeEvent(value = REDIRECT_COUNTER)
    public void updateCounter(Url url) {
        IpDto ipDto = ipService.getByIp(requestDetails.getRemoteAddress());
        List <CountryCount> countryCountList = updateCountryCountList(url, ipDto);
        url.setCountryCountList(countryCountList);
        url.setCount(url.getCount() + 1);
        repo.update(url);
    }
    
    @Override
    public UrlDto updateUrlByShortId(String shortUrl, UrlDto dto) {
        isValidUrl(dto);
        var entity = checkExistence(shortUrl);
        checkExpireDateAndDeleteIfOneMonth(entity);
        entity.setFullUrl(dto.getFullUrl());
        repo.update(entity);
        return new UrlDto(entity);
    }
    
    @Override
    public List<UrlDto> getAll(int index, int size) {
        List<Url> urls  = repo.findAll().page(Page.of(index, size)).list();
        return urls.stream().map(UrlDto::new).collect(Collectors.toList());
    }

    private Url checkExistence(String shortUrl) {
        return repo.findByShortUrl(shortUrl).orElseThrow((UrlNotFoundException::new));
    }

    private List<CountryCount> updateCountryCountList(Url url, IpDto ipDto){
        final List<CountryCount> outList = new LinkedList<>();

        if(Objects.nonNull(url.getCountryCountList()) && ! url.getCountryCountList().isEmpty()){
            outList.addAll(url.getCountryCountList());
        }

        if(Objects.nonNull(ipDto.getId())) {
           IntStream.range(0, outList.size()).filter(i -> outList.get(i).getCountry().contentEquals(ipDto.getCountryCode()))
                                            .findFirst()
                                            .ifPresentOrElse(i -> {
                                                final CountryCount countryUpdate = outList.get(i);
                                                countryUpdate.setCount(countryUpdate.getCount() + 1);
                                                outList.set(i, countryUpdate);
                                            },
                                            ()-> outList.add(new CountryCount(ipDto.getCountryCode(), 1)));
        }

        return outList;
    }

    private void checkExpireDateAndDeleteIfOneMonth(Url url) {
        if(isEXpireTimeAfterDays(url.getExpireTime(), 60l)) {
            repo.delete(url);
            throw new BadRequestException(BadRequestEnum.EXPIRED_DELETED);
        }
        if(isEXpireTimeAfterDays(url.getExpireTime(), 1l)) throw new BadRequestException(BadRequestEnum.EXPIRED_URL.getDescription().concat(url.getExpireTime().toString()));
    }

    @Override
    public UrlDto extendExpiretion(String shortUrl, long days) {
        var url  = checkExistence(shortUrl);
        url.setExpireTime(ZonedDateTime.now(ZoneOffset.UTC).plusDays(days));
        repo.update(url);
        return new UrlDto(url);
    }
}