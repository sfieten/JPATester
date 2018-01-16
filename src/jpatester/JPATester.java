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

    private int LARGE = 1000;

    public void runTest() throws InterruptedException {
        final int N = 1000;
        MasterObject[] masters = new MasterObject[N];
        for(int i = 0; i < N; i++) {
            masters[i] = new MasterObject("master_" + i);
            int S = Integer.max(1, new Random().nextInt(5));
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

        MasterObject m = new MasterObject("many_slaves");
        List<SlaveObject> slaves = new ArrayList<>(LARGE);
        for(int j = 0; j < LARGE; j++)
            slaves.add(new SlaveObject(j, "upd_slave_" + j, "something_new"));
        m.setSlaves(slaves);

        System.out.println("Saving master with many slaves");
        startTime = new Date().getTime();
        new MasterSaver().saveMaster(m);
        System.out.println("Saved object in " + (new Date().getTime() - startTime) + " ms");

        // Now lets replace with a set with one less slave
        int R = new Random().nextInt(LARGE);
        List<SlaveObject> newSlaves = new ArrayList<>(LARGE-1);
        for(int j = 0; j < LARGE; j++)
            if (j != R)
                newSlaves.add(new SlaveObject(j, "slave_" + j, "something"));

        System.out.println("Replacing set of slaves (-1)");
        startTime = new Date().getTime();
        EntityManager em = emFactory.createEntityManager();
        em.getTransaction().begin();
        MasterObject rm = em.find(MasterObject.class, m.oid);

        rm.setSlaves(newSlaves);
        em.persist(rm);
        em.getTransaction().commit();
        em.close();
        System.out.println("Saved object in " + (new Date().getTime() - startTime) + " ms");

        // Add slave object to get to 1000 slaves again
        em = emFactory.createEntityManager();
        em.getTransaction().begin();
        rm = em.find(MasterObject.class, m.oid);
        rm.getSlaves().add(new SlaveObject(2*LARGE, "slave_added", null));
        em.persist(rm);
        em.getTransaction().commit();
        em.close();

        // Now update the set by removing one slave
        R = new Random().nextInt(LARGE-1);
        System.out.println("Removing one slave");
        startTime = new Date().getTime();
        em = emFactory.createEntityManager();
        em.getTransaction().begin();
        rm = em.find(MasterObject.class, m.oid);
        boolean removed = false;
        for(int j = 0; j < rm.getSlaves().size() && !removed; j++)
            if (rm.getSlaves().get(j).getId() == R) {
                rm.getSlaves().remove(j);
                removed = true;
            }
        em.persist(rm);
        em.getTransaction().commit();
        em.close();
        System.out.println("Removed slave object in " + (new Date().getTime() - startTime) + " ms");
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
            int R = new Random().nextInt(lm.getSlaves().size());
            lm.getSlaves().get(R).setAttr("newAttr_" + new Random().nextInt());
//            for(SlaveObject s : lm.getSlaves())
//                s.setAttr("newAttr_" + new Random().nextInt());
            em.persist(lm);
            em.getTransaction().commit();
            em.close();
        }
    }

}
