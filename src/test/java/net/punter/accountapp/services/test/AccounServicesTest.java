package net.punter.accountapp.services.test;


import net.punter.accountapp.controllers.AccountsApiController;
import net.punter.accountapp.repositories.AccountRepository;
import net.punter.accountapp.services.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;


@RunWith(MockitoJUnitRunner.class)
public class AccounServicesTest {

    @InjectMocks
    AccountsApiController accountsApiController;

    @Mock
    AccountService accountService;

    @Test
    public void testGet() throws Exception {
        accountsApiController.getAccount(1L);




    }
}
