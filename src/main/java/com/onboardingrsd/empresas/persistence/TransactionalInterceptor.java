package com.onboardingrsd.empresas.persistence;

import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.transaction.Transactional;

@Interceptor
@Transactional
public class TransactionalInterceptor {

    @Inject
    private EntityManager entityManager;

    @AroundInvoke
    public Object manage(InvocationContext context) throws Exception {
        Transactional annotation = resolveAnnotation(context);
        Transactional.TxType type = annotation != null
                ? annotation.value()
                : Transactional.TxType.REQUIRED;

        EntityTransaction tx = entityManager.getTransaction();
        boolean startedHere = false;

        try {
            if (precisaIniciar(type, tx)) {
                tx.begin();
                startedHere = true;
            }

            Object result = context.proceed();

            if (startedHere && tx.isActive()) {
                tx.commit();
            }
            return result;
        } catch (Exception ex) {
            if (startedHere && tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        }
    }

    private static boolean precisaIniciar(Transactional.TxType type, EntityTransaction tx) {
        if (tx.isActive()) {
            return false;
        }
        return switch (type) {
            case REQUIRED, REQUIRES_NEW, MANDATORY, SUPPORTS -> true;
            case NOT_SUPPORTED, NEVER -> false;
        };
    }

    private static Transactional resolveAnnotation(InvocationContext context) {
        Transactional onMethod = context.getMethod().getAnnotation(Transactional.class);
        if (onMethod != null) {
            return onMethod;
        }
        return context.getMethod().getDeclaringClass().getAnnotation(Transactional.class);
    }
}
