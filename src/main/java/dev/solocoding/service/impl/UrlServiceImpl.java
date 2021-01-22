package dev.solocoding.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import javax.enterprise.context.ApplicationScoped;

import org.bson.types.ObjectId;

import dev.solocoding.common.CountryCount;
import dev.solocoding.dto.IpDto;
import dev.solocoding.dto.UrlDto;
import dev.solocoding.entity.Url;
import dev.solocoding.exception.NotValidUrlException;
import dev.solocoding.exception.UrlNotFoundException;
import dev.solocoding.repository.UrlRepository;
import dev.solocoding.service.IpService;
import dev.solocoding.service.RequestDetails;
import dev.solocoding.service.UrlService;
import io.quarkus.vertx.ConsumeEvent;
import io.vertx.mutiny.core.eventbus.EventBus;
import lombok.RequiredArgsConstructor;

import static dev.solocoding.common.Constants.REDIRECT_COUNTER;

@ApplicationScoped
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository repo;
    private final IpService ipService;
    private final RequestDetails requestDetails;
    private final EventBus bus;

    @Override
    public UrlDto getUrlByShortUrl(String shortUrl) {
        return new UrlDto(checkExistence(shortUrl));
    }

    @Override
    public UrlDto saveUrl(UrlDto dto) {
        if (!isValidUrl(dto)) {
            throw new NotValidUrlException();
        }
        var entity = new Url(dto);
        if (Objects.isNull(entity.getId())){
            ObjectId id = new ObjectId();
            entity.setId(id);
            entity.setShortUrl(id.toHexString().substring(18, 24));
        }
        repo.persist(entity);
        return new UrlDto(entity);
    }

    @Override
    public void deleteUrlById(String id) {
        if (ObjectId.isValid(id)) {
            Optional<Url> url = Optional.ofNullable(repo.findById(new ObjectId(id)));
                if(url.isEmpty()) {
                throw new UrlNotFoundException();
                } else {
                    repo.deleteById(url.get().getId());
                }     
        }
    }

    @Override
    public UrlDto getAndRedirect(String shortUrl) {
        Url url = checkExistence(shortUrl);
        bus.sendAndForget(REDIRECT_COUNTER, url);
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

    private Url checkExistence(String shortUrl) {
        return repo.findByShortUrl(shortUrl).orElseThrow((UrlNotFoundException::new));
    }

    private boolean isValidUrl(UrlDto url) {
        String regex = "((http|https)://)(www.)?" + "[a-zA-Z0-9@:%._\\+~#?&/=]" + "{2,256}\\.[a-z]"
                + "{2,6}\\b([-a-zA-Z0-9@:%" + "._\\+~#?&/=]*)";
        return Objects.nonNull(url.getFullUrl()) && Pattern.matches(regex, url.getFullUrl());
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
                                                CountryCount countryUpdate = outList.get(i);
                                                countryUpdate.setCount(countryUpdate.getCount() + 1);
                                                outList.remove(i);
                                                outList.add(countryUpdate);
                                            }, 
                                            ()-> outList.add(new CountryCount(ipDto.getCountryCode(), 1)));
        }

        return outList;
    }
}