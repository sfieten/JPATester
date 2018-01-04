/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
//@Entity
//@Table(name = "SLAVES")
@Embeddable
public class SlaveObject implements Serializable {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "OID")
//    public Long id;


    private MasterObject master;

    @Column
    private int     slaveId;

    @Column
    private String  name;

    @Column
    private String  attr;

    /**
     * @return the id
     */
    public int getId() {
        return slaveId;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.slaveId = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the attr
     */
    public String getAttr() {
        return attr;
    }

    /**
     * @param attr the attr to set
     */
    public void setAttr(String attr) {
        this.attr = attr;
    }

    public SlaveObject(int slaveId, String name, String attr) {
        this.slaveId = slaveId;
        this.name = name;
        this.attr = attr;
    }

    public SlaveObject() {
    }



}
