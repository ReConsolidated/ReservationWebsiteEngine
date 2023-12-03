package io.github.reconsolidated.zpibackend.domain.item.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(oneOf = {UpdateItemFailure.class, UpdateItemSuccess.class})
public interface UpdateItemResponse {

    int getHttpResponseCode();
}
