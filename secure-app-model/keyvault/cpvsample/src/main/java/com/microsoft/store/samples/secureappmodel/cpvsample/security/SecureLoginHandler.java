// -----------------------------------------------------------------------
// <copyright file="SecureLoginHandler.java" company="Microsoft">
//      Copyright (c) Microsoft Corporation. All rights reserved.
// </copyright>
// -----------------------------------------------------------------------

package com.microsoft.store.samples.secureappmodel.cpvsample.security;

import java.net.MalformedURLException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.store.partnercenter.AuthenticationToken;
import com.microsoft.store.partnercenter.IAadLoginHandler;
import com.microsoft.store.samples.secureappmodel.cpvsample.PropertyName;

import org.joda.time.DateTime;

public class SecureLoginHandler implements IAadLoginHandler
{
    /**
     * Provides the ability to request access tokens.
     */
    private IAccessTokenProvider tokenProvider; 

    /**
     * Provides access to configuration information stored in the application.properties file.
     */
    private Properties properties; 

    /**
     * Initializes a new instance of the {@link SecureLoginHandler}
     * 
     * @param tokenProvider Provides the ability to request access tokens.
     */
    public SecureLoginHandler(Properties properties, IAccessTokenProvider tokenProvider)
    {
        if(properties == null)
        {
            throw new IllegalArgumentException("properties cannot be null");
        }

        if(tokenProvider == null)
        {
            throw new IllegalArgumentException("tokenProvider cannot be null");
        }

        this.properties = properties;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public AuthenticationToken authenticate()
    {
        AuthenticationResult authResult = null; 
        
        try 
        { 
            authResult = tokenProvider.getAccessTokenBySecureRefreshToken(
                properties.getProperty(PropertyName.PARTNER_CENTER_ACCOUNT_ID), 
                "https://api.partnercenter.microsoft.com", 
                properties.getProperty(PropertyName.PARTNER_CENTER_ACCOUNT_ID), 
                properties.getProperty(PropertyName.PARTNER_CENTER_CLIENT_ID),
                properties.getProperty(PropertyName.PARTNER_CENTER_CLIENT_SECRET));
        }
        catch(ExecutionException ex)
        {
            ex.printStackTrace();
        }
        catch(InterruptedException ex)
        {
            ex.printStackTrace();
        }
        catch(MalformedURLException ex)
        {
            ex.printStackTrace();
        }

        if(authResult == null)
        {
            throw new NullPointerException("Unable to obtain an access token.");
        }

        return new AuthenticationToken(authResult.getAccessToken(), new DateTime(authResult.getExpiresOnDate()));
    }
}