package com.juanojedadev.pragma.createlambda.body.handler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.juanojedadev.pragma.createlambda.body.config.AppConfiguration;
import com.juanojedadev.pragma.createlambda.body.domain.model.User;
import com.juanojedadev.pragma.createlambda.body.dto.ResponseWrapper;
import com.juanojedadev.pragma.createlambda.body.dto.UserDto;
import com.juanojedadev.pragma.createlambda.body.entity.UserEntity;
import com.juanojedadev.pragma.createlambda.body.mapper.IGlobalMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import java.util.UUID;

@Slf4j
@Component
public class AwsCreateLambdaHandler implements RequestHandler<UserDto, ResponseWrapper<String>> {

    private static final IGlobalMapper globalMapper = IGlobalMapper.INSTANCE;

    private static final ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfiguration.class);

    private final DynamoDbTable<UserEntity> personTable;

    public AwsCreateLambdaHandler() {
        personTable = applicationContext.getBean(DynamoDbEnhancedClient.class)
                .table("users", TableSchema.fromBean(UserEntity.class));
    }

    @Override
    public ResponseWrapper<String> handleRequest(UserDto userDto, Context context) {
        log.info("Creating user {}", userDto);
        User user = createUser(globalMapper.userDtoToUserDomain(userDto));
        return new ResponseWrapper<>(user.id(), String.format("User %s saved correctly", user.name()));
    }

    private User createUser(User user) {
        UserEntity userEntity = globalMapper.userDomainToUserEntity(user);
        userEntity.setId(UUID.randomUUID().toString());
        personTable.putItem(userEntity);
        return globalMapper.userEntityToUserDomain(userEntity);
    }
}
