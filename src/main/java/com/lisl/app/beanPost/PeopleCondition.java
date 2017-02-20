package com.lisl.app.beanPost;


import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Created by Administrator on 2017/2/20.
 */
public class PeopleCondition implements Condition {
    /*@Conditional(PeopleCondition.class)
    这句代码可以标注在类上面，表示该类下面的所有@Bean都会启用配置
    也可以标注在方法上面，只是对该方法启用配置

    除了自己自定义Condition之外，Spring还提供了很多Condition给我们用
    @ConditionalOnBean（仅仅在当前上下文中存在某个对象时，才会实例化一个Bean）
    @ConditionalOnClass（某个class位于类路径上，才会实例化一个Bean）
    @ConditionalOnExpression（当表达式为true的时候，才会实例化一个Bean）
    @ConditionalOnMissingBean（仅仅在当前上下文中不存在某个对象时，才会实例化一个Bean）
    @ConditionalOnMissingClass（某个class类路径上不存在的时候，才会实例化一个Bean）
    @ConditionalOnNotWebApplication（不是web应用）*/
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        return false;
    }
}
