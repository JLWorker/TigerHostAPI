package tgc.plus.authservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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

    public Mono<TokenMeta> save(DeviceData deviceData, Long tokenId, String ipAddr){
        return tokenMetaRepository.save(new TokenMeta(tokenId, ipAddr, deviceData.getName(), deviceData.getApplicationType(),
                Instant.now()));
    }

}
