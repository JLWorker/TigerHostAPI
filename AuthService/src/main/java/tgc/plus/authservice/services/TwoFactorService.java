package tgc.plus.authservice.services;

import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.two_factor_dto.QrCodeData;

@Service
public class TwoFactorService {

    @Value("${totp.secret.length}")
    private Integer secretLength;

    @Value("${totp.code.length}")
    private Integer codeLength;
    private SecretGenerator secretGenerator;
    private QrGenerator qrGenerator;
    private CodeVerifier codeVerifier;

    @PostConstruct
    public void init(){
        secretGenerator = new DefaultSecretGenerator(secretLength);
        qrGenerator = new ZxingPngQrGenerator();
        CodeGenerator codeGenerator = new DefaultCodeGenerator(HashingAlgorithm.SHA256, codeLength);
        codeVerifier = new DefaultCodeVerifier(codeGenerator, new SystemTimeProvider());
    }

    public Mono<String> generateSecret(){
        return Mono.defer(() -> Mono.just(secretGenerator.generate()));
    }

    public Mono<QrCodeData> generateQrCode(String email, String secret){
        QrData data = new QrData.Builder()
                .label(email)
                .secret(secret)
                .issuer("TigerHost")
                .algorithm(HashingAlgorithm.SHA256)
                .digits(codeLength)
                .period(30)
                .build();

        try {
            byte[] qrCode = qrGenerator.generate(data);
            String type = qrGenerator.getImageMimeType();
            return Mono.just(new QrCodeData(qrCode, type));

        } catch (QrGenerationException e) {
            return Mono.error(e);
        }

    }
    public Mono<Boolean> verify(String code, String secret){
        return Mono.defer(()->Mono.just(codeVerifier.isValidCode(secret, code)));
    }

}
