package org.example;

import com.vaadin.annotations.Theme;
import com.vaadin.cdi.CDIUI;
import com.vaadin.event.FieldEvents;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import java.util.List;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;
import javax.inject.Inject;
import org.example.backend.PhoneBookEntry;
import org.example.backend.PhoneBookService;
import org.vaadin.viritin.fields.MTable;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 * This is a small tutorial application for Vaadin. It also uses Vaadin CDI (so
 * deploy to Java EE server) and a dependency collection for small Java EE +
 * Vaadin applications.
 *
 * Note, that this application is just to showcase Vaadin UI development and
 * some handy utilities. Pretty much whole application is just dumped into this
 * class. For larger apps where you strive for excellent testability and
 * maintainability, you most likely want to use better structured UI code. E.g.
 * google for "Vaadin MVP pattern".
 */
@CDIUI("")
@Theme("valo")
public class VaadinUI extends UI {

    @Inject
    PhoneBookService service;

    // Instantiate and configure a Table to list PhoneBookEntries
    MTable<PhoneBookEntry> entryList = new MTable<>(PhoneBookEntry.class)
            .withHeight("450px")
            .withFullWidth()
            .withProperties("name", "number")
            .withColumnHeaders("Name", "Phone number");

    TextField filter = new MTextField().withInputPrompt("filter...");

    @Override
    protected void init(VaadinRequest request) {

        // Add some event listners, e.g. to hook filter input to actually 
        // filter the displayed entries
        filter.addTextChangeListener((FieldEvents.TextChangeEvent e) -> {
            listEntries(e.getText());
        });

        setContent(
                new MVerticalLayout(
                        new Header("PhoneBook"),
                        filter,
                        new MHorizontalLayout(entryList)
                )
        );

        // List all entries and select first entry in the list
        listEntries();
        entryList.setValue(entryList.firstItemId());
    }

    private void listEntries(String filter) {
        // caching implemented in service using CDI interceptor
        entryList.setBeans(service.getEntries(filter));
        // Optionally do it with manual Java SE style API here
        // listEntriesJavaSEStyle(filter);
    }

    private void listEntriesJavaSEStyle(String filter) {
        final String name = "myCache";

        // Java SE style JCache usage
        Cache<String, List> cache = Caching.getCache(name, String.class,
                List.class);
        if (cache == null) {
            Notification.show("Creating cache",
                    Notification.Type.WARNING_MESSAGE);
            // You probably don't want to do things like this unless you are NOT
            // using Java EE or Spring. See TODO as well
            final CachingProvider cachingProvider = Caching.getCachingProvider();
            final CacheManager mgr = cachingProvider.getCacheManager();

            MutableConfiguration<String, List> config = new MutableConfiguration<>();
            config.setTypes(String.class, List.class);
            config.setStoreByValue(true);
            config.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(
                    Duration.ONE_MINUTE));
            cache = mgr.createCache(name, config);
        }

        // first look up from cache, if not found, go to service and cache value
        List cached = cache.get(filter);
        if (cached != null) {
            Notification.show("Cache hit!");
            entryList.setBeans(cached);
        } else {
            Notification.show("Cache missed :-(");
            List<PhoneBookEntry> entries = service.getEntries(filter);
            cache.put(filter, entries);
            entryList.setBeans(entries);
        }

    }

    private void listEntries() {
        listEntries(filter.getValue());
    }
    
}
