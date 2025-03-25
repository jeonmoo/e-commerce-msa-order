package com.ecommerce.order.external.product.client;

import com.ecommerce.order.external.product.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "ExchangeRateOpenFeign", url = "${exchange.currency.api.uri}")
public interface ProductClient {

    @GetMapping("/product/{id}")
    ProductResponse getProduct(Long id);
    List<ProductResponse> getProductInIds(List<Long> ids);

}
