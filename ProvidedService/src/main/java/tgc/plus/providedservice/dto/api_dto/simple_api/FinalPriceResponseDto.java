package tgc.plus.providedservice.dto.api_dto.simple_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class FinalPriceResponseDto {

    @JsonProperty("cost_kop")
    @Schema(example = "6148.40")
    private String priceKop;

    @JsonProperty("hash_cost_kop")
    @Schema(example = "d2eda7b666d9a27b53cba.....")
    private String hashPriceKop;

    @JsonProperty("hash_price_month_kop")
    @Schema(example = "d2eda7b666d9a27b53cba.....")
    private String hashPriceMonthKop;

    public FinalPriceResponseDto(String priceKop, String hashPriceKop, String hashPriceMonthKop) {
        this.priceKop = priceKop;
        this.hashPriceKop = hashPriceKop;
        this.hashPriceMonthKop = hashPriceMonthKop;
    }
}
