package br.edu.ppg.hub.integration.openalex.job;

import br.edu.ppg.hub.integration.openalex.service.OpenAlexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Job agendado para sincronização automática com OpenAlex.
 *
 * Executa a sincronização de métricas de todos os docentes
 * semanalmente às segundas-feiras às 02:00.
 *
 * @author PPG Hub
 * @since 1.0
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OpenAlexSyncJob {

    private final OpenAlexService openAlexService;

    /**
     * Sincroniza métricas de todos os docentes.
     *
     * Agendamento:
     * - Executa toda segunda-feira às 02:00 da manhã
     * - Cron: 0 0 2 * * MON
     *   - Segundo: 0
     *   - Minuto: 0
     *   - Hora: 2
     *   - Dia do mês: * (qualquer)
     *   - Mês: * (qualquer)
     *   - Dia da semana: MON (segunda-feira)
     *
     * O horário foi escolhido para:
     * - Evitar sobrecarga durante horário comercial
     * - Permitir que o sistema processe sem impactar usuários
     * - Dar tempo para processar todos os docentes
     */
    @Scheduled(cron = "0 0 2 * * MON")
    public void syncAllDocentes() {
        log.info("========================================");
        log.info("Iniciando sincronização semanal com OpenAlex");
        log.info("========================================");

        try {
            long startTime = System.currentTimeMillis();

            openAlexService.syncAllDocentesMetrics();

            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime) / 1000; // em segundos

            log.info("========================================");
            log.info("Sincronização semanal concluída com sucesso");
            log.info("Tempo total: {} segundos", duration);
            log.info("========================================");

        } catch (Exception e) {
            log.error("========================================");
            log.error("ERRO na sincronização semanal: {}", e.getMessage(), e);
            log.error("========================================");
        }
    }

    /**
     * Job de teste que executa a cada 1 hora (comentado por padrão).
     *
     * Descomente este método e comente o método syncAllDocentes() acima
     * se quiser testar o job com execuções mais frequentes.
     */
    // @Scheduled(cron = "0 0 * * * *")
    // public void syncAllDocentesHourly() {
    //     log.info("Executando sincronização horária (TESTE)");
    //     syncAllDocentes();
    // }
}
