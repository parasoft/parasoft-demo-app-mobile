package com.parasoft.demoapp.retrofitConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.annotation.NonNull;

import com.parasoft.demoapp.retrofitConfig.response.ResultResponse;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PDAServiceTest {

    @Before
    public void setUp() throws Exception {
        // Reset the value of static variables to default
        Field baseUrlField = PDAService.class.getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(null, null);

        Field authTokenField = PDAService.class.getDeclaredField("authToken");
        authTokenField.setAccessible(true);
        authTokenField.set(null, null);

        Field retrofitField = PDAService.class.getDeclaredField("retrofit");
        retrofitField.setAccessible(true);
        retrofitField.set(null, null);

        Field propertiesChangedField = PDAService.class.getDeclaredField("propertiesChanged");
        propertiesChangedField.setAccessible(true);
        propertiesChangedField.set(null, false);
    }

    @Test
    public void getClient_noPropertiesAreSet() {
        // Given
        boolean hasNullPointerException = false;

        try {
            // When
            new PDAService().getClient(ApiInterface.class);
        } catch (NullPointerException e) {
            hasNullPointerException = true;
            assertEquals("baseUrl == null", e.getMessage());
        } finally {
            // Then
            assertTrue(hasNullPointerException);
        }

        // Then
        assertFalse(PDAService.isPropertiesChanged());
        assertNull(PDAService.getAuthToken());
        assertNull(PDAService.getBaseUrl());
        assertNull(PDAService.getRetrofit());
    }

    @Test
    public void getClient_baseUrlIsSet() {
        // Given
        String baseUrl = "http://localhost1:8888";
        PDAService.setBaseUrl(baseUrl);
        assertNull(PDAService.getRetrofit());

        // When
        new PDAService().getClient(ApiInterface.class);

        // Then
        assertTrue(PDAService.isPropertiesChanged());
        assertNotNull(PDAService.getRetrofit());
        assertNull(PDAService.getAuthToken());
        assertEquals(baseUrl, PDAService.getBaseUrl());
    }

    @Test
    public void getClient_baseUrlIsSet_invalid() {
        // Given
        String baseUrl = "http://";
        PDAService.setBaseUrl(baseUrl);
        assertNull(PDAService.getRetrofit());
        boolean hasIllegalArgumentException = false;

        try {
            // When
            new PDAService().getClient(ApiInterface.class);
        } catch (IllegalArgumentException e) {
            hasIllegalArgumentException = true;
            assertEquals("Invalid URL host: \"\"", e.getMessage());
        } finally {
            // Then
            assertTrue(hasIllegalArgumentException);
        }

        // Then
        assertTrue(PDAService.isPropertiesChanged());
        assertNull(PDAService.getAuthToken());
        assertNull(PDAService.getRetrofit());
        assertEquals(baseUrl, PDAService.getBaseUrl());
    }

    @Test
    public void getClient_authTokenAndBaseUrlAreSet() {
        // Given
        String authToken = "fds4ds1jidshfu7eww";
        PDAService.setAuthToken(authToken);
        String baseUrl = "http://localhost:8081";
        PDAService.setBaseUrl(baseUrl);
        assertNull(PDAService.getRetrofit());

        // When
        new PDAService().getClient(ApiInterface.class);

        // Then
        assertTrue(PDAService.isPropertiesChanged());
        assertNotNull(PDAService.getRetrofit());
        assertEquals(authToken, PDAService.getAuthToken());
        assertEquals(baseUrl, PDAService.getBaseUrl());
    }

    boolean requestFinished = false;
    @Test
    public void getClient_rendARequest() throws InterruptedException {
        // Given
        String authToken = "fds4ds1jidshfu7eww";
        PDAService.setAuthToken(authToken);
        String baseUrl = "http://localhost1:8888";
        PDAService.setBaseUrl(baseUrl);
        assertNull(PDAService.getRetrofit());
        System.setProperty("http.agent", "Dalvik/2.1.0 (Linux; U; Android 13; sdk_gphone64_x86_64 Build/TPB4.220624.004)");
        // When
        ApiInterface api = new PDAService().getClient(ApiInterface.class);
        Call<ResultResponse<Void>> call = api.login("approver", "password");
        call.enqueue(new Callback<ResultResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ResultResponse<Void>> call, @NonNull Response<ResultResponse<Void>> response) {
                throw new RuntimeException(baseUrl + " can be connected, it is not expected.");
            }

            @Override
            public void onFailure(@NonNull Call<ResultResponse<Void>> call, @NonNull Throwable t) {
                try {
                    assertEquals("POST", call.request().method());
                    assertEquals("http://localhost1:8888/login", call.request().url().toString());
                    assertEquals("application/x-www-form-urlencoded", call.request().body().contentType().toString());
                    // TODO: Research for assert "Authorization" and "User-Agent" header value. Can not get the headers since
                    //       call.request().headers() returns empty.
                } finally {
                    requestFinished = true;
                }
            }
        });

        // Then
        assertTrue(PDAService.isPropertiesChanged());
        assertNotNull(PDAService.getRetrofit());

        // To wait for request finish.
        while(!requestFinished) {
            Thread.sleep(1000);
        }
    }

    @Test
    public void setBaseUrl() {
        // Given
        String baseUrl = "http://localhost1:8888";

        // When
        PDAService.setBaseUrl(baseUrl);

        // Then
        assertEquals(baseUrl, PDAService.getBaseUrl());
        assertTrue(PDAService.isPropertiesChanged());
    }

    @Test
    public void setAuthToken() {
        // Given
        String authToken = "fds4ds1jidshfu7eww";

        // When
        PDAService.setAuthToken(authToken);

        // Then
        assertEquals(authToken, PDAService.getAuthToken());
        assertTrue(PDAService.isPropertiesChanged());
    }
}