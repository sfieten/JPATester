/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
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
@Table(name = "SLAVES")
//@Embeddable
public class SlaveObject implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OID")
    public Long id;

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

    @Override
    public int hashCode() {
        return Objects.hash(slaveId, name, attr);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SlaveObject other = (SlaveObject) obj;
        if (this.slaveId != other.slaveId) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.attr, other.attr)) {
            return false;
        }
        return true;
    }

}
