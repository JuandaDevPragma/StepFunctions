package com.juanojedadev.pragma.createlambda.body.handler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juanojedadev.pragma.createlambda.body.config.AppConfiguration;
import com.juanojedadev.pragma.createlambda.body.domain.model.User;
import com.juanojedadev.pragma.createlambda.body.dto.ResponseWrapper;
import com.juanojedadev.pragma.createlambda.body.dto.UserDto;
import com.juanojedadev.pragma.createlambda.body.entity.UserEntity;
import com.juanojedadev.pragma.createlambda.body.mapper.IGlobalMapper;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
public class AwsCreateLambdaHandler implements RequestHandler<UserDto, ResponseWrapper<String>> {

    private static final IGlobalMapper globalMapper = IGlobalMapper.INSTANCE;

    private static final ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfiguration.class);

    private final DynamoDbTable<UserEntity> personTable;

    private final EventBridgeClient eventBridgeClient;

    public AwsCreateLambdaHandler() {
        this.eventBridgeClient = applicationContext.getBean(EventBridgeClient.class);
        personTable = applicationContext.getBean(DynamoDbEnhancedClient.class)
                .table("users", TableSchema.fromBean(UserEntity.class));
    }

    @Override
    public ResponseWrapper<String> handleRequest(UserDto userDto, Context context) {
        log.info("Creating user {}", userDto);
        User user = createUser(globalMapper.userDtoToUserDomain(userDto));
        if(Boolean.TRUE.equals(user.active())){
            ResponseWrapper<User> responseWrapper = new
                    ResponseWrapper<>(user, String.format("User %s saved correctly", user.name()));
            setResult(responseWrapper);
        }
        return new ResponseWrapper<>(user.id(), String.format("User %s saved correctly", user.name()));
    }

    private User createUser(User user) {
        UserEntity userEntity = globalMapper.userDomainToUserEntity(user);
        userEntity.setId(UUID.randomUUID().toString());
        personTable.putItem(userEntity);
        return globalMapper.userEntityToUserDomain(userEntity);
    }

    public void setResult(ResponseWrapper<User> result) {
        String data = Try.of(() -> new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(result))
                .get();

        PutEventsRequestEntry entry = PutEventsRequestEntry.builder()
                .source("Lambda Publish")
                .time(Instant.now())
                .detailType(result.getData().id())
                .detail(data)
                .eventBusName("arn:aws:events:us-east-2:302263088688:event-bus/UpdateCustomEventBus")
                .traceHeader("demo")
                .build();

        PutEventsRequest eventsRequest = PutEventsRequest.builder()
                .entries(entry)
                .build();

        eventBridgeClient.putEvents(eventsRequest);
    }
}
