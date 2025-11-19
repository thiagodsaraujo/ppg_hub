package br.edu.ppg.hub.shared.aspect;

import br.edu.ppg.hub.auth.application.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Aspect para auditoria automática de operações CRUD.
 *
 * Intercepta métodos de Service e registra automaticamente no log de auditoria.
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

    private final AuditService auditService;

    /**
     * Pointcut para métodos create() em Services.
     */
    @Pointcut("execution(* br.edu.ppg.hub..*.application.service.*.create(..)) && !within(br.edu.ppg.hub.auth.application.service.AuditService)")
    public void createMethods() {}

    /**
     * Pointcut para métodos update() em Services.
     */
    @Pointcut("execution(* br.edu.ppg.hub..*.application.service.*.update(..)) && !within(br.edu.ppg.hub.auth.application.service.AuditService)")
    public void updateMethods() {}

    /**
     * Pointcut para métodos delete() em Services.
     */
    @Pointcut("execution(* br.edu.ppg.hub..*.application.service.*.delete(..)) && !within(br.edu.ppg.hub.auth.application.service.AuditService)")
    public void deleteMethods() {}

    /**
     * Audita operações de criação.
     */
    @AfterReturning(pointcut = "createMethods()", returning = "result")
    public void auditCreate(JoinPoint joinPoint, Object result) {
        try {
            String className = getSimpleClassName(joinPoint);
            String entityName = getEntityNameFromService(className);

            // Extrair ID do objeto retornado (se tiver)
            Long entityId = extractId(result);

            auditService.registrarCriacao(entityName, entityId, result);
            log.debug("Auditado CREATE: {} #{}", entityName, entityId);

        } catch (Exception e) {
            log.error("Erro ao auditar criação", e);
        }
    }

    /**
     * Audita operações de atualização.
     */
    @AfterReturning(pointcut = "updateMethods()", returning = "result")
    public void auditUpdate(JoinPoint joinPoint, Object result) {
        try {
            String className = getSimpleClassName(joinPoint);
            String entityName = getEntityNameFromService(className);

            // Primeiro argumento geralmente é o ID
            Object[] args = joinPoint.getArgs();
            Long entityId = args.length > 0 && args[0] instanceof Long ? (Long) args[0] : extractId(result);

            auditService.registrarAtualizacao(entityName, entityId, null, result);
            log.debug("Auditado UPDATE: {} #{}", entityName, entityId);

        } catch (Exception e) {
            log.error("Erro ao auditar atualização", e);
        }
    }

    /**
     * Audita operações de exclusão.
     */
    @AfterReturning(pointcut = "deleteMethods()")
    public void auditDelete(JoinPoint joinPoint) {
        try {
            String className = getSimpleClassName(joinPoint);
            String entityName = getEntityNameFromService(className);

            // Primeiro argumento geralmente é o ID
            Object[] args = joinPoint.getArgs();
            Long entityId = args.length > 0 && args[0] instanceof Long ? (Long) args[0] : null;

            auditService.registrarExclusao(entityName, entityId, null);
            log.debug("Auditado DELETE: {} #{}", entityName, entityId);

        } catch (Exception e) {
            log.error("Erro ao auditar exclusão", e);
        }
    }

    /**
     * Obtém nome simples da classe.
     */
    private String getSimpleClassName(JoinPoint joinPoint) {
        return joinPoint.getTarget().getClass().getSimpleName();
    }

    /**
     * Extrai nome da entidade do nome do Service.
     * Exemplo: "ProgramaService" -> "Programa"
     */
    private String getEntityNameFromService(String serviceName) {
        if (serviceName.endsWith("Service")) {
            return serviceName.replace("Service", "");
        }
        return serviceName;
    }

    /**
     * Tenta extrair ID de um objeto (busca método getId()).
     */
    private Long extractId(Object obj) {
        try {
            if (obj == null) {
                return null;
            }
            // Tenta chamar getId() usando reflection
            var method = obj.getClass().getMethod("getId");
            Object id = method.invoke(obj);
            if (id instanceof Long) {
                return (Long) id;
            }
        } catch (Exception e) {
            // Ignora se não conseguir extrair ID
        }
        return null;
    }
}
