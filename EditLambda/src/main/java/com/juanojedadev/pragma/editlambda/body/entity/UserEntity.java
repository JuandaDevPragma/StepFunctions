package com.juanojedadev.pragma.editlambda.body.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class UserEntity {

    private String id;

    private String name;

    private Integer age;

    private Boolean active;

    @DynamoDbPartitionKey
    public String getId(){
        return id;
    }

}
