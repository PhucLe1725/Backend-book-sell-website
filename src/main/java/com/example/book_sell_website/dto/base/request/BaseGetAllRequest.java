package com.example.book_sell_website.dto.base.request;

import lombok.*;

@Getter
@Setter
public abstract class BaseGetAllRequest {
    protected Integer skipCount = 0;
    protected Integer maxResultCount = 10;
}
