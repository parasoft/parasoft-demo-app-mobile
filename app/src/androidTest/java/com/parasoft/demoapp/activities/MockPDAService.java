package com.parasoft.demoapp.activities;

import static org.mockito.Mockito.mock;

import com.parasoft.demoapp.retrofitConfig.PDAService;

public class MockPDAService {

    protected PDAService mockedPdaService;

    {
        resetMockedPDAService();
    }

    public void resetMockedPDAService() {
        mockedPdaService = mock(PDAService.class);
        PDAService.Factory.setPdaService(mockedPdaService);
    }
}
