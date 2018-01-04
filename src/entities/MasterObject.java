/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
@Entity
@Table(name="MSTR")
public class MasterObject implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OID")
    public Long oid;

    @Column
    private String name;

//	@OneToMany(mappedBy = "master", cascade = CascadeType.ALL)
    @ElementCollection
    @CollectionTable(name = "SLAVES")
	private List<SlaveObject> slaves;

    public void setSlaves(List<SlaveObject> newSlaves) {
        this.slaves = new ArrayList<>();
        newSlaves.forEach((s) -> this.slaves.add(s));
    }

    public List<SlaveObject> getSlaves() {
        return slaves;
    }

    public MasterObject(final String name) {
        this.name = name;
    }

    public MasterObject() {
    }


}
