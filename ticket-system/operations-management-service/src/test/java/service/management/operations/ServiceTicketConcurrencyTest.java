package service.management.operations;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.*;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@Transactional
public class ServiceTicketConcurrencyTest {

    @Inject
    ServiceTicket serviceTicket;

    @Inject
    RepositoryTicket repositoryTicket;

    private static final int THREAD_COUNT = 10;
    private static final int INITIAL_TICKETS = 5;

    private Long ticketId; // 每个测试使用不同的 ticketId

    @BeforeEach
    public void setUp() {
        // 每次创建一个新的 ticket，由数据库自动生成 ID
        EntityTicket ticket = new EntityTicket();
        ticket.setAvailableAmount(INITIAL_TICKETS);
        ticket.setAmount(INITIAL_TICKETS);

        repositoryTicket.persist(ticket); // 插入新票
        this.ticketId = ticket.getId();   // 记录本次测试使用的 ticketId
    }

    @AfterEach
    public void tearDown() {
        if (ticketId != null) {
            repositoryTicket.deleteById(ticketId); // 清理测试数据
        }
    }

    @RepeatedTest(5)
    public void testConcurrentTicketReduction() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger successCounter = new AtomicInteger(0);

        System.out.println("=== Starting new test iteration ===");
        System.out.println("Using ticketId: " + ticketId);

        IntStream.range(0, THREAD_COUNT).forEach(i -> {
            executorService.submit(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + " is trying to buy ticket...");
                    if (serviceTicket.reduceTicketAvailability(ticketId)) {
                        successCounter.incrementAndGet();
                    }
                } catch (Exception e) {
                    System.err.println("Error in thread: " + Thread.currentThread().getName());
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        });

        latch.await();
        executorService.shutdown();

        // 增强断言：验证最多只能卖出5张票
        assertTrue(successCounter.get() <= INITIAL_TICKETS,
                   "Only " + INITIAL_TICKETS + " tickets should be sold.");

        // 增强断言：验证最终库存为0
        EntityTicket updatedTicket = repositoryTicket.findById(ticketId);
        assertNotNull(updatedTicket, "Ticket should not be null after test");
        assertEquals(0, updatedTicket.getAvailableAmount(),
                     "All tickets should be sold out, available amount should be 0");

        // 输出本次测试统计信息
        System.out.println("✅ Test iteration completed.");
        System.out.println("   Tickets sold: " + successCounter.get());
        System.out.println("   Final available amount: " + updatedTicket.getAvailableAmount());
        System.out.println("=============================");
    }
}