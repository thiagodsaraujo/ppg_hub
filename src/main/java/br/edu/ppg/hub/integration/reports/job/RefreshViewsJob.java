package br.edu.ppg.hub.integration.reports.job;

import br.edu.ppg.hub.integration.reports.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Job agendado para atualização automática das views materializadas.
 * <p>
 * Executa refresh concorrente de todas as views materializadas de relatórios
 * diariamente às 01:00 AM. Isso garante que os dados do dashboard estejam sempre
 * atualizados sem impactar a performance durante o horário de uso.
 * </p>
 * <p>
 * Configuração:
 * - Cron: "0 0 1 * * *" - Todo dia às 01:00
 * - Zona: Horário do servidor
 * - Tipo: Refresh concorrente (não bloqueia leituras)
 * </p>
 *
 * @author PPG Hub
 * @since 1.0 - FASE 4 Sprint 4.2
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RefreshViewsJob {

    private final ReportService reportService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Executa refresh de todas as views materializadas.
     * <p>
     * Agendamento: Todo dia às 01:00 AM
     * </p>
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void refreshMaterializedViews() {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        log.info("========================================");
        log.info("Iniciando job de atualização de views materializadas");
        log.info("Data/Hora: {}", timestamp);
        log.info("========================================");

        long startTime = System.currentTimeMillis();

        try {
            // Executar refresh das views
            reportService.refreshMaterializedViews();

            long duration = System.currentTimeMillis() - startTime;
            log.info("Job concluído com sucesso!");
            log.info("Tempo de execução: {} ms ({} segundos)", duration, duration / 1000.0);
            log.info("========================================");

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("========================================");
            log.error("ERRO no job de atualização de views!");
            log.error("Tempo até falha: {} ms", duration);
            log.error("Mensagem: {}", e.getMessage());
            log.error("Stacktrace:", e);
            log.error("========================================");

            // Aqui você pode adicionar notificação por e-mail, Slack, etc.
            notifyError(e);
        }
    }

    /**
     * Executa refresh manual das views (pode ser chamado via endpoint).
     * <p>
     * Útil para testes ou atualização sob demanda.
     * </p>
     */
    public void executeManualRefresh() {
        log.info("Executando refresh manual das views materializadas");
        refreshMaterializedViews();
    }

    /**
     * Notifica erro no refresh das views.
     * <p>
     * Pode ser implementado para enviar e-mail, Slack, etc.
     * Por enquanto, apenas loga o erro.
     * </p>
     *
     * @param exception Exceção que ocorreu
     */
    private void notifyError(Exception exception) {
        // TODO: Implementar notificação (e-mail, Slack, etc)
        log.warn("Sistema de notificação não implementado. Erro apenas logado.");
        log.warn("Para implementar: integrar com servidor SMTP ou Slack webhook");
    }

    /**
     * Método auxiliar para teste do job.
     * <p>
     * Pode ser usado em testes de integração ou via endpoint REST de teste.
     * </p>
     */
    public void testJob() {
        log.info("Testando job de refresh de views...");
        try {
            reportService.refreshMaterializedViews();
            log.info("Teste do job executado com sucesso!");
        } catch (Exception e) {
            log.error("Erro no teste do job: {}", e.getMessage(), e);
            throw new RuntimeException("Falha no teste do job", e);
        }
    }
}
