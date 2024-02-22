package com.bistro.module.timeout;

import com.bistro.module.task.PaymentTimeoutTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class TimeOutTest {

    @Autowired
    PaymentTimeoutTask paymentTimeoutTask;

    @Test
    public void test() {
        paymentTimeoutTask.process(10000, 10000, 3, 3);
    }
}
