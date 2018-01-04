/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpatester;

import entities.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class JPATester {

    private static EntityManagerFactory emFactory;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        emFactory = Persistence.createEntityManagerFactory("jpatest");
        new JPATester().runTest();
    }

    public void runTest() throws InterruptedException {
        final int N = 1000;
        MasterObject[] masters = new MasterObject[N];
        for(int i = 0; i < N; i++) {
            masters[i] = new MasterObject("master_" + i);
            int S = new Random().nextInt(5);
            List<SlaveObject> slaves = new ArrayList<>(S);
            for(int j = 0; j < S; j++) {
                slaves.add(new SlaveObject(j, "slave_" + j, "something"));
            }
            masters[i].setSlaves(slaves);
        }
        System.out.println("Saving Masters");
        long startTime = new Date().getTime();
        Thread[] savers = new Thread[N];
        for(int i = 0; i < N; i++) {
            MasterObject m = masters[i];
            savers[i] = new Thread(() -> { new MasterSaver().saveMaster(m); });
            savers[i].start();
        }
        for(int i = 0; i < N; i++)
            savers[i].join();
        System.out.println("Saved Masters in " + (new Date().getTime() - startTime) + " ms");

        System.out.println("Replacing slaves");
        startTime = new Date().getTime();
        Thread[] replacers = new Thread[N];
        for(int i = 0; i < N; i++) {
            MasterObject m = masters[i];
            replacers[i] = new Thread(() -> { new SlaveReplacer().replaceSlaves(m); });
            replacers[i].start();
        }
        for(int i = 0; i < N; i++)
            replacers[i].join();
        System.out.println("Replaceed slaves in " + (new Date().getTime() - startTime) + " ms");

        System.out.println("Updating slaves");
        startTime = new Date().getTime();
        Thread[] updaters = new Thread[N];
        for(int i = 0; i < N; i++) {
            MasterObject m = masters[i];
            updaters[i] = new Thread(() -> { new SlaveUpdater().updateSlaves(m); });
            updaters[i].start();
        }
        for(int i = 0; i < N; i++)
            updaters[i].join();
        System.out.println("Updated slaves in " + (new Date().getTime() - startTime) + " ms");

    }


    class MasterSaver {
        void saveMaster(final MasterObject m) {
            EntityManager em = emFactory.createEntityManager();
            em.getTransaction().begin();
            em.persist(m);
            em.getTransaction().commit();
            em.close();
        }
    }

    class SlaveReplacer {
        void replaceSlaves(final MasterObject m) {
            List<SlaveObject> newSlaves = new ArrayList<>();
            for(SlaveObject s : m.getSlaves())
                newSlaves.add(new SlaveObject(s.getId(), s.getName(), "newAttr_" + new Random().nextInt()));
            EntityManager em = emFactory.createEntityManager();
            em.getTransaction().begin();
            MasterObject lm = em.find(MasterObject.class, m.oid);
            lm.setSlaves(newSlaves);
            em.persist(lm);
            em.getTransaction().commit();
            em.close();
        }
    }

    class SlaveUpdater {
        void updateSlaves(final MasterObject m) {
            EntityManager em = emFactory.createEntityManager();
            em.getTransaction().begin();
            MasterObject lm = em.find(MasterObject.class, m.oid);
            for(SlaveObject s : lm.getSlaves())
                s.setAttr("newAttr_" + new Random().nextInt());
            em.persist(lm);
            em.getTransaction().commit();
            em.close();
        }
    }

}
