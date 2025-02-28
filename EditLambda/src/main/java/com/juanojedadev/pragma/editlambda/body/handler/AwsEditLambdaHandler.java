package com.juanojedadev.pragma.editlambda.body.handler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.juanojedadev.pragma.editlambda.body.config.AppConfiguration;
import com.juanojedadev.pragma.editlambda.body.domain.User;
import com.juanojedadev.pragma.editlambda.body.dto.ResponseWrapper;
import com.juanojedadev.pragma.editlambda.body.entity.UserEntity;
import com.juanojedadev.pragma.editlambda.body.mapper.IGlobalMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Slf4j
@Component
public class AwsEditLambdaHandler implements RequestHandler<ResponseWrapper<String>, ResponseWrapper<String>> {

    private static final IGlobalMapper globalMapper = IGlobalMapper.INSTANCE;

    private static final ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfiguration.class);

    private final DynamoDbTable<UserEntity> personTable;

    public AwsEditLambdaHandler() {
        personTable = applicationContext.getBean(DynamoDbEnhancedClient.class)
                .table("users", TableSchema.fromBean(UserEntity.class));
    }

    private User updateUser(String user) {
        UserEntity ue = personTable.getItem(Key.builder().partitionValue(user).build());
        ue.setActive(!ue.getActive());
        personTable.updateItem(ue);
        return globalMapper.userEntityToUserDomain(ue);
    }

    @Override
    public ResponseWrapper<String> handleRequest(ResponseWrapper<String> stringResponseWrapper, Context context) {
        log.info("Updating user with id{}", stringResponseWrapper.getData());
        User user = updateUser(stringResponseWrapper.getData());
        return new ResponseWrapper<>(user.id(), String.format("User %s saved correctly", user.name()));
    }
}
