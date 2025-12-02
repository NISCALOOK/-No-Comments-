package com.classmateai.backend.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "ClassMate AI";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    private GoogleAuthorizationCodeFlow flow;

    public String getAuthorizationUrl(String userId) throws GeneralSecurityException, IOException {
        GoogleAuthorizationCodeFlow flow = getFlow();
        return flow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .setState(userId)
                .build();
    }

    public Credential authorize(String authorizationCode, String userId) throws IOException, GeneralSecurityException {
        GoogleAuthorizationCodeFlow flow = getFlow();
        GoogleTokenResponse response = flow.newTokenRequest(authorizationCode)
                .setRedirectUri(redirectUri)
                .execute();
        return flow.createAndStoreCredential(response, userId);
    }

    public List<Event> getUpcomingEvents(String userId, int maxResults) throws GeneralSecurityException, IOException {
        Calendar service = getCalendarService(userId);
        DateTime now = new DateTime(System.currentTimeMillis());
        
        Events events = service.events().list("primary")
                .setMaxResults(maxResults)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        
        return events.getItems();
    }

    public Event createEvent(String userId, String summary, String description, 
                           LocalDateTime startDateTime, LocalDateTime endDateTime) 
            throws GeneralSecurityException, IOException {
        
        Calendar service = getCalendarService(userId);
        
        Event event = new Event()
                .setSummary(summary)
                .setDescription(description);

        EventDateTime start = new EventDateTime()
                .setDateTime(new DateTime(java.util.Date.from(
                        startDateTime.atZone(ZoneId.systemDefault()).toInstant())));
        event.setStart(start);

        EventDateTime end = new EventDateTime()
                .setDateTime(new DateTime(java.util.Date.from(
                        endDateTime.atZone(ZoneId.systemDefault()).toInstant())));
        event.setEnd(end);

        return service.events().insert("primary", event).execute();
    }

    public void deleteEvent(String userId, String eventId) throws GeneralSecurityException, IOException {
        Calendar service = getCalendarService(userId);
        service.events().delete("primary", eventId).execute();
    }

    private Calendar getCalendarService(String userId) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = getFlow().loadCredential(userId);
        
        if (credential == null) {
            throw new IOException("User not authorized. Please authorize first.");
        }
        
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private GoogleAuthorizationCodeFlow getFlow() throws GeneralSecurityException, IOException {
        if (flow == null) {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            
            GoogleClientSecrets.Details details = new GoogleClientSecrets.Details()
                    .setClientId(clientId)
                    .setClientSecret(clientSecret);
            
            GoogleClientSecrets clientSecrets = new GoogleClientSecrets()
                    .setInstalled(details);

            flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                    .setAccessType("offline")
                    .build();
        }
        return flow;
    }
}

