package org.example.backend;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.cache.Cache;
import javax.cache.annotation.CacheResult;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * A very simple in memory service to provide access to PhoneBook entries.
 *
 * This example uses ApplicationScoped CDI bean as "database", but in a real
 * world app, you'll probably have something similar both with some real
 * persistency.
 */
@ApplicationScoped
public class PhoneBookService {

    private final Set<PhoneBookEntry> entries = new HashSet<>();

    public PhoneBookService() {
    }

    public void save(PhoneBookEntry entry) {
        entries.add(entry);
    }

    // Makes CDI container check and maintain cache before entering
    // the business logic
    @CacheResult
    public List<PhoneBookEntry> getEntries(String filter) {
        try {
            // To make it visible with log message and latency 
            // that the cache was missed...
            System.out.println("Cache missed!");
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(PhoneBookService.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        
        return entries.stream()
                .filter(e -> filter == null || e.getName().contains(filter))
                .sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
                .collect(Collectors.toList());
    }

    public void delete(PhoneBookEntry value) {
        entries.remove(value);
    }

    private static String[] names = new String[]{"Younker Patel", "Zollicoffer Robinson", "Zeh Haugen", "Yager Johansen", "Zickefoose Macdonald", "Yerkes Karlsson", "Yerby Gustavsson", "Zimple Svensson", "Youmans Stewart", "Zahn Davis", "Zenz Davis", "Zamastil Jackson", "Zamastil Gustavsson", "Zucchero Walker", "Zielke Martin", "Zabowski Carlsson", "Yoes Hansson", "Zuczek Smith", "Zeidler Watson", "Yingling Harris", "Zahn Karlsen", "Zimmermann Olsson", "Zerkey Martin", "Zatovich Andersson", "Yurky Andersson", "Yeary Carlsson", "Yeary Olsen", "Zabowski Olsen", "Zuber Jackson", "Zeim Nilsen"};

    @PostConstruct
    public void init() {
        Random r = new Random(0);
        for (String name : names) {
            String[] split = name.split(" ");
            entries.add(new PhoneBookEntry(name, "+ 358 555 " + (100 + r.
                    nextInt(900)), split[0].toLowerCase() + "@" + split[1].
                    toLowerCase() + ".com"));
        }
    }

}
