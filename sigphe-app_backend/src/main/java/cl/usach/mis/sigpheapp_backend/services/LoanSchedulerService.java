package cl.usach.mis.sigpheapp_backend.services;

import cl.usach.mis.sigpheapp_backend.entities.LoanEntity;
import cl.usach.mis.sigpheapp_backend.entities.LoanStatusEntity;
import cl.usach.mis.sigpheapp_backend.entities.UserEntity;
import cl.usach.mis.sigpheapp_backend.entities.UserStatusEntity;
import cl.usach.mis.sigpheapp_backend.repositories.LoanRepository;
import cl.usach.mis.sigpheapp_backend.repositories.LoanStatusRepository;
import cl.usach.mis.sigpheapp_backend.repositories.UserRepository;
import cl.usach.mis.sigpheapp_backend.repositories.UserStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio que ejecuta tareas programadas relacionadas con préstamos.
 */
@Service
public class LoanSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(LoanSchedulerService.class);

    // Constantes de estados
    private static final String STATUS_LOAN_ACTIVE = "Vigente";
    private static final String STATUS_LOAN_OVERDUE = "Atrasada";
    private static final String STATUS_USER_WITH_DEBT = "Con Deuda";

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanStatusRepository loanStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    /**
     * Tarea programada que se ejecuta cada 5 minutos para actualizar el estado de préstamos atrasados.
     *
     * Busca todos los préstamos con estado "Vigente" cuya fecha de vencimiento ya haya pasado
     * y los actualiza a estado "Atrasada". También actualiza el estado del cliente a "Con Deuda".
     */
    @Scheduled(cron = "0 */5 * * * *") // Ejecutar cada 5 minutos
    @Transactional
    public void updateOverdueLoans() {
        logger.info("Iniciando tarea programada: actualización de préstamos atrasados");

        try {
            // Obtener el estado "Vigente" y "Atrasada"
            LoanStatusEntity activeStatus = loanStatusRepository.findByName(STATUS_LOAN_ACTIVE)
                    .orElseThrow(() -> new RuntimeException("Estado de préstamo 'Vigente' no encontrado"));

            LoanStatusEntity overdueStatus = loanStatusRepository.findByName(STATUS_LOAN_OVERDUE)
                    .orElseThrow(() -> new RuntimeException("Estado de préstamo 'Atrasada' no encontrado"));

            UserStatusEntity userWithDebtStatus = userStatusRepository.findByName(STATUS_USER_WITH_DEBT)
                    .orElseThrow(() -> new RuntimeException("Estado de usuario 'Con Deuda' no encontrado"));

            // Buscar préstamos vigentes cuya fecha de vencimiento ya pasó
            LocalDateTime now = LocalDateTime.now();
            List<LoanEntity> overdueLoans = loanRepository.findAllByLoanStatusAndDueDateBefore(
                    activeStatus, now);

            if (overdueLoans.isEmpty()) {
                logger.info("No se encontraron préstamos atrasados");
                return;
            }

            int updatedCount = 0;
            for (LoanEntity loan : overdueLoans) {
                // Actualizar estado del préstamo
                loan.setLoanStatus(overdueStatus);
                loanRepository.save(loan);

                // Actualizar estado del cliente a "Con Deuda"
                UserEntity customer = loan.getCustomerUser();
                if (!customer.getUserStatus().getName().equals(STATUS_USER_WITH_DEBT)) {
                    customer.setUserStatus(userWithDebtStatus);
                    userRepository.save(customer);
                    logger.debug("Cliente {} actualizado a estado 'Con Deuda'", customer.getName());
                }

                updatedCount++;
                logger.debug("Préstamo ID {} actualizado a estado 'Atrasada'", loan.getId());
            }

            logger.info("Tarea completada: {} préstamos actualizados a estado 'Atrasada'", updatedCount);

        } catch (Exception e) {
            logger.error("Error al ejecutar tarea de actualización de préstamos atrasados", e);
        }
    }
}

