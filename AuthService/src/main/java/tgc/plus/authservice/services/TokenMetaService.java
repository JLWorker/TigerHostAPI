package tgc.plus.authservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.user_dto.DeviceData;
import tgc.plus.authservice.entity.TokenMeta;
import tgc.plus.authservice.repository.TokenMetaRepository;

import java.time.Instant;
import java.util.Date;

@Service
public class TokenMetaService {

    @Autowired
    TokenMetaRepository tokenMetaRepository;

    public Mono<TokenMeta> saveDeviceData(DeviceData deviceData, Long tokenId, String ipAddr){
        return tokenMetaRepository.save(new TokenMeta(tokenId, ipAddr, deviceData.getName(), deviceData.getApplicationType(),
                Date.from(Instant.now())));
    }

}
