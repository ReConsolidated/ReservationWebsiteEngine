package io.github.reconsolidated.zpibackend.domain.item.response;

import io.github.reconsolidated.zpibackend.domain.item.dtos.ItemDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(allOf = UpdateItemResponse.class)
public class UpdateItemSuccess implements UpdateItemResponse {

    @JsonIgnore
    private static final int HTTP_RESPONSE_CODE = 200;
    private ItemDto item;

    @Override
    public int getHttpResponseCode() {
        return HTTP_RESPONSE_CODE;
    }
}
