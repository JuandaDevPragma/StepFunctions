package com.juanojedadev.pragma.editlambda.body.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseWrapper <T>{
    private T data;
    private String message;
}
